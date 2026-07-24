package com.placenextai.client;

import com.placenextai.dto.AiResumeAnalysis;
import com.placenextai.exception.AiServiceException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class ResumeAiClient {

    // Bump this whenever the file changes: the marker below proves in the
    // startup log which version of this class is actually running.
    private static final String CLIENT_VERSION = "v3-simple-http11";
    private static final String LOCALHOST_DEFAULT = "http://localhost:8000";

    private final RestClient restClient;
    private final String baseUrl;
    private final Environment environment;

    public ResumeAiClient(@Value("${app.ai-service.base-url}") String baseUrl, Environment environment) {
        this.baseUrl = baseUrl;
        this.environment = environment;
        // HttpURLConnection-based factory: speaks plain HTTP/1.1 only.
        // It cannot send the h2c upgrade that was corrupting the multipart
        // body when the JDK HttpClient negotiated with uvicorn.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    // Same reasoning as CorsConfig's startup log: a misconfigured
    // AI_SERVICE_BASE_URL is otherwise invisible until a real resume upload
    // fails with a message written for local dev.
    @PostConstruct
    public void announce() {
        log.info(">>> ResumeAiClient {} loaded - plain HTTP/1.1 transport active <<<", CLIENT_VERSION);
        log.info("AI service base URL resolved to: {}", baseUrl);
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        if (isProd && LOCALHOST_DEFAULT.equals(baseUrl.trim())) {
            log.warn(
                    "AI_SERVICE_BASE_URL is not set and the 'prod' profile is active - " +
                            "falling back to the local-dev default ({}). Resume analysis will " +
                            "fail for every request until AI_SERVICE_BASE_URL is set to the " +
                            "deployed AI service's actual URL.",
                    LOCALHOST_DEFAULT);
        }
    }

    public AiResumeAnalysis analyze(MultipartFile file, String jobDescription) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() == null ? "resume.pdf" : file.getOriginalFilename();
                }
            };
            body.add("file", resource);
        } catch (IOException exception) {
            throw new AiServiceException("Could not read the uploaded file.");
        }
        if (jobDescription != null && !jobDescription.isBlank()) {
            body.add("job_description", jobDescription);
        }

        try {
            return restClient.post()
                    .uri("/api/v1/resume/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(AiResumeAnalysis.class);
        } catch (RestClientResponseException exception) {
            log.warn("AI service returned {} - body: {}", exception.getStatusCode().value(),
                    exception.getResponseBodyAsString());
            throw new AiServiceException(extractDetail(exception));
        } catch (Exception exception) {
            log.warn("AI service unreachable at {}: {}", baseUrl, exception.getMessage());
            boolean usingLocalDefault = LOCALHOST_DEFAULT.equals(baseUrl.trim());
            throw new AiServiceException(usingLocalDefault
                    ? "The AI service is not reachable at " + baseUrl + " (local dev default). "
                            + "If this is a deployed environment, set AI_SERVICE_BASE_URL and redeploy - "
                            + "otherwise start it locally with: uvicorn app.main:app --port 8000"
                    : "The AI service at " + baseUrl + " is not reachable. It may still be waking up "
                            + "from sleep on a free hosting tier - try again in a moment, or verify it "
                            + "is running.");
        }
    }

    private String extractDetail(RestClientResponseException exception) {
        String bodyText = exception.getResponseBodyAsString();
        int key = bodyText.indexOf("\"detail\"");
        if (key >= 0) {
            int colon = bodyText.indexOf(':', key);
            if (colon > 0 && colon + 2 < bodyText.length()) {
                char first = bodyText.charAt(colon + 1) == ' ' ? bodyText.charAt(colon + 2) : bodyText.charAt(colon + 1);
                if (first == '"') {
                    int start = bodyText.indexOf('"', colon);
                    int end = bodyText.indexOf('"', start + 1);
                    if (end > start) {
                        return bodyText.substring(start + 1, end);
                    }
                }
            }
        }
        return "The AI service rejected the resume (HTTP " + exception.getStatusCode().value() + ").";
    }
}
