package org.translatorBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * Модульные тесты для {@link Ui}: проверяют содержимое текстовых сообщений
 * и корректность разметки всех inline-клавиатур, включая соблюдение
 * ограничений Telegram на длину {@code callback_data}.
 */
class UiTest {
    private final Ui ui = new Ui();

    /**
     * Извлекает список {@code callbackData} всех кнопок клавиатуры
     * в порядке их следования.
     *
     * @param markup разметка клавиатуры
     * @return плоский список callback-данных
     */
    private List<String> callbackData(InlineKeyboardMarkup markup) {
        return markup.getKeyboard().stream()
                .flatMap(row -> row.stream())
                .map(InlineKeyboardButton::getCallbackData)
                .toList();
    }

    /**
     * Извлекает список подписей всех кнопок клавиатуры в порядке их следования.
     *
     * @param markup разметка клавиатуры
     * @return плоский список текстов кнопок
     */
    private List<String> texts(InlineKeyboardMarkup markup) {
        return markup.getKeyboard().stream()
                .flatMap(row -> row.stream())
                .map(InlineKeyboardButton::getText)
                .toList();
    }

    /** Проверяет, что ни одно текстовое сообщение бота не является пустым. */
    @Test
    void allMessagesAreNotBlank() {
        List<String> messages = List.of(
                ui.start, ui.help, ui.askText, ui.emptyText,
                ui.tooLong, ui.unknownCmd, ui.backToMenu, ui.useStart,
                ui.translateErr, ui.chooseSourceLang, ui.chooseTargetLang);

        for (String m : messages) {
            Assertions.assertFalse(m.isBlank());
        }
    }

    /** Проверяет, что справка упоминает все поддерживаемые команды. */
    @Test
    void helpMentionsAllCommands() {
        Assertions.assertTrue(ui.help.contains("/start"));
        Assertions.assertTrue(ui.help.contains("/translate"));
        Assertions.assertTrue(ui.help.contains("/help"));
        Assertions.assertTrue(ui.help.contains("/back"));
    }

    /** Проверяет, что сообщение о превышении длины содержит актуальный лимит. */
    @Test
    void tooLongMessageMentionsLimit() {
        Assertions.assertTrue(ui.tooLong.contains("4000"));
    }

    /** Проверяет состав кнопок главного меню. */
    @Test
    void mainMenuHasTranslateAndHelp() {
        Assertions.assertEquals(List.of("TRANSLATE", "HELP"), callbackData(ui.mainMenu()));
    }

    /** Проверяет состав кнопок, показываемых после перевода. */
    @Test
    void afterTranslationHasThreeActions() {
        Assertions.assertEquals(
                List.of("MORE", "CHANGE_LANG", "MENU"),
                callbackData(ui.afterTranslation()));
    }

    /** Проверяет, что кнопки выбора языка-источника имеют префикс {@code SRC_}. */
    @Test
    void sourceChoiceUsesSrcPrefix() {
        Assertions.assertEquals(List.of("SRC_RU", "SRC_EN"), callbackData(ui.sourceChoice()));
    }

    /** Проверяет, что кнопки выбора языка-цели имеют префикс {@code TGT_}. */
    @Test
    void targetChoiceUsesTgtPrefix() {
        Assertions.assertEquals(List.of("TGT_RU", "TGT_EN"), callbackData(ui.targetChoice()));
    }

    /** Проверяет, что подписи кнопок соответствуют меткам языков. */
    @Test
    void languageButtonsShowLanguageLabels() {
        Assertions.assertEquals(
                List.of(Language.RU.label, Language.EN.label),
                texts(ui.sourceChoice()));
    }

    /** Проверяет, что у всех кнопок всех клавиатур заполнены текст и callback-данные. */
    @Test
    void everyKeyboardButtonHasTextAndCallbackData() {
        List<InlineKeyboardMarkup> keyboards = List.of(
                ui.mainMenu(), ui.afterTranslation(),
                ui.sourceChoice(), ui.targetChoice());

        for (InlineKeyboardMarkup k : keyboards) {
            for (String t : texts(k)) Assertions.assertFalse(t.isBlank());
            for (String c : callbackData(k)) Assertions.assertFalse(c.isBlank());
        }
    }

    /** Проверяет, что {@code callbackData} всех кнопок укладываются в лимит Telegram (64 байта). */
    @Test
    void callbackDataFitsTelegramLimit() {
        // Telegram разрешает callback_data максимум 64 байта
        List<InlineKeyboardMarkup> keyboards = List.of(
                ui.mainMenu(), ui.afterTranslation(),
                ui.sourceChoice(), ui.targetChoice());

        for (InlineKeyboardMarkup k : keyboards) {
            for (String c : callbackData(k)) {
                Assertions.assertTrue(c.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 64);
            }
        }
    }
}