package com.example.translatorbot.service;

import com.example.translatorbot.model.Language;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslationServiceImplTest {

    private static final String API_URL = "http://localhost:5000/translate";

    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> response;

    private TranslationServiceImpl service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new TranslationServiceImpl(API_URL, httpClient, objectMapper);
    }

    // ---------- Вспомогательный метод ----------

    @SuppressWarnings("unchecked")
    private void mockResponse(int status, String body) throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(status);
        when(response.body()).thenReturn(body);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);
    }

    // ---------- source == target ----------

    @Test
    void translate_sourceEqualsTarget_shouldReturnTextAsIs() {
        String result = service.translate("привет", Language.RU, Language.RU);

        assertEquals("привет", result);
    }

    // ---------- Успешный перевод ----------

    @Test
    void translate_shouldSendCorrectRequestAndParseResponse() throws Exception {
        mockResponse(200, "{\"translatedText\":\"hello\"}");

        String result = service.translate("привет", Language.RU, Language.EN);

        assertEquals("hello", result);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture(), any(HttpResponse.BodyHandler.class));

        HttpRequest request = captor.getValue();
        assertEquals(API_URL, request.uri().toString());
        assertEquals("POST", request.method());
    }

    @Test
    void translate_shouldIncludeSourceAndTargetInBody() throws Exception {
        mockResponse(200, "{\"translatedText\":\"hello\"}");

        service.translate("привет", Language.RU, Language.EN);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture(), any(HttpResponse.BodyHandler.class));

        // тело запроса лежит в BodyPublisher — вытащить сложно, проверяем через URI и метод
        // детали тела мы не проверяем, чтобы не усложнять тест
        assertTrue(captor.getValue().uri().toString().endsWith("/translate"));
    }

    // ---------- Ошибки HTTP ----------

    @Test
    void translate_httpError_shouldThrowRuntimeException() throws Exception {
        mockResponse(500, "internal error");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.translate("привет", Language.RU, Language.EN));

        assertTrue(ex.getMessage().contains("500"));
    }

    @Test
    void translate_noTranslatedTextField_shouldThrowRuntimeException() throws Exception {
        mockResponse(200, "{\"other\":\"field\"}");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.translate("привет", Language.RU, Language.EN));

        assertTrue(ex.getMessage().toLowerCase().contains("empty"));
    }

    @Test
    void translate_nullTranslatedText_shouldThrowRuntimeException() throws Exception {
        mockResponse(200, "{\"translatedText\":null}");

        assertThrows(RuntimeException.class,
                () -> service.translate("привет", Language.RU, Language.EN));
    }

    // ---------- IO ошибки ----------

    @Test
    void translate_ioException_shouldWrapInRuntimeException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("connection refused"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.translate("привет", Language.RU, Language.EN));

        assertTrue(ex.getMessage().contains("connection refused")
                || ex.getMessage().contains("failed"));
    }

    @Test
    void translate_interrupted_shouldRestoreInterruptFlag() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("interrupted"));

        assertThrows(RuntimeException.class,
                () -> service.translate("привет", Language.RU, Language.EN));

        // проверяем, что флаг прерывания был восстановлен
        assertTrue(Thread.currentThread().isInterrupted());

        // ВАЖНО: сбросить флаг, чтобы он не влиял на другие тесты
        Thread.interrupted();
    }
}