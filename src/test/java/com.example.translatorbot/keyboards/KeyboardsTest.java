package com.example.translatorbot.keyboards;

import com.example.translatorbot.model.Language;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardsTest {

    private final Keyboards keyboards = new Keyboards();

    @Test
    void mainMenuShouldContainTwoButtons() {
        InlineKeyboardMarkup menu = keyboards.mainMenu();
        List<List<InlineKeyboardButton>> rows = menu.getKeyboard();

        assertEquals(1, rows.size());
        assertEquals(2, rows.get(0).size());

        InlineKeyboardButton translate = rows.get(0).get(0);
        assertEquals("🌐 Перевести текст", translate.getText());
        assertEquals("menu:translate", translate.getCallbackData());

        InlineKeyboardButton help = rows.get(0).get(1);
        assertEquals("❓ Помощь", help.getText());
        assertEquals("menu:help", help.getCallbackData());
    }

    @Test
    void languageSelectionShouldCreateRowForEachLanguageWithSourcePrefix() {
        InlineKeyboardMarkup markup = keyboards.languageSelection("src");
        List<List<InlineKeyboardButton>> rows = markup.getKeyboard();

        assertEquals(Language.values().length, rows.size());

        for (int i = 0; i < Language.values().length; i++) {
            Language lang = Language.values()[i];
            List<InlineKeyboardButton> row = rows.get(i);

            assertEquals(1, row.size());
            assertEquals(lang.getDisplayName(), row.get(0).getText());
            assertEquals("src:" + lang.getCode(), row.get(0).getCallbackData());
        }
    }

    @Test
    void languageSelectionShouldUseTargetPrefix() {
        InlineKeyboardMarkup markup = keyboards.languageSelection("tgt");
        List<List<InlineKeyboardButton>> rows = markup.getKeyboard();

        assertEquals(Language.values().length, rows.size());

        for (int i = 0; i < Language.values().length; i++) {
            Language lang = Language.values()[i];
            assertEquals("tgt:" + lang.getCode(), rows.get(i).get(0).getCallbackData());
        }
    }

    @Test
    void resultActionsShouldContainExpectedButtons() {
        InlineKeyboardMarkup markup = keyboards.resultActions();
        List<List<InlineKeyboardButton>> rows = markup.getKeyboard();

        assertEquals(2, rows.size());

        List<InlineKeyboardButton> firstRow = rows.get(0);
        assertEquals(2, firstRow.size());

        assertEquals("🔁 Перевести ещё", firstRow.get(0).getText());
        assertEquals("result:more", firstRow.get(0).getCallbackData());

        assertEquals("🌐 Сменить язык", firstRow.get(1).getText());
        assertEquals("result:change_lang", firstRow.get(1).getCallbackData());

        List<InlineKeyboardButton> secondRow = rows.get(1);
        assertEquals(1, secondRow.size());
        assertEquals("🏠 В меню", secondRow.get(0).getText());
        assertEquals("result:menu", secondRow.get(0).getCallbackData());
    }
}