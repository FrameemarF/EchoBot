package com.example.translatorbot.model;

/**
 * Языки, поддерживаемые ботом. Хранит читаемое имя и ISO-код
 * для обращения к LibreTranslate.
 */
public enum Language {

    /** Русский язык. */
    RU("🇷🇺 Русский", "ru"),

    /** Английский язык. */
    EN("🇬🇧 English", "en");

    private final String displayName;
    private final String code;

    Language(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    /** @return название с флагом для UI */
    public String getDisplayName() {
        return displayName;
    }

    /** @return ISO-код ({@code "ru"} или {@code "en"}) */
    public String getCode() {
        return code;
    }

    /** @return противоположный язык */
    public Language opposite() {
        return this == RU ? EN : RU;
    }
}