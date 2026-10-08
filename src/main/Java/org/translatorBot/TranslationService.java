package org.translatorBot;

public interface TranslationService {
    String translate(String text, Language from, Language to) throws Exception;
}