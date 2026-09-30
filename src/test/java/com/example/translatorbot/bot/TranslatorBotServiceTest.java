package com.example.translatorbot.bot;

import com.example.translatorbot.handlers.CallbackHandler;
import com.example.translatorbot.handlers.CommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslatorBotServiceTest {

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private CommandHandler commandHandler;

    @Mock
    private CallbackHandler callbackHandler;

    private TranslatorBotService bot;

    @BeforeEach
    void setUp() {
        bot = new TranslatorBotService(telegramClient, commandHandler, callbackHandler);
    }

    // ---------- consume ----------

    @Test
    void consume_shouldDelegateTextMessageToCommandHandler() throws TelegramApiException {
        Message message = mock(Message.class);
        Update update = mock(Update.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);

        bot.consume(update);

        verify(commandHandler, times(1)).handle(bot, message);
        verify(callbackHandler, never()).handle(any(), any());
    }

    @Test
    void consume_shouldDelegateCallbackQueryToCallbackHandler() throws TelegramApiException {
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        Update update = mock(Update.class);

        when(update.hasMessage()).thenReturn(false);
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);

        bot.consume(update);

        verify(callbackHandler, times(1)).handle(bot, callbackQuery);
        verify(commandHandler, never()).handle(any(), any());
    }

    @Test
    void consume_shouldIgnoreMessageWithoutText() throws TelegramApiException {
        Message message = mock(Message.class);
        Update update = mock(Update.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(false);

        bot.consume(update);

        verify(commandHandler, never()).handle(any(), any());
        verify(callbackHandler, never()).handle(any(), any());
    }

    @Test
    void consume_shouldWrapTelegramApiExceptionIntoRuntimeException() throws TelegramApiException {
        Message message = mock(Message.class);
        Update update = mock(Update.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);

        doThrow(new TelegramApiException("fail"))
                .when(commandHandler).handle(bot, message);

        assertThrows(RuntimeException.class, () -> bot.consume(update));
    }

    // ---------- sendText ----------

    @Test
    void sendText_shouldSendMessageWithoutKeyboard() throws TelegramApiException {
        bot.sendText(42L, "hello");

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient).execute(captor.capture());

        SendMessage sent = captor.getValue();
        assertEquals("42", sent.getChatId());
        assertEquals("hello", sent.getText());
    }

    @Test
    void sendText_withKeyboard_shouldAttachKeyboard() throws TelegramApiException {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text("Кнопка")
                .callbackData("test:data")
                .build());
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(List.of(row));

        bot.sendText(42L, "hello", keyboard);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient).execute(captor.capture());

        SendMessage sent = captor.getValue();
        assertEquals("42", sent.getChatId());
        assertEquals("hello", sent.getText());
        assertEquals(keyboard, sent.getReplyMarkup());
    }

    // ---------- editText ----------

    @Test
    void editText_shouldEditMessageText() throws TelegramApiException {
        bot.editText(42L, 100, "new text", null);

        ArgumentCaptor<EditMessageText> captor = ArgumentCaptor.forClass(EditMessageText.class);
        verify(telegramClient).execute(captor.capture());

        EditMessageText sent = captor.getValue();
        assertEquals("42", sent.getChatId());
        assertEquals(100, sent.getMessageId());
        assertEquals("new text", sent.getText());
    }

    // ---------- answerCallback ----------

    @Test
    void answerCallback_shouldSendAnswer() throws TelegramApiException {
        bot.answerCallback("cb-123");

        ArgumentCaptor<AnswerCallbackQuery> captor =
                ArgumentCaptor.forClass(AnswerCallbackQuery.class);
        verify(telegramClient).execute(captor.capture());

        assertEquals("cb-123", captor.getValue().getCallbackQueryId());
    }

    // ---------- removeKeyboard ----------

    @Test
    void removeKeyboard_shouldEditReplyMarkup() throws TelegramApiException {
        bot.removeKeyboard(42L, 100);

        ArgumentCaptor<EditMessageReplyMarkup> captor =
                ArgumentCaptor.forClass(EditMessageReplyMarkup.class);
        verify(telegramClient).execute(captor.capture());

        EditMessageReplyMarkup sent = captor.getValue();
        assertEquals("42", sent.getChatId());
        assertEquals(100, sent.getMessageId());
    }
}