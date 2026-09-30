package com.example.translatorbot.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStateTest {

    @Test
    void shouldContainAllStates() {
        assertArrayEquals(
                new UserState[]{
                        UserState.IDLE,
                        UserState.WAITING_SOURCE_LANGUAGE,
                        UserState.WAITING_TARGET_LANGUAGE,
                        UserState.WAITING_TEXT
                },
                UserState.values()
        );
    }

    @Test
    void valueOfShouldWork() {
        assertEquals(UserState.IDLE, UserState.valueOf("IDLE"));
        assertEquals(UserState.WAITING_TEXT, UserState.valueOf("WAITING_TEXT"));
    }
}