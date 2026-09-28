package com.example.translatorbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа TranslateBot. Запускает Spring Boot контекст,
 * который поднимает бины и регистрирует бота в Telegram API.
 */
@SpringBootApplication
public class TranslatorBotApplication {

    /** Запускает приложение. */
    public static void main(String[] args) {
        SpringApplication.run(TranslatorBotApplication.class, args);
    }
}