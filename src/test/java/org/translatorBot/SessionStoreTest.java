package org.translatorBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Модульные тесты для {@link SessionStore}: проверяют, что хранилище
 * возвращает одну и ту же сессию для одного {@code chatId}, разные —
 * для разных, сохраняет состояние между вызовами и корректно работает
 * при конкурентном доступе из нескольких потоков.
 */
class SessionStoreTest {

    /**
     * Проверяет, что повторный вызов {@link SessionStore#get(long)} с одним и
     * тем же {@code chatId} возвращает тот же самый экземпляр сессии.
     */
    @Test
    void returnsSameSessionForSameChat() {
        SessionStore store = new SessionStore();

        Assertions.assertSame(store.get(1L), store.get(1L));
    }

    /**
     * Проверяет, что для разных {@code chatId} хранилище возвращает
     * разные экземпляры {@link Session}.
     */
    @Test
    void returnsDifferentSessionsForDifferentChats() {
        SessionStore store = new SessionStore();

        Assertions.assertNotSame(store.get(1L), store.get(2L));
    }

    /**
     * Проверяет, что изменения сессии, сделанные через один вызов
     * {@link SessionStore#get(long)}, видны при последующих вызовах с тем же
     * {@code chatId} и не затрагивают сессию другого пользователя.
     */
    @Test
    void sessionStateIsKeptBetweenCalls() {
        SessionStore store = new SessionStore();
        store.get(1L).setSource(Language.RU);

        Assertions.assertEquals(Language.RU, store.get(1L).getSource());
        Assertions.assertNull(store.get(2L).getSource());
    }

    /**
     * Проверяет, что при одновременном обращении из нескольких потоков
     * к одному {@code chatId} создаётся только одна сессия, и все потоки
     * получают ссылку на один и тот же экземпляр.
     *
     * @throws Exception если ожидание потоков было прервано
     */
    @Test
    void concurrentAccessCreatesOnlyOneSession() throws Exception {
        SessionStore store = new SessionStore();
        int threadCount = 16;
        CountDownLatch start = new CountDownLatch(1);
        ConcurrentLinkedQueue<Session> seen = new ConcurrentLinkedQueue<>();
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                seen.add(store.get(42L));
            });
            threads[i].start();
        }
        start.countDown();
        for (Thread t : threads) {
            t.join(5000);
        }

        Assertions.assertEquals(threadCount, seen.size());
        Session first = seen.peek();
        for (Session s : seen) {
            Assertions.assertSame(first, s);
        }
    }
}