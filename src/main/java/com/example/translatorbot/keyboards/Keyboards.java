package com.example.translatorbot.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * Фабрика клавиатур для Telegram-бота.
 * Предоставляет готовые наборы inline-кнопок для главного меню,
 * выбора языка и действий с результатом перевода.
 */
@Component
public class Keyboards {

    /**
     * Создаёт клавиатуру главного меню.
     *
     * @return разметка с кнопками «Перевести текст» и «Помощь»
     */
    public InlineKeyboardMarkup mainMenu() {
        InlineKeyboardButton translate = button("🌐 Перевести текст", "menu:translate");
        InlineKeyboardButton help = button("❓ Помощь", "menu:help");
        return new InlineKeyboardMarkup(List.of(List.of(translate, help)));
    }

    /**
     * Создаёт клавиатуру выбора языка перевода.
     *
     * @return разметка с кнопками «Русский» и «English»
     */
    public InlineKeyboardMarkup languageSelection() {
        InlineKeyboardButton ru = button("🇷🇺 Русский", "lang:ru");
        InlineKeyboardButton en = button("🇬🇧 English", "lang:en");
        return new InlineKeyboardMarkup(List.of(List.of(ru, en)));
    }

    /**
     * Создаёт клавиатуру действий после перевода.
     *
     * @return разметка с кнопками «Перевести ещё», «Сменить язык» и «В меню»
     */
    public InlineKeyboardMarkup resultActions() {
        InlineKeyboardButton more = button("🔁 Перевести ещё", "result:more");
        InlineKeyboardButton changeLang = button("🌐 Сменить язык", "result:change_lang");
        InlineKeyboardButton menu = button("🏠 В меню", "result:menu");
        return new InlineKeyboardMarkup(List.of(
                List.of(more, changeLang),
                List.of(menu)
        ));
    }

    /**
     * Создаёт inline-кнопку с указанным текстом и callback-данными.
     *
     * @param text         отображаемый текст кнопки
     * @param callbackData данные, отправляемые при нажатии
     * @return готовая кнопка
     */
    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton();
        b.setText(text);
        b.setCallbackData(callbackData);
        return b;
    }
}