package com.example.translatorbot.service;

import com.example.translatorbot.model.Language;

/**
 * Сервис перевода текста.
 * Определяет контракт для перевода текста между поддерживаемыми языками.
 */
public interface TranslationService {

    /**
     * Переводит текст с исходного языка на целевой.
     *
     * @param text   текст для перевода
     * @param source исходный язык
     * @param target целевой язык
     * @return переведённый текст
     */
    String translate(String text, Language source, Language target);
}