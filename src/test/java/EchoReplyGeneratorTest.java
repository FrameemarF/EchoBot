import org.example.EchoReplyGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Модульные тесты для {@link EchoReplyGenerator}.
 * Проверяют только логику формирования ответа, без обращения к Telegram API.
 */
class EchoReplyGeneratorTest {

    private final EchoReplyGenerator echoReplyGenerator = new EchoReplyGenerator();

    @Test
    void generateReplyReturnsSameTextAsInput() {
        String reply = echoReplyGenerator.generateReply("Привет, бот!");

        assertEquals("Привет, бот!", reply);
    }

    @Test
    void generateReplyReturnsEmptyStringForEmptyInput() {
        String reply = echoReplyGenerator.generateReply("");

        assertEquals("", reply);
    }

    @Test
    void generateReplyThrowsExceptionForNullInput() {
        assertThrows(IllegalArgumentException.class, () -> echoReplyGenerator.generateReply(null));
    }
}