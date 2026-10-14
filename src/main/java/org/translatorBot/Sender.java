package org.translatorBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Обёртка над {@link TelegramClient} для отправки сообщений и подтверждения
 * callback-запросов. Ошибки отправки логируются, но не пробрасываются наружу.
 */
public class Sender {
    private final TelegramClient client;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * @param client клиент Telegram, через который выполняются запросы
     */
    public Sender(TelegramClient client) { this.client = client; }

    /**
     * Отправляет текстовое сообщение без клавиатуры.
     *
     * @param chatId идентификатор чата
     * @param text   текст сообщения
     */
    public void send(long chatId, String text) { send(chatId, text, null); }

    /**
     * Отправляет текстовое сообщение с опциональной inline-клавиатурой.
     * При возникновении ошибки логирует её и не выбрасывает исключение.
     *
     * @param chatId   идентификатор чата
     * @param text     текст сообщения
     * @param keyboard клавиатура или {@code null}, если не нужна
     */
    public void send(long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage.SendMessageBuilder<?, ?> b = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text);
        if (keyboard != null) b.replyMarkup(keyboard);
        try {
            client.execute(b.build());
        } catch (Exception e) {
            log.error("Failed to send message to chat {}", chatId, e);
        }
    }

    /**
     * Подтверждает callback-запрос (убирает «часики» у пользователя).
     * При ошибке логирует предупреждение и не выбрасывает исключение.
     *
     * @param callbackId идентификатор callback-запроса
     */
    public void ackCallback(String callbackId) {
        try {
            client.execute(AnswerCallbackQuery.builder().callbackQueryId(callbackId).build());
        } catch (Exception e) {
            log.warn("Failed to answer callback {}", callbackId, e);
        }
    }
}