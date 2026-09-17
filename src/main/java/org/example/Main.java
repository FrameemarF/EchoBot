package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Точка входа в приложение. Считывает токен бота из переменной окружения
 * и запускает эхо-бота в режиме long polling.
 */
public class Main {
    /**
     * Запускает приложение.
     * @param args аргументы командной строки; в этой задаче не используются
     * @throws TelegramApiException если не удалось зарегистрировать бота в Telegram
     */
    public static void main(String[] args) throws TelegramApiException {
        String botToken = System.getenv("BOT_TOKEN");
        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException(
                    "Не задан токен бота. Установите переменную окружения BOT_TOKEN "
                            + "(в IntelliJ IDEA: Run Configuration -> Environment variables).");
        }

        TelegramClient telegramClient = new OkHttpTelegramClient(botToken);
        EchoReplyGenerator echoReplyGenerator = new EchoReplyGenerator();
        EchoUpdateConsumer echoUpdateConsumer = new EchoUpdateConsumer(telegramClient, echoReplyGenerator);

        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        botsApplication.registerBot(botToken, echoUpdateConsumer);

        System.out.println("Эхо-бот запущен и ожидает сообщений...");
    }
}