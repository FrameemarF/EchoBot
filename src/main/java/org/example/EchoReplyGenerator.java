package org.example;

/**
 * Формирует текст ответа бота на основе входящего сообщения пользователя.
 */
public class EchoReplyGenerator {

    /**
     * Строит текст ответа для заданного входящего сообщения.
     *
     * @param incomingText текст, полученный от пользователя; не должен быть {@code null}
     * @return текст, который следует отправить пользователю в ответ
     * @throws IllegalArgumentException если {@code incomingText} равен {@code null}
     */
    public String generateReply(String incomingText) {
        if (incomingText == null) {
            throw new IllegalArgumentException("Входящий текст сообщения не может быть null");
        }
        return incomingText;
    }
}