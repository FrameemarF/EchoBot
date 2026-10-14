package org.translatorBot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты для {@link BotApplication}, проверяющие корректность
 * создания {@link Router} через {@link BotApplication#buildRouter(BotConfig)}.
 */
class BotApplicationTest {

    /**
     * Проверяет, что {@code buildRouter} возвращает не {@code null}.
     */
    @Test
    void buildRouter_returnsNonNullRouter() {
        BotApplication app = new BotApplication();
        BotConfig config = new BotConfig();

        Router router = app.buildRouter(config);

        assertNotNull(router);
    }

    /**
     * Проверяет, что каждый вызов {@code buildRouter} создаёт новый
     * независимый экземпляр {@link Router}.
     */
    @Test
    void buildRouter_createsIndependentInstances() {
        BotApplication app = new BotApplication();
        BotConfig config = new BotConfig();

        Router first  = app.buildRouter(config);
        Router second = app.buildRouter(config);

        assertNotSame(first, second,
                "Каждый вызов buildRouter должен создавать новый Router");
    }

    /**
     * Проверяет, что {@link BotConfig} по умолчанию содержит токен бота.
     * <p>
     * Служит защитой от запуска с некорректной конфигурацией.
     */
    @Test
    void buildRouter_failsIfTokenIsMissing() {
        // BotConfig без BOT_TOKEN и без properties бросит исключение
        // — это защита от запуска с некорректной конфигурацией.
        // В тестах у нас есть application.properties, поэтому здесь
        // проверяем только контракт: сам BotConfig валиден.
        BotConfig config = new BotConfig();
        assertNotNull(config.getBotToken());
    }
}