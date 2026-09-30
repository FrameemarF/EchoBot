package com.example.translatorbot.handlers;

import com.example.translatorbot.bot.TranslatorBotService;
import com.example.translatorbot.keyboards.Keyboards;
import com.example.translatorbot.model.Language;
import com.example.translatorbot.model.UserState;
import com.example.translatorbot.state.SessionManager;
import com.example.translatorbot.util.Messages;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработчик callback-запросов от inline-кнопок Telegram.
 * Разбирает callback-данные и выполняет соответствующее действие:
 * запускает выбор языка, сохраняет выбранные языки, управляет
 * клавиатурами и состоянием сессии пользователя.
 */
public class CallbackHandler {

    private static final String MENU_TRANSLATE = "menu:translate";
    private static final String MENU_HELP = "menu:help";
    private static final String RESULT_MORE = "result:more";
    private static final String RESULT_CHANGE_LANG = "result:change_lang";
    private static final String RESULT_MENU = "result:menu";

    private static final String SRC_PREFIX = "src:";
    private static final String TGT_PREFIX = "tgt:";

    private final SessionManager sessionManager;
    private final Messages messages;
    private final Keyboards keyboards;

    /**
     * Создаёт обработчик callback-запросов.
     *
     * @param sessionManager менеджер сессий пользователей
     * @param messages       источник текстов сообщений
     * @param keyboards      фабрика inline-клавиатур
     */
    public CallbackHandler(SessionManager sessionManager, Messages messages, Keyboards keyboards) {
        this.sessionManager = sessionManager;
        this.messages = messages;
        this.keyboards = keyboards;
    }

    /**
     * Обрабатывает входящий callback-запрос от inline-кнопки.
     * Определяет действие по содержимому {@code data} и делегирует
     * обработку соответствующему приватному методу.
     *
     * @param bot   сервис бота для отправки и редактирования сообщений
     * @param query callback-запрос от Telegram
     * @throws TelegramApiException если взаимодействие с Telegram API не удалось
     */
    public void handle(TranslatorBotService bot, CallbackQuery query) throws TelegramApiException {
        String data = query.getData();
        Long chatId = query.getMessage().getChatId();
        Integer messageId = query.getMessage().getMessageId();

        bot.answerCallback(query.getId());

        if (MENU_TRANSLATE.equals(data)) {
            onStartTranslateFromMenu(bot, chatId, messageId);
        } else if (MENU_HELP.equals(data)) {
            bot.editText(chatId, messageId, messages.help(), keyboards.mainMenu());
        } else if (data.startsWith(SRC_PREFIX)) {
            Language lang = parseLanguage(data.substring(SRC_PREFIX.length()));
            if (lang != null) onSourceSelected(bot, chatId, messageId, lang);
        } else if (data.startsWith(TGT_PREFIX)) {
            Language lang = parseLanguage(data.substring(TGT_PREFIX.length()));
            if (lang != null) onTargetSelected(bot, chatId, messageId, lang);
        } else if (RESULT_MORE.equals(data)) {
            onMore(bot, chatId, messageId);
        } else if (RESULT_CHANGE_LANG.equals(data)) {
            onStartTranslateFromResult(bot, chatId, messageId);
        } else if (RESULT_MENU.equals(data)) {
            onMenu(bot, chatId, messageId);
        }
    }

    /**
     * Запускает сценарий перевода из главного меню.
     * Устанавливает состояние ожидания языка-источника и заменяет
     * текущее сообщение на клавиатуру выбора источника.
     *
     * @param bot       сервис бота
     * @param chatId    идентификатор чата
     * @param messageId идентификатор редактируемого сообщения
     * @throws TelegramApiException если редактирование не удалось
     */
    private void onStartTranslateFromMenu(TranslatorBotService bot, Long chatId, Integer messageId)
            throws TelegramApiException {
        sessionManager.setState(chatId, UserState.WAITING_SOURCE_LANGUAGE);
        bot.editText(chatId, messageId,
                messages.chooseSourceLanguage(),
                keyboards.languageSelection("src"));
    }

    /**
     * Запускает сценарий перевода по кнопке «Сменить язык» после перевода.
     * Убирает клавиатуру с сообщения с предыдущим переводом, устанавливает
     * состояние ожидания языка-источника и отправляет новое сообщение
     * с клавиатурой выбора источника.
     *
     * @param bot       сервис бота
     * @param chatId    идентификатор чата
     * @param messageId идентификатор сообщения с предыдущим переводом
     * @throws TelegramApiException если взаимодействие с Telegram API не удалось
     */
    private void onStartTranslateFromResult(TranslatorBotService bot, Long chatId, Integer messageId)
            throws TelegramApiException {
        bot.removeKeyboard(chatId, messageId);
        sessionManager.setState(chatId, UserState.WAITING_SOURCE_LANGUAGE);
        bot.sendText(chatId,
                messages.chooseSourceLanguage(),
                keyboards.languageSelection("src"));
    }

    /**
     * Обрабатывает выбор языка-источника.
     * Сохраняет язык в сессии, переводит пользователя в состояние ожидания
     * языка-цели и заменяет сообщение на клавиатуру выбора цели.
     *
     * @param bot       сервис бота
     * @param chatId    идентификатор чата
     * @param messageId идентификатор редактируемого сообщения
     * @param lang      выбранный язык-источник
     * @throws TelegramApiException если редактирование не удалось
     */
    private void onSourceSelected(TranslatorBotService bot, Long chatId, Integer messageId, Language lang)
            throws TelegramApiException {
        sessionManager.setSourceLanguage(chatId, lang);
        sessionManager.setState(chatId, UserState.WAITING_TARGET_LANGUAGE);
        bot.editText(chatId, messageId,
                messages.chooseTargetLanguage(),
                keyboards.languageSelection("tgt"));
    }

    /**
     * Обрабатывает выбор языка-цели.
     * Сохраняет язык в сессии, переводит пользователя в состояние ожидания
     * текста и заменяет сообщение на приглашение отправить текст.
     *
     * @param bot       сервис бота
     * @param chatId    идентификатор чата
     * @param messageId идентификатор редактируемого сообщения
     * @param lang      выбранный язык-цель
     * @throws TelegramApiException если редактирование не удалось
     */
    private void onTargetSelected(TranslatorBotService bot, Long chatId, Integer messageId, Language lang)
            throws TelegramApiException {
        sessionManager.setTargetLanguage(chatId, lang);
        sessionManager.setState(chatId, UserState.WAITING_TEXT);
        bot.editText(chatId, messageId, messages.sendText(), null);
    }

    /**
     * Обрабатывает нажатие кнопки «Перевести ещё».
     * Убирает клавиатуру с сообщения с предыдущим переводом, переводит
     * пользователя в состояние ожидания текста и отправляет новое
     * сообщение с приглашением отправить текст.
     *
     * @param bot       сервис бота
     * @param chatId    идентификатор чата
     * @param messageId идентификатор сообщения с предыдущим переводом
     * @throws TelegramApiException если взаимодействие с Telegram API не удалось
     */
    private void onMore(TranslatorBotService bot, Long chatId, Integer messageId)
            throws TelegramApiException {
        sessionManager.setState(chatId, UserState.WAITING_TEXT);
        bot.removeKeyboard(chatId, messageId);
        bot.sendText(chatId, messages.sendText());
    }

    /**
     * Обрабатывает нажатие кнопки «В меню».
     * Убирает клавиатуру с текущего сообщения, сбрасывает сессию
     * пользователя и отправляет новое сообщение с главным меню.
     *
     * @param bot       сервис бота
     * @param chatId    идентификатор чата
     * @param messageId идентификатор текущего сообщения
     * @throws TelegramApiException если взаимодействие с Telegram API не удалось
     */
    private void onMenu(TranslatorBotService bot, Long chatId, Integer messageId)
            throws TelegramApiException {
        bot.removeKeyboard(chatId, messageId);
        sessionManager.reset(chatId);
        bot.sendText(chatId, messages.backToMenu(), keyboards.mainMenu());
    }

    /**
     * Находит значение {@link Language} по его коду.
     *
     * @param code код языка (например, {@code "ru"}, {@code "en"})
     * @return соответствующий язык или {@code null}, если код неизвестен
     */
    private Language parseLanguage(String code) {
        for (Language lang : Language.values()) {
            if (lang.getCode().equals(code)) return lang;
        }
        return null;
    }
}