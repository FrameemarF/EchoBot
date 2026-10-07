package org.translatorBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Тесты {@link Sender}: формирование запросов и устойчивость к ошибкам клиента.
 * <p>
 * {@link TelegramClient} имеет десятки методов, реализовывать их все вручную нет смысла,
 * поэтому вместо класса используется {@link Proxy}, который записывает вызовы
 * {@code execute(...)} в список {@link #executed}. Mockito не нужен.
 */
class SenderTest {
    /** Запросы, переданные в {@code TelegramClient.execute(...)} в порядке вызова. */
    private final List<Object> executed = new ArrayList<>();
    /** Если {@code true}, клиент бросает {@link TelegramApiException} на каждый вызов. */
    private boolean failing = false;
    private Sender sender;

    /**
     * Создаёт прокси-клиент, записывающий вызовы {@code execute}, и оборачивает его в {@link Sender}.
     */
    @BeforeEach
    void setUp() {
        TelegramClient client = (TelegramClient) Proxy.newProxyInstance(
                TelegramClient.class.getClassLoader(),
                new Class<?>[]{TelegramClient.class},
                (proxy, method, args) -> {
                    if (!method.getName().equals("execute")) {
                        return null;
                    }
                    executed.add(args[0]);
                    if (failing) {
                        throw new TelegramApiException("network down");
                    }
                    return null;
                });
        sender = new Sender(client);
    }

    /**
     * Проверяет, что был выполнен ровно один запрос, и возвращает его как {@link SendMessage}.
     *
     * @return единственное отправленное сообщение
     */
    private SendMessage onlySentMessage() {
        Assertions.assertEquals(1, executed.size());
        return (SendMessage) executed.get(0);
    }

    /** Отправляет простое сообщение без клавиатуры с корректными chatId и текстом. */
    @Test
    void sendsPlainMessage() {
        sender.send(123L, "hello");

        SendMessage sent = onlySentMessage();
        Assertions.assertEquals("123", sent.getChatId());
        Assertions.assertEquals("hello", sent.getText());
        Assertions.assertNull(sent.getReplyMarkup());
    }

    /** Отправляет сообщение с inline-клавиатурой и прикрепляет её без копирования. */
    @Test
    void sendsMessageWithKeyboard() {
        InlineKeyboardMarkup keyboard = new Ui().mainMenu();

        sender.send(5L, "menu", keyboard);

        SendMessage sent = onlySentMessage();
        Assertions.assertEquals("5", sent.getChatId());
        Assertions.assertSame(keyboard, sent.getReplyMarkup());
    }

    /** При {@code null}-клавиатуре replyMarkup не устанавливается. */
    @Test
    void nullKeyboardMeansNoReplyMarkup() {
        sender.send(7L, "text", null);

        Assertions.assertNull(onlySentMessage().getReplyMarkup());
    }

    /** Отрицательные chatId (группы/каналы) корректно преобразуются в строку. */
    @Test
    void handlesNegativeChatIds() {
        sender.send(-1001234L, "group");

        Assertions.assertEquals("-1001234", onlySentMessage().getChatId());
    }

    /** Исключения клиента при отправке сообщения проглатываются и не пробрасываются. */
    @Test
    void sendSwallowsClientExceptions() {
        failing = true;

        Assertions.assertDoesNotThrow(() -> sender.send(1L, "text"));
        Assertions.assertEquals(1, executed.size());
    }

    /** {@code ackCallback} отвечает на callback с переданным идентификатором. */
    @Test
    void ackCallbackAnswersWithGivenId() {
        sender.ackCallback("cb-42");

        Assertions.assertEquals(1, executed.size());
        AnswerCallbackQuery answer = (AnswerCallbackQuery) executed.get(0);
        Assertions.assertEquals("cb-42", answer.getCallbackQueryId());
    }

    /** Исключения клиента при подтверждении callback проглатываются. */
    @Test
    void ackCallbackSwallowsClientExceptions() {
        failing = true;

        Assertions.assertDoesNotThrow(() -> sender.ackCallback("cb-1"));
    }
}