package com.placenextai.service.impl;

import com.placenextai.entity.InterviewQuestionCategory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Rule-based interview question generator: no external AI call, just a curated
 * bank keyed by skill plus a behavioral pool, with multiple phrasing variants
 * per skill so back-to-back interviews don't feel identical. {@link
 * InterviewServiceImpl} scores answers with a keyword/length heuristic that
 * this bank supplies expected keywords for.
 */
@Component
public class InterviewQuestionBank {

    public record GeneratedQuestion(InterviewQuestionCategory category, String text, List<String> keywords) {
    }

    private static final List<GeneratedQuestion> BEHAVIORAL_POOL = List.of(
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Tell me about yourself and your journey so far.",
                    List.of("experience", "skills", "project", "background", "learn")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Walk me through your resume, focusing on what led you to this field.",
                    List.of("experience", "background", "interest", "project", "growth")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Describe a challenging project you worked on and how you overcame the obstacles.",
                    List.of("challenge", "problem", "solution", "team", "overcame")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Tell me about a time something went wrong on a project and how you handled it.",
                    List.of("problem", "issue", "fix", "debug", "resolve")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Tell me about a time you worked effectively as part of a team.",
                    List.of("team", "collaborate", "communication", "conflict", "goal")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Describe a disagreement you had with a teammate and how you resolved it.",
                    List.of("conflict", "disagree", "communication", "compromise", "resolve")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "How do you prioritize tasks when working under a tight deadline?",
                    List.of("priority", "deadline", "time", "plan", "manage")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Describe a time you had to juggle multiple responsibilities at once.",
                    List.of("priority", "manage", "balance", "time", "organize")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "What is your greatest strength, and how does it help you professionally?",
                    List.of("strength", "skill", "example", "result", "impact")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "What do you consider your biggest weakness, and how are you improving it?",
                    List.of("weakness", "improve", "learn", "feedback", "growth")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Describe a mistake you made and what you learned from it.",
                    List.of("mistake", "learn", "improve", "responsibility", "feedback")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Tell me about a time you received critical feedback. How did you respond?",
                    List.of("feedback", "respond", "improve", "listen", "change")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Where do you see yourself in the next few years?",
                    List.of("goal", "growth", "career", "learn", "future")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "What motivates you to do your best work?",
                    List.of("motivate", "passion", "goal", "drive", "interest")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Describe a time you had to learn a new tool or technology quickly.",
                    List.of("learn", "adapt", "quickly", "documentation", "practice")),
            new GeneratedQuestion(InterviewQuestionCategory.BEHAVIORAL,
                    "Tell me about a project you led or took initiative on.",
                    List.of("lead", "initiative", "ownership", "plan", "result"))
    );

    private static final Map<String, List<GeneratedQuestion>> TECHNICAL_BANK = new LinkedHashMap<>();

    static {
        put("java",
                q("Explain the difference between an abstract class and an interface in Java, and when you'd use each.",
                        "abstract", "interface", "class", "inherit", "method", "implement"),
                q("How does the JVM manage memory, and what causes a memory leak in Java?",
                        "heap", "stack", "garbage", "memory", "reference"));
        put("python",
                q("How does Python's garbage collection work, and what are some best practices for memory-efficient code?",
                        "memory", "garbage", "reference", "object", "efficient"),
                q("What's the difference between a list, a tuple, and a dictionary in Python, and when would you use each?",
                        "list", "tuple", "dictionary", "mutable", "key"));
        put("javascript",
                q("Explain the difference between var, let, and const, and what closures are in JavaScript.",
                        "scope", "closure", "var", "let", "const", "function"),
                q("What is the event loop in JavaScript, and how does it handle asynchronous code?",
                        "event", "loop", "async", "callback", "queue"));
        put("typescript",
                q("What advantages does TypeScript give you over plain JavaScript?",
                        "type", "interface", "compile", "safety", "error"));
        put("react",
                q("How do React hooks like useState and useEffect work, and why were they introduced?",
                        "hook", "state", "effect", "component", "render"),
                q("What is the virtual DOM in React, and how does it improve performance?",
                        "virtual", "dom", "render", "diff", "performance"));
        put("angular",
                q("What is dependency injection in Angular, and why does the framework rely on it so heavily?",
                        "dependency", "injection", "service", "module", "component"));
        put("vue",
                q("How does Vue's reactivity system detect and respond to data changes?",
                        "reactive", "data", "watch", "component", "render"));
        put("node.js",
                q("How does Node.js handle asynchronous operations under the hood?",
                        "async", "event", "loop", "callback", "promise"));
        put("spring boot",
                q("How does dependency injection work in Spring Boot, and why is it useful?",
                        "dependency", "injection", "bean", "spring", "container"),
                q("What's the difference between @Component, @Service, and @Repository in Spring?",
                        "component", "service", "repository", "bean", "layer"));
        put("sql",
                q("What's the difference between an INNER JOIN and a LEFT JOIN? Give an example.",
                        "join", "table", "inner", "left", "query"),
                q("How would you find and fix a slow SQL query?",
                        "index", "query", "optimize", "explain", "performance"));
        put("dbms",
                q("Explain database normalization and why it matters.",
                        "normalization", "redundancy", "table", "key", "schema"));
        put("mongodb",
                q("When would you choose MongoDB over a relational database like MySQL?",
                        "document", "schema", "nosql", "scale", "flexible"));
        put("data structures",
                q("When would you use a hash map versus a binary search tree?",
                        "hash", "tree", "lookup", "complexity", "structure"),
                q("How does a linked list differ from an array, and what are the tradeoffs?",
                        "array", "linked", "pointer", "memory", "insert"));
        put("algorithms",
                q("How would you approach optimizing an algorithm that's running too slowly?",
                        "complexity", "optimize", "time", "space", "approach"),
                q("Explain the difference between BFS and DFS, and when you'd use each.",
                        "breadth", "depth", "graph", "traverse", "queue"));
        put("machine learning",
                q("Walk me through how you'd approach a classification problem end-to-end.",
                        "model", "train", "data", "feature", "evaluate"),
                q("What's the difference between overfitting and underfitting, and how do you address each?",
                        "overfit", "underfit", "train", "generalize", "regularize"));
        put("deep learning",
                q("How does a neural network learn through backpropagation?",
                        "neural", "network", "gradient", "weight", "train"));
        put("nlp",
                q("How would you approach building a sentiment analysis model from raw text?",
                        "text", "tokenize", "model", "feature", "train"));
        put("aws",
                q("What AWS services would you use to deploy a scalable web application?",
                        "ec2", "s3", "scalable", "cloud", "deploy"));
        put("azure",
                q("How would you deploy and scale a web application on Azure?",
                        "app service", "scale", "cloud", "deploy", "resource"));
        put("docker",
                q("What problem does Docker solve, and how do containers differ from virtual machines?",
                        "container", "image", "virtual", "isolate", "deploy"));
        put("kubernetes",
                q("What role does Kubernetes play in managing containerized applications?",
                        "container", "orchestrate", "pod", "scale", "cluster"));
        put("git",
                q("Explain the difference between git merge and git rebase.",
                        "merge", "rebase", "branch", "commit", "history"));
        put("rest api",
                q("What makes an API RESTful, and how do you handle versioning?",
                        "rest", "endpoint", "http", "resource", "version"));
        put("graphql",
                q("How does GraphQL differ from a traditional REST API?",
                        "query", "schema", "resolver", "endpoint", "flexible"));
        put("microservices",
                q("What are the tradeoffs of a microservices architecture compared to a monolith?",
                        "service", "scale", "deploy", "communication", "complexity"));
        put("system design",
                q("How would you design a URL-shortening service like bit.ly?",
                        "scale", "database", "design", "cache", "load"),
                q("How would you design a rate limiter for a public API?",
                        "rate", "limit", "throttle", "cache", "scale"));
        put("html",
                q("How do semantic HTML elements improve accessibility and SEO?",
                        "semantic", "accessibility", "seo", "element", "structure"));
        put("css",
                q("Explain the CSS box model and how flexbox differs from grid.",
                        "box", "flexbox", "grid", "layout", "css"));
        put("c++",
                q("What is the difference between stack and heap memory allocation in C++?",
                        "stack", "heap", "memory", "pointer", "allocate"));
        put("c",
                q("How does manual memory management in C differ from garbage-collected languages?",
                        "pointer", "malloc", "free", "memory", "allocate"));
        put("oop",
                q("Explain the four pillars of object-oriented programming with examples.",
                        "encapsulation", "inheritance", "polymorphism", "abstraction", "class"));
        put("php",
                q("How does PHP handle sessions and state across requests in a web application?",
                        "session", "state", "request", "server", "cookie"));
        put("kotlin",
                q("What advantages does Kotlin offer over Java for Android development?",
                        "null", "safety", "concise", "coroutine", "interop"));
        put("android",
                q("How does the Android activity lifecycle affect how you manage app state?",
                        "lifecycle", "activity", "state", "onresume", "onpause"));
        put("flutter",
                q("How does Flutter achieve consistent UI rendering across platforms?",
                        "widget", "render", "cross-platform", "state", "build"));
        put("devops",
                q("What does a typical CI/CD pipeline look like, and what problems does it solve?",
                        "pipeline", "build", "deploy", "automate", "test"));
        put("testing",
                q("What's the difference between unit tests, integration tests, and end-to-end tests?",
                        "unit", "integration", "test", "mock", "coverage"));
        put("linux",
                q("How would you investigate a process that's consuming too much CPU on a Linux server?",
                        "process", "cpu", "monitor", "command", "debug"));
        put("data analysis",
                q("Walk me through how you'd clean and explore a messy real-world dataset.",
                        "clean", "data", "explore", "missing", "analyze"));
        put("excel",
                q("What Excel features do you rely on most for analyzing large datasets?",
                        "pivot", "formula", "data", "analyze", "chart"));
        put("power bi",
                q("How would you design a dashboard that clearly communicates key business metrics?",
                        "dashboard", "metric", "visualize", "data", "report"));
        put("tableau",
                q("How do you decide which chart type best represents a given dataset?",
                        "chart", "visualize", "data", "dashboard", "insight"));
    }

    private static void put(String skill, GeneratedQuestion... variants) {
        TECHNICAL_BANK.put(skill, List.of(variants));
    }

    private static GeneratedQuestion q(String text, String... keywords) {
        return new GeneratedQuestion(InterviewQuestionCategory.TECHNICAL, text, List.of(keywords));
    }

    private final Random random = new Random();

    public List<GeneratedQuestion> generate(Set<String> studentSkills, String targetCompany, Set<String> companyRequiredSkills) {
        List<GeneratedQuestion> questions = new ArrayList<>();

        questions.add(pickVariant(BEHAVIORAL_POOL.subList(0, 2))); // Vary the opener between the two "about you" questions.

        boolean hasCompany = targetCompany != null && !targetCompany.isBlank() && !companyRequiredSkills.isEmpty();

        if (hasCompany) {
            List<String> matched = companyRequiredSkills.stream()
                    .filter(skill -> containsIgnoreCase(studentSkills, skill))
                    .limit(3)
                    .toList();
            List<String> missing = companyRequiredSkills.stream()
                    .filter(skill -> !containsIgnoreCase(studentSkills, skill))
                    .limit(2)
                    .toList();

            matched.forEach(skill -> questions.add(technicalQuestionFor(skill)));
            missing.forEach(skill -> questions.add(gapQuestionFor(skill, targetCompany)));

            questions.add(new GeneratedQuestion(InterviewQuestionCategory.COMPANY_FIT,
                    "Why do you want to work at " + targetCompany + ", and what do you know about the role?",
                    List.of("company", targetCompany.toLowerCase(), "role", "product", "growth")));
        } else {
            List<String> skillsToAsk = studentSkills.stream().limit(4).toList();
            if (skillsToAsk.isEmpty()) {
                questions.add(new GeneratedQuestion(InterviewQuestionCategory.TECHNICAL,
                        "Tell me about a technical project you're proud of and the tools you used.",
                        List.of("project", "tool", "build", "result", "learn")));
            } else {
                skillsToAsk.forEach(skill -> questions.add(technicalQuestionFor(skill)));
            }
        }

        questions.add(pickClosingBehavioral());

        return questions;
    }

    private GeneratedQuestion technicalQuestionFor(String skill) {
        List<GeneratedQuestion> variants = TECHNICAL_BANK.get(skill.toLowerCase().trim());
        if (variants != null && !variants.isEmpty()) {
            return pickVariant(variants);
        }
        return new GeneratedQuestion(InterviewQuestionCategory.TECHNICAL,
                "Walk me through a project where you used " + skill + ", including a challenge you faced.",
                List.of(skill.toLowerCase(), "project", "challenge", "implement", "result"));
    }

    private GeneratedQuestion gapQuestionFor(String skill, String company) {
        return new GeneratedQuestion(InterviewQuestionCategory.COMPANY_FIT,
                company + " lists " + skill + " as a requirement for this role - how would you go about "
                        + "getting up to speed on it quickly?",
                List.of("learn", "practice", "course", "documentation", "project", "plan"));
    }

    private GeneratedQuestion pickClosingBehavioral() {
        List<GeneratedQuestion> rest = BEHAVIORAL_POOL.subList(2, BEHAVIORAL_POOL.size());
        return pickVariant(rest);
    }

    private GeneratedQuestion pickVariant(List<GeneratedQuestion> variants) {
        return variants.get(random.nextInt(variants.size()));
    }

    private boolean containsIgnoreCase(Set<String> haystack, String needle) {
        return haystack.stream().anyMatch(item -> item.equalsIgnoreCase(needle));
    }
}
