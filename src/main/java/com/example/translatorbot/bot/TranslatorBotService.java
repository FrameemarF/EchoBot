package com.example.translatorbot.bot;

import com.example.translatorbot.handlers.CallbackHandler;
import com.example.translatorbot.handlers.CommandHandler;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


/**
 * Основной сервис Telegram-бота.
 * Реализует {@link LongPollingSingleThreadUpdateConsumer} и принимает обновления от Telegram.
 * Делегирует обработку сообщений и callback-запросов соответствующим хендлерам,
 * а также предоставляет методы для отправки и редактирования сообщений.
 */
public class TranslatorBotService implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final CommandHandler commandHandler;
    private final CallbackHandler callbackHandler;

    /**
     * Создаёт сервис бота с указанными зависимостями.
     *
     * @param telegramClient клиент для отправки запросов в Telegram API
     * @param commandHandler обработчик текстовых сообщений и команд
     * @param callbackHandler обработчик нажатий на inline-кнопки
     */
    public TranslatorBotService(TelegramClient telegramClient,
                                CommandHandler commandHandler,
                                CallbackHandler callbackHandler) {
        this.telegramClient = telegramClient;
        this.commandHandler = commandHandler;
        this.callbackHandler = callbackHandler;
    }

    /**
     * Обрабатывает входящее обновление от Telegram.
     * Если пришло текстовое сообщение — передаёт его {@link CommandHandler},
     * если нажатие кнопки — {@link CallbackHandler}.
     *
     * @param update обновление от Telegram
     */
    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                commandHandler.handle(this, update.getMessage());
            } else if (update.hasCallbackQuery()) {
                callbackHandler.handle(this, update.getCallbackQuery());
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException("Telegram API failure", e);
        }
    }

    /**
     * Отправляет текстовое сообщение в указанный чат.
     *
     * @param chatId идентификатор чата
     * @param text   текст сообщения
     * @throws TelegramApiException если отправка не удалась
     */
    public void sendText(Long chatId, String text) throws TelegramApiException {
        telegramClient.execute(
                SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(text)
                        .build()
        );
    }

    /**
     * Отправляет текстовое сообщение с inline-клавиатурой в указанный чат.
     *
     * @param chatId   идентификатор чата
     * @param text     текст сообщения
     * @param keyboard inline-клавиатура, прикрепляемая к сообщению
     * @throws TelegramApiException если отправка не удалась
     */
    public void sendText(Long chatId, String text, InlineKeyboardMarkup keyboard)
            throws TelegramApiException {
        telegramClient.execute(
                SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(text)
                        .replyMarkup(keyboard)
                        .build()
        );
    }

    /**
     * Редактирует текст и клавиатуру ранее отправленного сообщения.
     *
     * @param chatId    идентификатор чата
     * @param messageId идентификатор редактируемого сообщения
     * @param text      новый текст сообщения
     * @param keyboard  новая inline-клавиатура (может быть {@code null}, чтобы убрать)
     * @throws TelegramApiException если редактирование не удалось
     */
    public void editText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard)
            throws TelegramApiException {
        telegramClient.execute(
                EditMessageText.builder()
                        .chatId(chatId.toString())
                        .messageId(messageId)
                        .text(text)
                        .replyMarkup(keyboard)
                        .build()
        );
    }

    /**
     * Подтверждает обработку callback-запроса, убирая индикатор загрузки
     * с нажатой inline-кнопки.
     *
     * @param callbackQueryId идентификатор callback-запроса
     * @throws TelegramApiException если подтверждение не удалось
     */
    public void answerCallback(String callbackQueryId) throws TelegramApiException {
        telegramClient.execute(
                AnswerCallbackQuery.builder()
                        .callbackQueryId(callbackQueryId)
                        .build()
        );
    }

    /**
     * Удаляет inline-клавиатуру у ранее отправленного сообщения,
     * не изменяя его текст.
     *
     * @param chatId    идентификатор чата
     * @param messageId идентификатор сообщения
     * @throws TelegramApiException если редактирование не удалось
     */
    public void removeKeyboard(Long chatId, Integer messageId) throws TelegramApiException {
        telegramClient.execute(
                EditMessageReplyMarkup.builder()
                        .chatId(chatId.toString())
                        .messageId(messageId)
                        .build()
        );
    }
}