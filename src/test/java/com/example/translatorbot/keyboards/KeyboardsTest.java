package com.example.translatorbot.keyboards;

import com.example.translatorbot.model.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyboardsTest {

    private Keyboards keyboards;

    @BeforeEach
    void setUp() {
        keyboards = new Keyboards();
    }

    // ---------- mainMenu ----------

    @Test
    @DisplayName("mainMenu(): однорядная клавиатура из двух кнопок")
    void mainMenu_hasSingleRowWithTwoButtons() {
        InlineKeyboardMarkup markup = keyboards.mainMenu();

        assertNotNull(markup);
        assertNotNull(markup.getKeyboard());
        assertEquals(1, markup.getKeyboard().size(), "Ожидается ровно один ряд");
        assertEquals(2, markup.getKeyboard().get(0).size(), "Ожидается ровно две кнопки");
    }

    @Test
    @DisplayName("mainMenu(): тексты и callback-данные кнопок")
    void mainMenu_hasExpectedTextsAndCallbacks() {
        InlineKeyboardMarkup markup = keyboards.mainMenu();
        List<InlineKeyboardButton> buttons = flatten(markup);

        assertEquals("🌐 Перевести текст", buttons.get(0).getText());
        assertEquals("menu:translate", buttons.get(0).getCallbackData());

        assertEquals("❓ Помощь", buttons.get(1).getText());
        assertEquals("menu:help", buttons.get(1).getCallbackData());
    }

    @Test
    @DisplayName("mainMenu(): у всех кнопок заполнены текст и callback")
    void mainMenu_allButtonsAreFullyConfigured() {
        assertAllButtonsConfigured(keyboards.mainMenu());
    }

    // ---------- languageSelection ----------

    @Test
    @DisplayName("languageSelection(): по одному ряду и одной кнопке на каждый язык")
    void languageSelection_hasRowPerLanguage() {
        InlineKeyboardMarkup markup = keyboards.languageSelection("lang:to");

        assertNotNull(markup);
        assertEquals(Language.values().length, markup.getKeyboard().size(),
                "Количество рядов должно совпадать с количеством языков");

        for (InlineKeyboardRow row : markup.getKeyboard()) {
            assertEquals(1, row.size(), "В каждом ряду должна быть ровно одна кнопка");
        }
    }

    @Test
    @DisplayName("languageSelection(): callback = prefix + ':' + код языка")
    void languageSelection_buildsCallbackFromPrefixAndCode() {
        String prefix = "lang:to";
        InlineKeyboardMarkup markup = keyboards.languageSelection(prefix);
        List<InlineKeyboardButton> buttons = flatten(markup);

        Language[] languages = Language.values();
        assertEquals(languages.length, buttons.size());

        for (int i = 0; i < languages.length; i++) {
            Language lang = languages[i];
            InlineKeyboardButton button = buttons.get(i);

            assertEquals(lang.getDisplayName(), button.getText(),
                    "Текст кнопки должен быть displayName языка " + lang);
            assertEquals(prefix + ":" + lang.getCode(), button.getCallbackData(),
                    "Callback должен содержать префикс и код языка " + lang);
        }
    }

    @Test
    @DisplayName("languageSelection(): порядок кнопок совпадает с порядком Language.values()")
    void languageSelection_preservesEnumOrder() {
        InlineKeyboardMarkup markup = keyboards.languageSelection("lang:from");
        List<InlineKeyboardButton> buttons = flatten(markup);
        Language[] languages = Language.values();

        for (int i = 0; i < languages.length; i++) {
            assertTrue(buttons.get(i).getCallbackData().endsWith(":" + languages[i].getCode()));
        }
    }

    @Test
    @DisplayName("languageSelection(): работает с любым префиксом, включая пустую строку")
    void languageSelection_usesGivenPrefixVerbatim() {
        InlineKeyboardMarkup markup = keyboards.languageSelection("");
        List<InlineKeyboardButton> buttons = flatten(markup);

        assertEquals(":" + Language.values()[0].getCode(), buttons.get(0).getCallbackData());
    }

    @Test
    @DisplayName("languageSelection(): у всех кнопок заполнены текст и callback")
    void languageSelection_allButtonsAreFullyConfigured() {
        assertAllButtonsConfigured(keyboards.languageSelection("lang:to"));
    }

    // ---------- resultActions ----------

    @Test
    @DisplayName("resultActions(): два ряда — 2 и 1 кнопка")
    void resultActions_hasTwoRows() {
        InlineKeyboardMarkup markup = keyboards.resultActions();

        assertNotNull(markup);
        assertEquals(2, markup.getKeyboard().size());
        assertEquals(2, markup.getKeyboard().get(0).size());
        assertEquals(1, markup.getKeyboard().get(1).size());
    }

    @Test
    @DisplayName("resultActions(): тексты и callback-данные кнопок")
    void resultActions_hasExpectedTextsAndCallbacks() {
        InlineKeyboardMarkup markup = keyboards.resultActions();
        List<InlineKeyboardButton> buttons = flatten(markup);

        assertEquals(3, buttons.size());

        assertEquals("🔁 Перевести ещё", buttons.get(0).getText());
        assertEquals("result:more", buttons.get(0).getCallbackData());

        assertEquals("🌐 Сменить язык", buttons.get(1).getText());
        assertEquals("result:change_lang", buttons.get(1).getCallbackData());

        assertEquals("🏠 В меню", buttons.get(2).getText());
        assertEquals("result:menu", buttons.get(2).getCallbackData());
    }

    @Test
    @DisplayName("resultActions(): у всех кнопок заполнены текст и callback")
    void resultActions_allButtonsAreFullyConfigured() {
        assertAllButtonsConfigured(keyboards.resultActions());
    }

    // ---------- helpers ----------

    /** Разворачивает ряды клавиатуры в плоский список кнопок. */
    private List<InlineKeyboardButton> flatten(InlineKeyboardMarkup markup) {
        return markup.getKeyboard().stream()
                .flatMap(List::stream)
                .toList();
    }

    private void assertAllButtonsConfigured(InlineKeyboardMarkup markup) {
        assertNotNull(markup);
        assertNotNull(markup.getKeyboard());

        List<InlineKeyboardButton> buttons = flatten(markup);
        assertTrue(!buttons.isEmpty(), "Клавиатура не должна быть пустой");

        for (InlineKeyboardButton button : buttons) {
            assertNotNull(button.getText(), "Текст кнопки не должен быть null");
            assertTrue(!button.getText().isBlank(), "Текст кнопки не должен быть пустым");
            assertNotNull(button.getCallbackData(), "callbackData не должен быть null");
            assertTrue(!button.getCallbackData().isBlank(), "callbackData не должен быть пустым");
        }
    }
}