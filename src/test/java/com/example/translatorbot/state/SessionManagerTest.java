package com.example.translatorbot.state;

import com.example.translatorbot.model.Language;
import com.example.translatorbot.model.UserSession;
import com.example.translatorbot.model.UserState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class SessionManagerTest {

    private static final Long CHAT_ID = 42L;

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    // ---------- getSession ----------

    @Test
    void getSession_shouldCreateNewSessionIfAbsent() {
        UserSession session = sessionManager.getSession(CHAT_ID);

        assertNotNull(session);
        assertEquals(CHAT_ID, session.getChatId());
        assertEquals(UserState.IDLE, session.getState());
        assertNull(session.getSourceLanguage());
        assertNull(session.getTargetLanguage());
    }

    @Test
    void getSession_shouldReturnSameSessionOnSecondCall() {
        UserSession first = sessionManager.getSession(CHAT_ID);
        UserSession second = sessionManager.getSession(CHAT_ID);

        assertSame(first, second);
    }

    @Test
    void getSession_shouldReturnDifferentSessionsForDifferentChats() {
        UserSession first = sessionManager.getSession(1L);
        UserSession second = sessionManager.getSession(2L);

        assertSame(first, sessionManager.getSession(1L));
        assertSame(second, sessionManager.getSession(2L));
        assertEquals(1L, first.getChatId());
        assertEquals(2L, second.getChatId());
    }

    // ---------- setState ----------

    @Test
    void setState_shouldUpdateSessionState() {
        sessionManager.setState(CHAT_ID, UserState.WAITING_TEXT);

        UserSession session = sessionManager.getSession(CHAT_ID);
        assertEquals(UserState.WAITING_TEXT, session.getState());
    }

    @Test
    void setState_shouldCreateSessionIfAbsent() {
        sessionManager.setState(CHAT_ID, UserState.WAITING_SOURCE_LANGUAGE);

        UserSession session = sessionManager.getSession(CHAT_ID);
        assertEquals(CHAT_ID, session.getChatId());
        assertEquals(UserState.WAITING_SOURCE_LANGUAGE, session.getState());
    }

    // ---------- setSourceLanguage ----------

    @Test
    void setSourceLanguage_shouldUpdateSession() {
        sessionManager.setSourceLanguage(CHAT_ID, Language.RU);

        UserSession session = sessionManager.getSession(CHAT_ID);
        assertEquals(Language.RU, session.getSourceLanguage());
    }

    // ---------- setTargetLanguage ----------

    @Test
    void setTargetLanguage_shouldUpdateSession() {
        sessionManager.setTargetLanguage(CHAT_ID, Language.EN);

        UserSession session = sessionManager.getSession(CHAT_ID);
        assertEquals(Language.EN, session.getTargetLanguage());
    }

    // ---------- reset ----------

    @Test
    void reset_shouldClearStateAndLanguages() {
        sessionManager.setState(CHAT_ID, UserState.WAITING_TEXT);
        sessionManager.setSourceLanguage(CHAT_ID, Language.RU);
        sessionManager.setTargetLanguage(CHAT_ID, Language.EN);

        sessionManager.reset(CHAT_ID);

        UserSession session = sessionManager.getSession(CHAT_ID);
        assertEquals(UserState.IDLE, session.getState());
        assertNull(session.getSourceLanguage());
        assertNull(session.getTargetLanguage());
    }

    @Test
    void reset_shouldKeepSameSessionInstance() {
        UserSession before = sessionManager.getSession(CHAT_ID);
        before.setState(UserState.WAITING_TEXT);

        sessionManager.reset(CHAT_ID);

        UserSession after = sessionManager.getSession(CHAT_ID);
        assertSame(before, after);
        assertEquals(UserState.IDLE, after.getState());
    }

    @Test
    void reset_shouldCreateSessionIfAbsent() {
        sessionManager.reset(CHAT_ID);

        UserSession session = sessionManager.getSession(CHAT_ID);
        assertEquals(UserState.IDLE, session.getState());
        assertEquals(CHAT_ID, session.getChatId());
    }
}