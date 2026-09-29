package com.example.translatorbot;

import com.example.translatorbot.bot.TranslatorBotService;
import com.example.translatorbot.config.BotConfig;
import com.example.translatorbot.handlers.CallbackHandler;
import com.example.translatorbot.handlers.CommandHandler;
import com.example.translatorbot.keyboards.Keyboards;
import com.example.translatorbot.service.TranslationService;
import com.example.translatorbot.service.TranslationServiceImpl;
import com.example.translatorbot.state.SessionManager;
import com.example.translatorbot.util.Messages;

import java.io.InputStream;
import java.util.Properties;

/**
 * Точка входа приложения Translator Bot.
 * Загружает конфигурацию из classpath, собирает все компоненты бота
 * (обработчики, сервисы, клавиатуры, менеджер сессий) и запускает его.
 */
public class TranslatorBotApplication {

    /**
     * Инициализирует и запускает Telegram-бота.
     * Читает {@code config.properties}, создаёт зависимости вручную
     * и регистрирует бота в Telegram.
     *
     * @param args аргументы командной строки (не используются)
     * @throws Exception если не удалось загрузить конфигурацию или запустить бота
     */
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (InputStream in = TranslatorBotApplication.class
                .getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found in classpath");
            }
            props.load(in);
        }

        BotConfig config = new BotConfig(props);

        Messages messages = new Messages();
        Keyboards keyboards = new Keyboards();
        SessionManager sessionManager = new SessionManager();
        TranslationService translationService =
                new TranslationServiceImpl(config.getTranslationApiUrl());

        CommandHandler commandHandler = new CommandHandler(
                sessionManager, translationService, messages, keyboards, config);
        CallbackHandler callbackHandler = new CallbackHandler(
                sessionManager, messages, keyboards);

        TranslatorBotService bot = new TranslatorBotService(
                config, commandHandler, callbackHandler);

        bot.register();
    }
}