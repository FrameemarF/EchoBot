package org.translatorBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты перечисления {@link Language}: состав, ISO-коды и заполненность названий.
 */
class LanguageTest {

    /** В перечислении ровно два языка. */
    @Test
    void hasExactlyTwoLanguages() {
        Assertions.assertEquals(2, Language.values().length);
    }

    /** ISO-коды соответствуют ожидаемым значениям. */
    @Test
    void codesAreCorrect() {
        Assertions.assertEquals("ru", Language.RU.code);
        Assertions.assertEquals("en", Language.EN.code);
    }

    /** У каждого языка непустое отображаемое название. */
    @Test
    void labelsAreNotBlank() {
        for (Language l : Language.values()) {
            Assertions.assertFalse(l.label.isBlank());
        }
    }
}