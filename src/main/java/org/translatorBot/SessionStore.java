package org.translatorBot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище сессий пользователей.
 * <p>
 * Сопоставляет идентификатор чата Telegram ({@code chatId}) экземпляру
 * {@link Session}, в котором хранится состояние диалога этого пользователя.
 * Использует потокобезопасную реализацию карты, так как сообщения разных
 * пользователей могут обрабатываться параллельно.
 */
public class SessionStore {
    private final Map<Long, Session> sessions = new ConcurrentHashMap<>();

    /**
     * Возвращает сессию пользователя по идентификатору чата.
     * Если сессии ещё нет, создаёт новую и сохраняет её.
     *
     * @param chatId идентификатор чата Telegram
     * @return сессия пользователя (существующая или только что созданная)
     */
    public Session get(long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new Session());
    }
}