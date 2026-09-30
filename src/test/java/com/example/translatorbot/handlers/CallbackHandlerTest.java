package com.example.translatorbot.handlers;

import com.example.translatorbot.bot.TranslatorBotService;
import com.example.translatorbot.keyboards.Keyboards;
import com.example.translatorbot.model.Language;
import com.example.translatorbot.model.UserState;
import com.example.translatorbot.state.SessionManager;
import com.example.translatorbot.util.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallbackHandlerTest {

    private static final Long CHAT_ID = 42L;
    private static final Integer MESSAGE_ID = 100;
    private static final String CALLBACK_ID = "cb-1";

    @Mock
    private TranslatorBotService bot;
    @Mock
    private SessionManager sessionManager;
    @Mock
    private Messages messages;
    @Mock
    private Keyboards keyboards;

    private CallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CallbackHandler(sessionManager, messages, keyboards);
    }

    // ---------- Вспомогательный метод ----------

    private CallbackQuery buildQuery(String data) {
        CallbackQuery query = mock(CallbackQuery.class);
        Message message = mock(Message.class);

        when(query.getId()).thenReturn(CALLBACK_ID);
        when(query.getData()).thenReturn(data);
        when(query.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(CHAT_ID);
        when(message.getMessageId()).thenReturn(MESSAGE_ID);

        return query;
    }

    // ---------- menu:translate ----------

    @Test
    void handle_menuTranslate_shouldSetStateAndEditMessage() throws TelegramApiException {
        CallbackQuery query = buildQuery("menu:translate");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.chooseSourceLanguage()).thenReturn("choose source");
        when(keyboards.languageSelection("src")).thenReturn(keyboard);

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(sessionManager).setState(CHAT_ID, UserState.WAITING_SOURCE_LANGUAGE);
        verify(bot).editText(CHAT_ID, MESSAGE_ID, "choose source", keyboard);
    }

    // ---------- menu:help ----------

    @Test
    void handle_menuHelp_shouldEditMessageWithHelp() throws TelegramApiException {
        CallbackQuery query = buildQuery("menu:help");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.help()).thenReturn("help text");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(bot).editText(CHAT_ID, MESSAGE_ID, "help text", keyboard);
    }

    // ---------- src:ru ----------

    @Test
    void handle_srcPrefix_shouldSaveSourceLanguageAndAskForTarget() throws TelegramApiException {
        CallbackQuery query = buildQuery("src:ru");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.chooseTargetLanguage()).thenReturn("choose target");
        when(keyboards.languageSelection("tgt")).thenReturn(keyboard);

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(sessionManager).setSourceLanguage(CHAT_ID, Language.RU);
        verify(sessionManager).setState(CHAT_ID, UserState.WAITING_TARGET_LANGUAGE);
        verify(bot).editText(CHAT_ID, MESSAGE_ID, "choose target", keyboard);
    }

    // ---------- tgt:en ----------

    @Test
    void handle_tgtPrefix_shouldSaveTargetLanguageAndAskForText() throws TelegramApiException {
        CallbackQuery query = buildQuery("tgt:en");

        when(messages.sendText()).thenReturn("send text");

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(sessionManager).setTargetLanguage(CHAT_ID, Language.EN);
        verify(sessionManager).setState(CHAT_ID, UserState.WAITING_TEXT);
        verify(bot).editText(CHAT_ID, MESSAGE_ID, "send text", null);
    }

    // ---------- src:unknown ----------

    @Test
    void handle_srcPrefix_withUnknownCode_shouldDoNothingButAnswerCallback() throws TelegramApiException {
        CallbackQuery query = buildQuery("src:xx");

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(sessionManager, never()).setSourceLanguage(any(), any());
        verify(sessionManager, never()).setState(any(), any());
        verify(bot, never()).editText(any(), any(), any(), any());
    }

    // ---------- result:more ----------

    @Test
    void handle_resultMore_shouldRemoveKeyboardAndSendText() throws TelegramApiException {
        CallbackQuery query = buildQuery("result:more");

        when(messages.sendText()).thenReturn("send text");

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(sessionManager).setState(CHAT_ID, UserState.WAITING_TEXT);
        verify(bot).removeKeyboard(CHAT_ID, MESSAGE_ID);
        verify(bot).sendText(CHAT_ID, "send text");
    }

    // ---------- result:change_lang ----------

    @Test
    void handle_resultChangeLang_shouldRemoveKeyboardAndSendSourceKeyboard() throws TelegramApiException {
        CallbackQuery query = buildQuery("result:change_lang");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.chooseSourceLanguage()).thenReturn("choose source");
        when(keyboards.languageSelection("src")).thenReturn(keyboard);

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(bot).removeKeyboard(CHAT_ID, MESSAGE_ID);
        verify(sessionManager).setState(CHAT_ID, UserState.WAITING_SOURCE_LANGUAGE);
        verify(bot).sendText(CHAT_ID, "choose source", keyboard);
    }

    // ---------- result:menu ----------

    @Test
    void handle_resultMenu_shouldResetSessionAndSendMainMenu() throws TelegramApiException {
        CallbackQuery query = buildQuery("result:menu");
        InlineKeyboardMarkup keyboard = mock(InlineKeyboardMarkup.class);

        when(messages.backToMenu()).thenReturn("back");
        when(keyboards.mainMenu()).thenReturn(keyboard);

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(bot).removeKeyboard(CHAT_ID, MESSAGE_ID);
        verify(sessionManager).reset(CHAT_ID);
        verify(bot).sendText(CHAT_ID, "back", keyboard);
    }

    // ---------- неизвестный callback ----------

    @Test
    void handle_unknownCallback_shouldOnlyAnswerCallback() throws TelegramApiException {
        CallbackQuery query = buildQuery("unknown:data");

        handler.handle(bot, query);

        verify(bot).answerCallback(CALLBACK_ID);
        verify(sessionManager, never()).setState(any(), any());
        verify(sessionManager, never()).reset(any());
        verify(bot, never()).editText(any(), any(), any(), any());
        verify(bot, never()).sendText(any(), any());
        verify(bot, never()).sendText(any(), any(), any());
        verify(bot, never()).removeKeyboard(any(), any());
    }
}