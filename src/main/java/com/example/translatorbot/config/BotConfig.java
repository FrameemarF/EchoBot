package com.example.translatorbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Telegram-бота.
 * Содержит учётные данные бота и ограничения на обработку переводов,
 * загружаемые из внешней конфигурации приложения.
 */
@Configuration
public class BotConfig {

    private final String botToken;
    private final String botUsername;
    private final int translationMaxLength;

    /**
     * Создаёт конфигурацию бота.
     *
     * @param botToken            токен доступа к Telegram Bot API
     * @param botUsername         имя пользователя бота в Telegram
     * @param translationMaxLength максимальная длина текста для перевода
     */
    public BotConfig(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${translation.max-length:500}") int translationMaxLength) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.translationMaxLength = translationMaxLength;
    }

    /** @return токен доступа к Telegram Bot API */
    public String getBotToken() { return botToken; }

    /** @return имя пользователя бота в Telegram */
    public String getBotUsername() { return botUsername; }

    /** @return максимальная длина текста для перевода */
    public int getTranslationMaxLength() { return translationMaxLength; }
}