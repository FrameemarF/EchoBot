package com.example.translatorbot.model;

/**
 * Сессия пользователя бота.
 * Хранит текущее состояние диалога, идентификатор чата и выбранные
 * языки перевода (исходный и целевой).
 */
public class UserSession {

    private Long chatId;
    private UserState state = UserState.IDLE;
    private Language sourceLanguage;
    private Language targetLanguage;

    /** @return идентификатор чата пользователя */
    public Long getChatId() { return chatId; }

    /** @param chatId идентификатор чата пользователя */
    public void setChatId(Long chatId) { this.chatId = chatId; }

    /** @return текущее состояние диалога */
    public UserState getState() { return state; }

    /** @param state текущее состояние диалога */
    public void setState(UserState state) { this.state = state; }

    /** @return исходный язык перевода */
    public Language getSourceLanguage() { return sourceLanguage; }

    /** @param sourceLanguage исходный язык перевода */
    public void setSourceLanguage(Language sourceLanguage) { this.sourceLanguage = sourceLanguage; }

    /** @return целевой язык перевода */
    public Language getTargetLanguage() { return targetLanguage; }

    /** @param targetLanguage целевой язык перевода */
    public void setTargetLanguage(Language targetLanguage) { this.targetLanguage = targetLanguage; }

}