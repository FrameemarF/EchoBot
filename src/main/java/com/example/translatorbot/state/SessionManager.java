package com.example.translatorbot.state;

import com.example.translatorbot.model.Language;
import com.example.translatorbot.model.UserSession;
import com.example.translatorbot.model.UserState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер сессий пользователей.
 * Хранит сессии в потокобезопасной карте по идентификатору чата
 * и предоставляет методы для чтения и изменения состояния сессии.
 */
public class SessionManager {

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    /**
     * Возвращает сессию пользователя по идентификатору чата.
     * Если сессия отсутствует, создаёт и сохраняет новую.
     *
     * @param chatId идентификатор чата
     * @return существующая или созданная сессия пользователя
     */
    public UserSession getSession(Long chatId) {
        return sessions.computeIfAbsent(chatId, id -> {
            UserSession s = new UserSession();
            s.setChatId(id);
            return s;
        });
    }

    /**
     * Устанавливает состояние сессии для указанного чата.
     *
     * @param chatId идентификатор чата
     * @param state  новое состояние
     */
    public void setState(Long chatId, UserState state) {
        getSession(chatId).setState(state);
    }

    /**
     * Устанавливает язык-источник для указанного чата.
     *
     * @param chatId идентификатор чата
     * @param lang   выбранный язык-источник
     */
    public void setSourceLanguage(Long chatId, Language lang) { getSession(chatId).setSourceLanguage(lang); }

    /**
     * Устанавливает язык-цель для указанного чата.
     *
     * @param chatId идентификатор чата
     * @param lang   выбранный язык-цель
     */
    public void setTargetLanguage(Long chatId, Language lang) {
        getSession(chatId).setTargetLanguage(lang);
    }

    /**
     * Сбрасывает сессию пользователя в начальное состояние:
     * состояние — IDLE, языки-источник и цель — null.
     *
     * @param chatId идентификатор чата
     */
    public void reset(Long chatId) {
        UserSession s = getSession(chatId);
        s.setState(UserState.IDLE);
        s.setSourceLanguage(null);
        s.setTargetLanguage(null);
    }
}