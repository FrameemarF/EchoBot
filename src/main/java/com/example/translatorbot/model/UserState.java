package com.example.translatorbot.model;

/**
 * Шаги сценария диалога с пользователем.
 */
public enum UserState {

    /** Ожидание команды. */
    IDLE,

    /** Ожидание выбора целевого языка. */
    WAITING_LANGUAGE,

    /** Ожидание текста для перевода. */
    WAITING_TEXT
}