package com.example.translatorbot.model;

public enum Language {
    RU("🇷🇺 Русский", "ru"),
    EN("🇬🇧 English", "en");

    private final String displayName;
    private final String code;

    Language(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() { return displayName; }
    public String getCode() { return code; }

    public Language opposite() {
        return this == RU ? EN : RU;
    }
}