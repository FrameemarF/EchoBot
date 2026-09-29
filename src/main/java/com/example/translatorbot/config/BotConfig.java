package com.example.translatorbot.config;

import java.util.Properties;

/**
 * Конфигурация Telegram-бота.
 * Загружает учётные данные бота, адрес API перевода и ограничения
 * из переданного объекта {@link Properties}.
 */
public class BotConfig {

    private final String botToken;
    private final String botUsername;
    private final String translationApiUrl;
    private final int translationMaxLength;

    /**
     * Создаёт конфигурацию на основе свойств.
     * Обязательные свойства ({@code telegram.bot.token}, {@code telegram.bot.username},
     * {@code translation.api.url}) должны присутствовать, иначе выбрасывается исключение.
     * Максимальная длина перевода ({@code translation.max-length}) по умолчанию равна 500.
     *
     * @param props свойства конфигурации
     * @throws IllegalStateException если обязательное свойство отсутствует или пусто
     */
    public BotConfig(Properties props) {
        this.botToken = require(props, "telegram.bot.token");
        this.botUsername = require(props, "telegram.bot.username");
        this.translationApiUrl = require(props, "translation.api.url");
        this.translationMaxLength = Integer.parseInt(
                props.getProperty("translation.max-length", "500"));
    }

    /**
     * Возвращает значение обязательного свойства.
     *
     * @param props свойства конфигурации
     * @param key   ключ свойства
     * @return значение свойства
     * @throws IllegalStateException если значение отсутствует или пусто
     */
    private String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing property: " + key);
        }
        return value;
    }

    /** @return токен доступа к Telegram Bot API */
    public String getBotToken() { return botToken; }

    /** @return имя пользователя бота в Telegram */
    public String getBotUsername() { return botUsername; }

    /** @return URL API сервиса перевода */
    public String getTranslationApiUrl() { return translationApiUrl; }

    /** @return максимальная длина текста для перевода */
    public int getTranslationMaxLength() { return translationMaxLength; }
}