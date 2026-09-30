package com.example.translatorbot;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TranslatorBotApplicationTest {

    @Test
    void mainShouldThrowWhenConfigPropertiesMissing() throws Exception {
        ClassLoader parent = TranslatorBotApplication.class.getClassLoader();

        ClassLoader noConfigLoader = new ClassLoader(parent) {
            @Override
            public InputStream getResourceAsStream(String name) {
                if ("/config.properties".equals(name)) {
                    return null;
                }
                return super.getResourceAsStream(name);
            }
        };

        Class<?> appClass = Class.forName(
                "com.example.translatorbot.TranslatorBotApplication",
                true,
                noConfigLoader
        );

        Method main = appClass.getMethod("main", String[].class);

        InvocationTargetException thrown = assertThrows(
                InvocationTargetException.class,
                () -> main.invoke(null, (Object) new String[0])
        );

        Throwable cause = thrown.getCause();
        assertInstanceOf(IllegalStateException.class, cause);
        assertEquals("config.properties not found in classpath", cause.getMessage());
    }
}