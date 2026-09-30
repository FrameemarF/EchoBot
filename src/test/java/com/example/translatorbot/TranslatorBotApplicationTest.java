package com.example.translatorbot;

import com.example.translatorbot.bot.TranslatorBotService;
import com.example.translatorbot.config.BotConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TranslatorBotApplicationTest {

    private TranslatorBotApplication app;

    @BeforeEach
    void setUp() {
        app = new TranslatorBotApplication();
    }

    @Test
    @DisplayName("loadProperties: загружает существующий файл из classpath")
    void loadProperties_existingFile_returnsProperties() throws Exception {
        Properties props = app.loadProperties("/test-config.properties");

        assertEquals("bar", props.getProperty("foo"));
        assertEquals("42", props.getProperty("answer"));
    }

    @Test
    @DisplayName("loadProperties: бросает IllegalStateException, если файла нет")
    void loadProperties_missingFile_throwsIllegalState() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> app.loadProperties("/nonexistent.properties"));

        assertTrue(ex.getMessage().contains("/nonexistent.properties"));
    }

    @Test
    @DisplayName("createBot: собирает бота из конфигурации")
    void createBot_validConfig_returnsBot() {
        BotConfig config = mock(BotConfig.class);
        when(config.getBotToken()).thenReturn("test-token");
        when(config.getTranslationApiUrl()).thenReturn("http://localhost:8080/translate");

        TranslatorBotService bot = app.createBot(config);

        assertNotNull(bot);
        verify(config, atLeastOnce()).getBotToken();
        verify(config, atLeastOnce()).getTranslationApiUrl();
    }

    @Test
    @DisplayName("run: регистрирует бота в Telegram с токеном из конфига")
    void run_registersBotWithToken() throws Exception {
        TelegramBotsLongPollingApplication polling = mock(TelegramBotsLongPollingApplication.class);
        TranslatorBotApplication spyApp = spy(app);
        doReturn(polling).when(spyApp).createPollingApplication();

        spyApp.run("/test-config.properties");

        verify(polling).registerBot(eq("test-token"), any(TranslatorBotService.class));
        verifyNoMoreInteractions(polling);
    }

    @Test
    @DisplayName("run: при отсутствии конфига бросает исключение и ничего не регистрирует")
    void run_missingConfig_doesNotRegisterBot() throws Exception {
        TelegramBotsLongPollingApplication polling = mock(TelegramBotsLongPollingApplication.class);
        TranslatorBotApplication spyApp = spy(app);
        doReturn(polling).when(spyApp).createPollingApplication();

        assertThrows(IllegalStateException.class, () -> spyApp.run("/nonexistent.properties"));

        verifyNoInteractions(polling);
    }
}