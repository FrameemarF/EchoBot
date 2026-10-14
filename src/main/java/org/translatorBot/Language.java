package org.translatorBot;

/**
 * Поддерживаемые языки перевода: отображаемое название и ISO-код.
 */
public enum Language {
    /** Русский язык. */
    RU("🇷🇺 Русский", "ru"),
    /** Английский язык. */
    EN("🇬🇧 English", "en");

    /** Отображаемое название языка (с флагом). */
    public final String label;
    /** ISO-код языка, используемый в запросах к LibreTranslate. */
    public final String code;

    Language(String label, String code) {
        this.label = label;
        this.code = code;
    }
}