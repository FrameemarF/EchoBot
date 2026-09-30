package com.example.translatorbot.handlers;

import com.example.translatorbot.bot.TranslatorBotService;
import com.example.translatorbot.config.BotConfig;
import com.example.translatorbot.keyboards.Keyboards;
import com.example.translatorbot.model.Language;
import com.example.translatorbot.model.UserSession;
import com.example.translatorbot.model.UserState;
import com.example.translatorbot.service.TranslationService;
import com.example.translatorbot.state.SessionManager;
import com.example.translatorbot.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработчик текстовых сообщений и команд Telegram.
 * Разбирает входящий текст: если это команда — вызывает соответствующий
 * сценарий, если обычный текст в состоянии ожидания — передаёт его
 * на перевод. Управляет состоянием сессии пользователя через
 * {@link SessionManager}.
 */
public class CommandHandler {

    private final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    private final SessionManager sessionManager;
    private final TranslationService translationService;
    private final Messages messages;
    private final Keyboards keyboards;
    private final BotConfig botConfig;

    /**
     * Создаёт обработчик команд.
     *
     * @param sessionManager     менеджер сессий пользователей
     * @param translationService сервис перевода текста
     * @param messages           источник текстов сообщений
     * @param keyboards          фабрика inline-клавиатур
     * @param botConfig          конфигурация бота
     */
    public CommandHandler(SessionManager sessionManager,
                          TranslationService translationService,
                          Messages messages,
                          Keyboards keyboards,
                          BotConfig botConfig) {
        this.sessionManager = sessionManager;
        this.translationService = translationService;
        this.messages = messages;
        this.keyboards = keyboards;
        this.botConfig = botConfig;
    }

    /**
     * Обрабатывает входящее текстовое сообщение.
     * Если сообщение начинается с {@code /} — разбирает его как команду.
     * Иначе, если пользователь находится в состоянии {@link UserState#WAITING_TEXT},
     * передаёт текст на перевод. В остальных случаях отвечает сообщением
     * о нераспознанной команде.
     *
     * @param bot     сервис бота для отправки сообщений
     * @param message входящее сообщение от Telegram
     * @throws TelegramApiException если отправка сообщения не удалась
     */
    public void handle(TranslatorBotService bot, Message message) throws TelegramApiException {
        Long chatId = message.getChatId();
        String text = message.getText();
        UserSession session = sessionManager.getSession(chatId);

        if (text != null && text.startsWith("/")) {
            String command = text.split("\\s+")[0].toLowerCase();
            switch (command) {
                case "/start"     -> handleStart(bot, chatId);
                case "/translate" -> handleTranslate(bot, chatId);
                case "/help"      -> handleHelp(bot, chatId);
                case "/back"      -> handleBack(bot, chatId);
                default           -> handleUnknown(bot, chatId);
            }
            return;
        }

        if (session.getState() == UserState.WAITING_TEXT) {
            handleText(bot, chatId, session, text);
        } else {
            handleUnknown(bot, chatId);
        }
    }

    /**
     * Обрабатывает команду {@code /start}.
     * Сбрасывает сессию пользователя и отправляет приветствие
     * с главным меню.
     *
     * @param bot    сервис бота
     * @param chatId идентификатор чата
     * @throws TelegramApiException если отправка не удалась
     */
    private void handleStart(TranslatorBotService bot, Long chatId)
            throws TelegramApiException {
        sessionManager.reset(chatId);
        bot.sendText(chatId, messages.start(), keyboards.mainMenu());
    }

    /**
     * Обрабатывает команду {@code /translate}.
     * Сбрасывает сессию, переводит пользователя в состояние ожидания
     * языка-источника и отправляет клавиатуру выбора источника.
     *
     * @param bot    сервис бота
     * @param chatId идентификатор чата
     * @throws TelegramApiException если отправка не удалась
     */
    private void handleTranslate(TranslatorBotService bot, Long chatId)
            throws TelegramApiException {
        sessionManager.reset(chatId);
        sessionManager.setState(chatId, UserState.WAITING_SOURCE_LANGUAGE);
        bot.sendText(chatId,
                messages.chooseSourceLanguage(),
                keyboards.languageSelection("src"));
    }

    /**
     * Обрабатывает команду {@code /help}.
     * Отправляет список команд и главное меню, не изменяя состояние сессии.
     *
     * @param bot    сервис бота
     * @param chatId идентификатор чата
     * @throws TelegramApiException если отправка не удалась
     */
    private void handleHelp(TranslatorBotService bot, Long chatId) throws TelegramApiException {
        bot.sendText(chatId, messages.help(), keyboards.mainMenu());
    }

    /**
     * Обрабатывает команду {@code /back}.
     * Сбрасывает сессию пользователя и отправляет главное меню.
     *
     * @param bot    сервис бота
     * @param chatId идентификатор чата
     * @throws TelegramApiException если отправка не удалась
     */
    private void handleBack(TranslatorBotService bot, Long chatId)
            throws TelegramApiException {
        sessionManager.reset(chatId);
        bot.sendText(chatId, messages.backToMenu(), keyboards.mainMenu());
    }

    /**
     * Обрабатывает неизвестную команду или текст вне сценария.
     * Отправляет сообщение о нераспознанной команде и справку
     * с главным меню.
     *
     * @param bot    сервис бота
     * @param chatId идентификатор чата
     * @throws TelegramApiException если отправка не удалась
     */
    private void handleUnknown(TranslatorBotService bot, Long chatId) throws TelegramApiException {
        bot.sendText(chatId, messages.unknownCommand());
        bot.sendText(chatId, messages.help(), keyboards.mainMenu());
    }

    /**
     * Обрабатывает текст для перевода.
     * Проверяет, что текст не пустой и не превышает лимит длины,
     * убеждается, что языки-источник и цель выбраны, и вызывает
     * {@link TranslationService#translate}. При ошибке перевода
     * отправляет пользователю сообщение об ошибке.
     *
     * @param bot     сервис бота
     * @param chatId  идентификатор чата
     * @param session текущая сессия пользователя
     * @param text    текст для перевода
     * @throws TelegramApiException если отправка не удалась
     */
    private void handleText(TranslatorBotService bot, Long chatId, UserSession session, String text)
            throws TelegramApiException {


        if (text == null || text.trim().isEmpty()) {
            bot.sendText(chatId, messages.emptyText());
            return;
        }
        if (text.length() > botConfig.getTranslationMaxLength()) {
            bot.sendText(chatId, messages.tooLong(botConfig.getTranslationMaxLength()));
            return;
        }

        Language source = session.getSourceLanguage();
        Language target = session.getTargetLanguage();

        if (source == null || target == null) {
            sessionManager.reset(chatId);
            sessionManager.setState(chatId, UserState.WAITING_SOURCE_LANGUAGE);
            bot.sendText(chatId,
                    messages.chooseSourceLanguage(),
                    keyboards.languageSelection("src"));
            return;
        }

        try {
            String translated = translationService.translate(text, source, target);
            bot.sendText(chatId, messages.translationResult(translated), keyboards.resultActions());
        } catch (RuntimeException ex) {
            log.error("Translation failed for chat {} ({}->{}): {}",
                    chatId, source, target, ex.getMessage(), ex);
            bot.sendText(chatId, messages.translationError(), keyboards.resultActions());
        }
    }
}
