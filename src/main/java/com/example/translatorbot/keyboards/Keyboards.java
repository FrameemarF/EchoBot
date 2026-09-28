package com.example.translatorbot.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class Keyboards {

    public InlineKeyboardMarkup mainMenu() {
        return new InlineKeyboardMarkup(List.of(List.of(
                button("🌐 Перевести текст", "menu:translate"),
                button("❓ Помощь", "menu:help")
        )));
    }

    public InlineKeyboardMarkup languageSelection() {
        return new InlineKeyboardMarkup(List.of(List.of(
                button("🇷🇺 Русский", "lang:ru"),
                button("🇬🇧 English", "lang:en")
        )));
    }

    public InlineKeyboardMarkup resultActions() {
        return new InlineKeyboardMarkup(List.of(
                List.of(
                        button("🔁 Перевести ещё", "result:more"),
                        button("🌐 Сменить язык", "result:change_lang")
                ),
                List.of(button("🏠 В меню", "result:menu"))
        ));
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton();
        b.setText(text);
        b.setCallbackData(callbackData);
        return b;
    }
}