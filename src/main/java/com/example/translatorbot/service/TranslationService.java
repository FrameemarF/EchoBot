package com.example.translatorbot.service;

import com.example.translatorbot.model.Language;

/**
 * Контракт сервиса перевода. Отделён от реализации, чтобы
 * в тестах мокать HTTP-клиент и при необходимости менять бэкенд.
 */
public interface TranslationService {

    /**
     * Переводит текст с {@code source} на {@code target}.
     * Если языки совпадают — возвращает исходный текст без изменений.
     *
     * @throws RuntimeException если сервис недоступен или вернул ошибку
     */
    String translate(String text, Language source, Language target);
}