package com.placenextai.client;

import com.placenextai.dto.AiResumeAnalysis;
import com.placenextai.exception.AiServiceException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Component
public class ResumeAiClient {

    // Bump this whenever the file changes: the marker below proves in the
    // startup log which version of this class is actually running.
    private static final String CLIENT_VERSION = "v3-simple-http11";

    private final RestClient restClient;

    public ResumeAiClient(@Value("${app.ai-service.base-url}") String baseUrl) {
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

    @PostConstruct
    public void announce() {
        log.info(">>> ResumeAiClient {} loaded - plain HTTP/1.1 transport active <<<", CLIENT_VERSION);
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
            log.warn("AI service unreachable: {}", exception.getMessage());
            throw new AiServiceException(
                    "The AI service is not reachable. Start it with: uvicorn app.main:app --port 8000");
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
