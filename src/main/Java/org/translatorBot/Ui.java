package org.translatorBot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

/**
 * Хранит тексты сообщений и собирает inline-клавиатуры для всех шагов
 * диалога с пользователем. Позволяет держать весь UI-контент бота в одном
 * месте и не дублировать строки и разметку кнопок в обработчиках.
 */
public class Ui {
    public final String start =
            "👋 Привет! Это бот TranslateBot.\n" +
                    "Я помогу перевести текст между русским и английским языками.\n" +
                    "Выбери действие:";

    public final String help =
            "ℹ️ Список команд:\n" +
                    "/start      — начало работы с ботом и вывод главного меню\n" +
                    "/translate  — начать перевод: выбор языка, на который нужно перевести\n" +
                    "/help       — помощь\n" +
                    "/back       — вернуться на предыдущий шаг";

    public final String askText      = "✏️ Отправь текст для перевода.";
    public final String emptyText    = "⚠️ Текст пустой. Отправь, пожалуйста, непустой текст.";
    public final String tooLong      = "⚠️ Текст слишком длинный (макс. 4000 символов).";
    public final String unknownCmd   = "⚠️ Команда не распознана.";
    public final String backToMenu   = "↩️ Возвращаю в главное меню.";
    public final String useStart     = "ℹ️ Чтобы начать, нажми /start или /translate.";
    public final String translateErr = "❌ Не удалось перевести. Попробуй позже.";

    /**
     * Собирает клавиатуру главного меню.
     *
     * @return разметка с кнопками «Перевести текст» и «Помощь»
     */
    public InlineKeyboardMarkup mainMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button("Перевести текст", "TRANSLATE"),
                        button("Помощь", "HELP")))
                .build();
    }

    /**
     * Собирает клавиатуру, показываемую после выдачи перевода.
     *
     * @return разметка с кнопками «Перевести ещё», «Сменить язык» и «В меню»
     */
    public InlineKeyboardMarkup afterTranslation() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button("Перевести ещё", "MORE"),
                        button("Сменить язык", "CHANGE_LANG"),
                        button("В меню", "MENU")))
                .build();
    }

    /**
     * Создаёт inline-кнопку с заданным текстом и callback-данными.
     *
     * @param text отображаемый текст кнопки
     * @param data данные, приходящие в callback при нажатии
     * @return готовая inline-кнопка
     */
    private InlineKeyboardButton button(String text, String data) {
        return InlineKeyboardButton.builder().text(text).callbackData(data).build();
    }

    public String chooseSourceLang = "🌐 Выбери язык, С КОТОРОГО переводить:";
    public String chooseTargetLang = "🌐 Выбери язык, НА КОТОРЫЙ переводить:";

    /**
     * Собирает клавиатуру выбора языка-источника.
     *
     * @return разметка с кнопками доступных языков
     */
    public InlineKeyboardMarkup sourceChoice() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button(Language.RU.label, "SRC_RU"),
                        button(Language.EN.label, "SRC_EN")))
                .build();
    }

    /**
     * Собирает клавиатуру выбора языка-цели.
     *
     * @return разметка с кнопками доступных языков
     */
    public InlineKeyboardMarkup targetChoice() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button(Language.RU.label, "TGT_RU"),
                        button(Language.EN.label, "TGT_EN")))
                .build();
    }
}