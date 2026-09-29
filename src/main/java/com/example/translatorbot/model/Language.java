package com.example.translatorbot.model;

/**
 * Языки, поддерживаемые ботом для перевода.
 * Каждый элемент хранит отображаемое название и код языка.
 */
public enum Language {
    RU("🇷🇺 Русский", "ru"),
    EN("🇬🇧 English", "en");

    private final String displayName;
    private final String code;

    /**
     * Создаёт элемент перечисления.
     *
     * @param displayName отображаемое название языка
     * @param code        код языка (например, для API перевода)
     */
    Language(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    /** @return отображаемое название языка */
    public String getDisplayName() { return displayName; }

    /** @return код языка */
    public String getCode() { return code; }
}