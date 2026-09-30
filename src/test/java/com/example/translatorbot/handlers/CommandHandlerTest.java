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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    private static final Long CHAT_ID = 42L;

    @Mock
    private TranslatorBotService bot;
    @Mock
    private SessionManager sessionManager;
    @Mock
    private TranslationService translationService;
    @Mock
    private Messages messages;
    @Mock
    private Keyboards keyboards;
    @Mock
    private BotConfig botConfig;

    private CommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CommandHandler(
                sessionManager, translationService, messages, keyboards, botConfig);
    }

    private Message buildMessage(String text) {
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(CHAT_ID);
        when(message.getText()).thenReturn(text);
        return message;
    }

    // ---------- Команды ----------

    @Test
    void handle_start_shouldResetAndSendMenu() throws TelegramApiException {
        Message message = buildMessage("/start");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.start()).thenReturn("start");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(sessionManager).reset(CHAT_ID);
        verify(bot).sendText(CHAT_ID, "start", keyboard);
    }

    @Test
    void handle_translate_shouldSetStateAndSendSourceKeyboard() throws TelegramApiException {
        Message message = buildMessage("/translate");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.chooseSourceLanguage()).thenReturn("choose source");
        when(keyboards.languageSelection("src")).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(sessionManager).reset(CHAT_ID);
        verify(sessionManager).setState(CHAT_ID, UserState.WAITING_SOURCE_LANGUAGE);
        verify(bot).sendText(CHAT_ID, "choose source", keyboard);
    }

    @Test
    void handle_help_shouldSendHelpAndMenu() throws TelegramApiException {
        Message message = buildMessage("/help");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.help()).thenReturn("help");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(bot).sendText(CHAT_ID, "help", keyboard);
        verify(sessionManager, never()).reset(any());
    }

    @Test
    void handle_back_shouldResetAndSendMenu() throws TelegramApiException {
        Message message = buildMessage("/back");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.backToMenu()).thenReturn("back");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(sessionManager).reset(CHAT_ID);
        verify(bot).sendText(CHAT_ID, "back", keyboard);
    }

    @Test
    void handle_unknownCommand_shouldSendUnknownAndHelp() throws TelegramApiException {
        Message message = buildMessage("/foo");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.unknownCommand()).thenReturn("unknown");
        when(messages.help()).thenReturn("help");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(bot).sendText(CHAT_ID, "unknown");
        verify(bot).sendText(CHAT_ID, "help", keyboard);
    }

    @Test
    void handle_commandWithArguments_shouldRecognizeCommand() throws TelegramApiException {
        Message message = buildMessage("/start some args");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.start()).thenReturn("start");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(sessionManager).reset(CHAT_ID);
        verify(bot).sendText(CHAT_ID, "start", keyboard);
    }

    @Test
    void handle_commandInUpperCase_shouldRecognizeCommand() throws TelegramApiException {
        Message message = buildMessage("/START");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.start()).thenReturn("start");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(sessionManager).reset(CHAT_ID);
        verify(bot).sendText(CHAT_ID, "start", keyboard);
    }

    // ---------- Обычный текст вне WAITING_TEXT ----------

    @Test
    void handle_textOutsideWaitingText_shouldSendUnknown() throws TelegramApiException {
        Message message = buildMessage("привет");
        UserSession session = new UserSession();
        session.setState(UserState.IDLE);
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(sessionManager.getSession(CHAT_ID)).thenReturn(session);
        when(messages.unknownCommand()).thenReturn("unknown");
        when(messages.help()).thenReturn("help");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(bot).sendText(CHAT_ID, "unknown");
        verify(bot).sendText(CHAT_ID, "help", keyboard);
        verify(translationService, never()).translate(anyString(), any(), any());
    }

    // ---------- handleText через состояние WAITING_TEXT ----------

    private UserSession waitingSession(Language source, Language target) {
        UserSession session = new UserSession();
        session.setState(UserState.WAITING_TEXT);
        session.setSourceLanguage(source);
        session.setTargetLanguage(target);
        return session;
    }

    @Test
    void handle_emptyText_shouldSendEmptyWarning() throws TelegramApiException {
        Message message = buildMessage("   ");
        UserSession session = waitingSession(Language.RU, Language.EN);

        when(sessionManager.getSession(CHAT_ID)).thenReturn(session);
        when(messages.emptyText()).thenReturn("empty");

        handler.handle(bot, message);

        verify(bot).sendText(CHAT_ID, "empty");
        verify(translationService, never()).translate(anyString(), any(), any());
    }

    @Test
    void handle_tooLongText_shouldSendLimitWarning() throws TelegramApiException {
        Message message = buildMessage("long text");
        UserSession session = waitingSession(Language.RU, Language.EN);

        when(sessionManager.getSession(CHAT_ID)).thenReturn(session);
        when(botConfig.getTranslationMaxLength()).thenReturn(3);
        when(messages.tooLong(3)).thenReturn("too long");

        handler.handle(bot, message);

        verify(bot).sendText(CHAT_ID, "too long");
        verify(translationService, never()).translate(anyString(), any(), any());
    }

    @Test
    void handle_languagesNotSelected_shouldRestartLanguageSelection() throws TelegramApiException {
        Message message = buildMessage("hello");
        UserSession session = waitingSession(null, null);
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(sessionManager.getSession(CHAT_ID)).thenReturn(session);
        when(botConfig.getTranslationMaxLength()).thenReturn(500);
        when(messages.chooseSourceLanguage()).thenReturn("choose source");
        when(keyboards.languageSelection("src")).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(sessionManager).reset(CHAT_ID);
        verify(sessionManager).setState(CHAT_ID, UserState.WAITING_SOURCE_LANGUAGE);
        verify(bot).sendText(CHAT_ID, "choose source", keyboard);
        verify(translationService, never()).translate(anyString(), any(), any());
    }

    @Test
    void handle_validText_shouldTranslateAndSendResult() throws TelegramApiException {
        Message message = buildMessage("привет");
        UserSession session = waitingSession(Language.RU, Language.EN);
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(sessionManager.getSession(CHAT_ID)).thenReturn(session);
        when(botConfig.getTranslationMaxLength()).thenReturn(500);
        when(translationService.translate("привет", Language.RU, Language.EN))
                .thenReturn("hello");
        when(messages.translationResult("hello")).thenReturn("result: hello");
        when(keyboards.resultActions()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(translationService).translate("привет", Language.RU, Language.EN);
        verify(bot).sendText(CHAT_ID, "result: hello", keyboard);
    }

    @Test
    void handle_translationFails_shouldSendErrorMessage() throws TelegramApiException {
        Message message = buildMessage("привет");
        UserSession session = waitingSession(Language.RU, Language.EN);
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(sessionManager.getSession(CHAT_ID)).thenReturn(session);
        when(botConfig.getTranslationMaxLength()).thenReturn(500);
        when(translationService.translate("привет", Language.RU, Language.EN))
                .thenThrow(new RuntimeException("service down"));
        when(messages.translationError()).thenReturn("error");
        when(keyboards.resultActions()).thenReturn(keyboard);

        handler.handle(bot, message);

        verify(bot).sendText(CHAT_ID, "error", keyboard);
    }
}