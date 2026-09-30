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
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Точка входа приложения Translator Bot.
 * Загружает конфигурацию из classpath, собирает все компоненты бота
 * (обработчики, сервисы, клавиатуры, менеджер сессий) и запускает его.
 */
public class TranslatorBotApplication {

    private static final String DEFAULT_CONFIG = "/config.properties";

    /**
     * Запускает приложение.
     *
     * @param args аргументы командной строки (не используются)
     * @throws Exception если не удалось загрузить конфигурацию или запустить бота
     */
    public static void main(String[] args) throws Exception {
        new TranslatorBotApplication().run(DEFAULT_CONFIG);
    }

    /**
     * Загружает конфигурацию, собирает бота и регистрирует его в Telegram.
     *
     * @param configPath путь к файлу конфигурации в classpath
     * @throws Exception если не удалось загрузить конфигурацию или зарегистрировать бота
     */
    void run(String configPath) throws Exception {
        BotConfig config = new BotConfig(loadProperties(configPath));
        TranslatorBotService bot = createBot(config);
        createPollingApplication().registerBot(config.getBotToken(), bot);
    }

    /**
     * Читает properties-файл из classpath.
     *
     * @param resourcePath путь к ресурсу, например {@code /config.properties}
     * @return загруженные свойства
     * @throws IOException           при ошибке чтения
     * @throws IllegalStateException если ресурс не найден
     */
    Properties loadProperties(String resourcePath) throws IOException {
        Properties props = new Properties();
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException(resourcePath + " not found in classpath");
            }
            props.load(in);
        }
        return props;
    }

    /**
     * Вручную собирает все зависимости и создаёт бота.
     *
     * @param config конфигурация бота
     * @return готовый к регистрации бот
     */
    TranslatorBotService createBot(BotConfig config) {
        Messages messages = new Messages();
        Keyboards keyboards = new Keyboards();
        SessionManager sessionManager = new SessionManager();
        TranslationService translationService =
                new TranslationServiceImpl(config.getTranslationApiUrl());

        CommandHandler commandHandler = new CommandHandler(
                sessionManager, translationService, messages, keyboards, config);
        CallbackHandler callbackHandler = new CallbackHandler(
                sessionManager, messages, keyboards);

        TelegramClient telegramClient = new OkHttpTelegramClient(config.getBotToken());

        return new TranslatorBotService(telegramClient, commandHandler, callbackHandler);
    }

    /**
     * Создаёт long-polling приложение. Вынесено в отдельный метод,
     * чтобы его можно было подменить в тестах.
     *
     * @return новое long-polling приложение
     */
    TelegramBotsLongPollingApplication createPollingApplication() {
        return new TelegramBotsLongPollingApplication();
    }
}