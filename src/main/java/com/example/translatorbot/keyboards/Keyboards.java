package com.example.translatorbot.keyboards;

import com.example.translatorbot.model.Language;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {

    public InlineKeyboardMarkup mainMenu() {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button("🌐 Перевести текст", "menu:translate"));
        row.add(button("❓ Помощь", "menu:help"));
        return new InlineKeyboardMarkup(List.of(row));
    }

    public InlineKeyboardMarkup languageSelection(String callbackPrefix) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (Language lang : Language.values()) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(button(
                    lang.getDisplayName(),
                    callbackPrefix + ":" + lang.getCode()
            ));
            rows.add(row);
        }
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup resultActions() {
        InlineKeyboardRow firstRow = new InlineKeyboardRow();
        firstRow.add(button("🔁 Перевести ещё", "result:more"));
        firstRow.add(button("🌐 Сменить язык", "result:change_lang"));

        InlineKeyboardRow secondRow = new InlineKeyboardRow();
        secondRow.add(button("🏠 В меню", "result:menu"));

        return new InlineKeyboardMarkup(List.of(firstRow, secondRow));
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}