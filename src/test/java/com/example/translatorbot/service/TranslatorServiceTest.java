package com.example.translatorbot.service;

import com.example.translatorbot.model.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslationServiceTest {

    @Test
    void shouldBeImplementableAsLambda() {
        TranslationService service = (text, source, target) ->
                text + ":" + source.getCode() + "->" + target.getCode();

        String result = service.translate("hello", Language.RU, Language.EN);

        assertEquals("hello:ru->en", result);
    }
}