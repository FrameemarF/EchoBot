package com.example.translatorbot;

import com.example.translatorbot.config.BotConfig;
import com.example.translatorbot.keyboards.Keyboards;

import java.io.InputStream;
import java.util.Properties;

public class TranslatorBotApplication {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (InputStream in = TranslatorBotApplication.class
                .getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found in classpath");
            }
            props.load(in);
        }

        BotConfig config = new BotConfig(props);
        Keyboards keyboards = new Keyboards();

        System.out.println("Config loaded. Bot username: " + config.getBotUsername());
        System.out.println("Keyboards ready: " + keyboards.mainMenu().getKeyboard().size() + " rows");
    }
}