package org.translatorBot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Тесты для {@link Main}.
 */
class MainTest {

    /**
     * Проверяет, что {@link Main} можно создать без исключений.
     */
    @Test
    void canBeInstantiated() {
        assertDoesNotThrow(Main::new);
    }
}