package com.example.translatorbot.keyboards;

import com.example.translatorbot.model.Language;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboards {

    /**
     * Создаёт клавиатуру главного меню с кнопками «Перевести текст» и «Помощь».
     *
     * @return inline-клавиатура главного меню
     */
    public InlineKeyboardMarkup mainMenu() {
        return new InlineKeyboardMarkup(List.of(List.of(
                button("🌐 Перевести текст", "menu:translate"),
                button("❓ Помощь", "menu:help")
        )));
    }

    /**
     * Универсальная клавиатура выбора языка.
     * @param callbackPrefix "src" для выбора источника, "tgt" для выбора цели
     */
    public InlineKeyboardMarkup languageSelection(String callbackPrefix) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Language lang : Language.values()) {
            rows.add(List.of(button(
                    lang.getDisplayName(),
                    callbackPrefix + ":" + lang.getCode()
            )));
        }
        return new InlineKeyboardMarkup(rows);
    }

    /**
     * Создаёт клавиатуру с действиями после перевода:
     * «Перевести ещё», «Сменить язык», «В меню».
     *
     * @return inline-клавиатура с действиями над результатом
     */
    public InlineKeyboardMarkup resultActions() {
        return new InlineKeyboardMarkup(List.of(
                List.of(
                        button("🔁 Перевести ещё", "result:more"),
                        button("🌐 Сменить язык", "result:change_lang")
                ),
                List.of(button("🏠 В меню", "result:menu"))
        ));
    }

    /**
     * Создаёт inline-кнопку с указанным текстом и callback-данными.
     *
     * @param text         текст, отображаемый на кнопке
     * @param callbackData данные, отправляемые боту при нажатии
     * @return готовая inline-кнопка
     */
    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton();
        b.setText(text);
        b.setCallbackData(callbackData);
        return b;
    }
}