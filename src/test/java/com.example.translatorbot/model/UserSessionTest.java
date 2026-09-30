package com.example.translatorbot.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @Test
    void defaultStateShouldBeIdle() {
        UserSession session = new UserSession();
        assertEquals(UserState.IDLE, session.getState());
    }

    @Test
    void shouldSetAndGetChatId() {
        UserSession session = new UserSession();
        session.setChatId(123L);
        assertEquals(123L, session.getChatId());
    }

    @Test
    void shouldSetAndGetState() {
        UserSession session = new UserSession();
        session.setState(UserState.WAITING_TEXT);
        assertEquals(UserState.WAITING_TEXT, session.getState());
    }

    @Test
    void shouldSetAndGetSourceLanguage() {
        UserSession session = new UserSession();
        session.setSourceLanguage(Language.RU);
        assertEquals(Language.RU, session.getSourceLanguage());
    }

    @Test
    void shouldSetAndGetTargetLanguage() {
        UserSession session = new UserSession();
        session.setTargetLanguage(Language.EN);
        assertEquals(Language.EN, session.getTargetLanguage());
    }
}