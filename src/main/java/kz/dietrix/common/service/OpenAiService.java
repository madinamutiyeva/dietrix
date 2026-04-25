package kz.dietrix.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.dietrix.common.config.OpenAiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.SocketException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OpenAiService {

    private final WebClient webClient;
    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper;

    public OpenAiService(WebClient.Builder webClientBuilder, OpenAiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String chat(String systemPrompt, String userMessage) {
        return chat(systemPrompt, userMessage, 2000);
    }

    /**
     * Chat with OpenAI. Forces JSON output via response_format.
     */
    public String chat(String systemPrompt, String userMessage, int maxTokens) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", properties.getModel());
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("response_format", Map.of("type", "json_object"));

        return executeChatRequest(requestBody);
    }

    public String chatWithHistory(String systemPrompt, List<Map<String, String>> messages) {
        List<Map<String, String>> allMessages = new java.util.ArrayList<>();
        allMessages.add(Map.of("role", "system", "content", systemPrompt));
        allMessages.addAll(messages);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", properties.getModel());
        requestBody.put("messages", allMessages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);
        // NOTE: no response_format here — assistant chat returns plain text.

        return executeChatRequest(requestBody);
    }

    // ─── Shared executor with proper retry handling ───────────────────────────

    private String executeChatRequest(Map<String, Object> requestBody) {
        try {
            String response = webClient.post()
                    .uri("/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(90))
                    .retryWhen(Retry.backoff(4, Duration.ofSeconds(2))
                            .maxBackoff(Duration.ofSeconds(15))
                            .jitter(0.5)
                            .filter(this::isRetryable)
                            .doBeforeRetry(signal -> log.warn(
                                    "Retrying OpenAI request (attempt {}): {}",
                                    signal.totalRetries() + 1,
                                    rootCauseMessage(signal.failure())))
                            .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            log.debug("OpenAI response length: {} chars", content.length());
            return content;
        } catch (Exception e) {
            log.error("OpenAI API call failed: {}", rootCauseMessage(e));
            throw new RuntimeException("Failed to get AI response: " + rootCauseMessage(e));
        }
    }

    /**
     * Walks the full cause chain, since Reactor wraps the original network error
     * into WebClientRequestException / PrematureCloseException / etc.
     * Also retries on 408/429/5xx.
     */
    private boolean isRetryable(Throwable ex) {
        // HTTP-level: retry transient server errors and rate limits
        if (ex instanceof WebClientResponseException wcre) {
            HttpStatusCode status = wcre.getStatusCode();
            int code = status.value();
            return code == 408 || code == 429 || (code >= 500 && code <= 599);
        }
        // Reactor wraps network errors — unwrap the whole cause chain.
        Throwable t = ex;
        for (int i = 0; i < 10 && t != null; i++) {
            String name = t.getClass().getName();
            if (t instanceof IOException
                    || t instanceof SocketException
                    || t instanceof TimeoutException
                    || t instanceof WebClientRequestException
                    || name.contains("PrematureCloseException")
                    || name.contains("ReadTimeoutException")
                    || name.contains("WriteTimeoutException")) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    private static String rootCauseMessage(Throwable ex) {
        Throwable t = ex;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }
}
