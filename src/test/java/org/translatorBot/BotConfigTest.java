package org.translatorBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Тесты загрузки {@link BotConfig} из {@code src/test/resources/application.properties}.
 * <p>
 * Если в системе заданы переменные окружения {@code BOT_TOKEN},
 * {@code LIBRETRANSLATE_URL} или {@code LIBRETRANSLATE_API_KEY}, они имеют приоритет
 * над файлом, поэтому в этом случае все тесты пропускаются.
 */
class BotConfigTest {

    /**
     * Пропускает тесты, если установлены переменные окружения, перекрывающие
     * значения из {@code application.properties}.
     */
    @BeforeEach
    void requireCleanEnvironment() {
        Assumptions.assumeTrue(System.getenv("BOT_TOKEN") == null,
                "BOT_TOKEN is set, skipping");
        Assumptions.assumeTrue(System.getenv("LIBRETRANSLATE_URL") == null,
                "LIBRETRANSLATE_URL is set, skipping");
        Assumptions.assumeTrue(System.getenv("LIBRETRANSLATE_API_KEY") == null,
                "LIBRETRANSLATE_API_KEY is set, skipping");
    }

    /** Токен бота читается из свойства {@code bot.token}. */
    @Test
    void readsTokenFromProperties() {
        Assertions.assertEquals("test-token", new BotConfig().getBotToken());
    }

    /** URL LibreTranslate читается из свойства {@code libretranslate.url}. */
    @Test
    void readsLibreTranslateUrlFromProperties() {
        Assertions.assertEquals("http://test-libre:5000/", new BotConfig().getLibreTranslateUrl());
    }

    /** API-ключ читается из свойства {@code libretranslate.api-key}. */
    @Test
    void readsApiKeyFromProperties() {
        Assertions.assertEquals("test-key", new BotConfig().getLibreTranslateApiKey());
    }
}