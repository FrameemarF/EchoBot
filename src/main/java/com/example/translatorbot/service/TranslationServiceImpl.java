package com.example.translatorbot.service;

import com.example.translatorbot.model.Language;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class TranslationServiceImpl implements TranslationService {

    private final Logger log = LoggerFactory.getLogger(TranslationServiceImpl.class);

    private final String apiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TranslationServiceImpl(String apiUrl) {
        this.apiUrl = apiUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // для тестов
    TranslationServiceImpl(String apiUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.apiUrl = apiUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Переводит текст с исходного языка на целевой через внешний сервис.
     * Если исходный и целевой языки совпадают, возвращает текст без изменений.
     *
     * @param text   текст для перевода
     * @param source язык, с которого переводим
     * @param target язык, на который переводим
     * @return переведённый текст
     * @throws RuntimeException если сервис перевода недоступен или вернул ошибку
     */
    @Override
    public String translate(String text, Language source, Language target) {
        try {
            if (source.getCode().equalsIgnoreCase(target.getCode())) {
                log.info("Source and target languages are the same ('{}') — returning as-is",
                        target.getCode());
                return text;
            }

            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "q", text,
                    "source", source.getCode(),
                    "target", target.getCode(),
                    "format", "text"
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("LibreTranslate returned status {}, body: {}",
                        response.statusCode(), response.body());
                throw new RuntimeException(
                        "Translation service returned HTTP " + response.statusCode());
            }

            JsonNode json = objectMapper.readTree(response.body());
            JsonNode translated = json.get("translatedText");

            if (translated == null || translated.isNull()) {
                log.error("LibreTranslate response has no 'translatedText': {}",
                        response.body());
                throw new RuntimeException("Translation service returned empty result");
            }

            return translated.asText();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Translation request was interrupted", e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Translation request failed: " + e.getMessage(), e);
        }
    }
}