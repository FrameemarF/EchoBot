package com.example.translatorbot.model;

public class UserSession {

    private Long chatId;
    private UserState state = UserState.IDLE;
    private Language targetLanguage;

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public UserState getState() { return state; }
    public void setState(UserState state) { this.state = state; }

    public Language getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(Language targetLanguage) { this.targetLanguage = targetLanguage; }

    public Language getSourceLanguage() {
        return targetLanguage == null ? null : targetLanguage.opposite();
    }
}