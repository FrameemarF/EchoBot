package com.example.translatorbot.model;

/**
 * Сессия пользователя: состояние сценария и выбранный язык.
 * Создаётся лениво в {@code SessionManager} по chatId.
 */
public class UserSession {

    private Long chatId;
    private UserState state = UserState.IDLE;
    private Language targetLanguage;

    /** @return идентификатор чата */
    public Long getChatId() {
        return chatId;
    }

    /** @param chatId идентификатор чата Telegram */
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    /** @return текущий шаг сценария */
    public UserState getState() {
        return state;
    }

    /** @param state новый шаг */
    public void setState(UserState state) {
        this.state = state;
    }

    /** @return целевой язык или {@code null} */
    public Language getTargetLanguage() {
        return targetLanguage;
    }

    /** @param targetLanguage новый целевой язык */
    public void setTargetLanguage(Language targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    /**
     * @return исходный язык (противоположный целевому)
     *         или {@code null}, если целевой ещё не выбран
     */
    public Language getSourceLanguage() {
        return targetLanguage == null ? null : targetLanguage.opposite();
    }
}