package com.example.translatorbot.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

    @Test
    void shouldContainRuAndEn() {
        assertArrayEquals(
                new Language[]{Language.RU, Language.EN},
                Language.values()
        );
    }

    @Test
    void shouldReturnDisplayNameAndCode() {
        assertEquals("🇷🇺 Русский", Language.RU.getDisplayName());
        assertEquals("ru", Language.RU.getCode());

        assertEquals("🇬🇧 English", Language.EN.getDisplayName());
        assertEquals("en", Language.EN.getCode());
    }

    @Test
    void valueOfShouldWork() {
        assertEquals(Language.RU, Language.valueOf("RU"));
        assertEquals(Language.EN, Language.valueOf("EN"));
    }
}