package org.example;

import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Принимает обновления Telegram и реализует поведение эхо-бота:
 * на каждое текстовое сообщение отправляет пользователю точно такой же текст.
 * Обновления, не содержащие текстового сообщения (например, стикеры,
 * фото без подписи, системные события), игнорируются.
 */
public class EchoUpdateConsumer extends DefaultLongPollingUpdateConsumer {

    private final TelegramClient telegramClient;
    private final EchoReplyGenerator echoReplyGenerator;

    /**
     * Создаёт обработчик обновлений.
     *
     * @param telegramClient     клиент, через который отправляются запросы в Telegram Bot API
     * @param echoReplyGenerator компонент, формирующий текст ответа на входящее сообщение
     */
    public EchoUpdateConsumer(TelegramClient telegramClient, EchoReplyGenerator echoReplyGenerator) {
        this.telegramClient = telegramClient;
        this.echoReplyGenerator = echoReplyGenerator;
    }

    /**
     * Обрабатывает одно входящее обновление. Если это текстовое сообщение —
     * отправляет ответ с тем же текстом в тот же чат, откуда оно пришло.
     *
     * @param update входящее обновление от Telegram
     */
    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String chatId = update.getMessage().getChatId().toString();
        String incomingText = update.getMessage().getText();
        String replyText = echoReplyGenerator.generateReply(incomingText);

        SendMessage outgoingMessage = SendMessage.builder().chatId(chatId).text(replyText).build();

        try {
            telegramClient.execute(outgoingMessage);
        } catch (TelegramApiException exception) {
            System.err.println("Не удалось отправить ответ пользователю: " + exception.getMessage());
        }
    }
}