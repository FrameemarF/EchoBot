package com.example.translatorbot.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class BotConfigTest {

    private Properties validProperties() {
        Properties props = new Properties();
        props.setProperty("telegram.bot.token", "test-token");
        props.setProperty("telegram.bot.username", "test_bot");
        props.setProperty("translation.api.url", "https://api.example.com/translate");
        return props;
    }

    @Test
    void shouldLoadRequiredProperties() {
        BotConfig config = new BotConfig(validProperties());

        assertEquals("test-token", config.getBotToken());
        assertEquals("test_bot", config.getBotUsername());
        assertEquals("https://api.example.com/translate", config.getTranslationApiUrl());
    }

    @Test
    void shouldUseDefaultMaxLengthWhenNotSpecified() {
        BotConfig config = new BotConfig(validProperties());

        assertEquals(500, config.getTranslationMaxLength());
    }

    @Test
    void shouldUseCustomMaxLength() {
        Properties props = validProperties();
        props.setProperty("translation.max-length", "1000");

        BotConfig config = new BotConfig(props);

        assertEquals(1000, config.getTranslationMaxLength());
    }

    @Test
    void shouldThrowWhenTokenMissing() {
        Properties props = validProperties();
        props.remove("telegram.bot.token");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> new BotConfig(props)
        );
        assertEquals("Missing property: telegram.bot.token", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUsernameMissing() {
        Properties props = validProperties();
        props.remove("telegram.bot.username");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> new BotConfig(props)
        );
        assertEquals("Missing property: telegram.bot.username", ex.getMessage());
    }

    @Test
    void shouldThrowWhenApiUrlMissing() {
        Properties props = validProperties();
        props.remove("translation.api.url");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> new BotConfig(props)
        );
        assertEquals("Missing property: translation.api.url", ex.getMessage());
    }

    @Test
    void shouldThrowWhenRequiredPropertyIsBlank() {
        Properties props = validProperties();
        props.setProperty("telegram.bot.token", "   ");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> new BotConfig(props)
        );
        assertEquals("Missing property: telegram.bot.token", ex.getMessage());
    }

    @Test
    void shouldThrowWhenMaxLengthIsNotNumber() {
        Properties props = validProperties();
        props.setProperty("translation.max-length", "abc");

        assertThrows(NumberFormatException.class, () -> new BotConfig(props));
    }
}