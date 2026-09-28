package com.example.translatorbot.config;

import java.util.Properties;

public class BotConfig {

    private final String botToken;
    private final String botUsername;
    private final String translationApiUrl;
    private final int translationMaxLength;

    public BotConfig(Properties props) {
        this.botToken = require(props, "telegram.bot.token");
        this.botUsername = require(props, "telegram.bot.username");
        this.translationApiUrl = require(props, "translation.api.url");
        this.translationMaxLength = Integer.parseInt(
                props.getProperty("translation.max-length", "500"));
    }

    private String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing property: " + key);
        }
        return value;
    }

    public String getBotToken() { return botToken; }
    public String getBotUsername() { return botUsername; }
    public String getTranslationApiUrl() { return translationApiUrl; }
    public int getTranslationMaxLength() { return translationMaxLength; }
}