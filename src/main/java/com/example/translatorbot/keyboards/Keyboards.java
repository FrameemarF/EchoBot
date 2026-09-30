package com.example.translatorbot.keyboards;

import com.example.translatorbot.model.Language;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика инлайн-клавиатур (inline-клавиатур) бота-переводчика.
 *
 * <p>Класс не хранит состояния и не обращается к Telegram API — он лишь собирает
 * объекты {@link InlineKeyboardMarkup}, которые затем отправляются пользователю.
 * Все callback-данные строятся по схеме {@code "область:действие[:параметр]"},
 * например {@code "menu:translate"} или {@code "lang:to:en"}.</p>
 *
 */
public class Keyboards {

    /**
     * Создаёт клавиатуру главного меню бота.
     *
     * <p>Содержит один ряд из двух кнопок:</p>
     * <ul>
     *     <li>«🌐 Перевести текст» — callback {@code menu:translate};</li>
     *     <li>«❓ Помощь» — callback {@code menu:help}.</li>
     * </ul>
     *
     * @return разметка инлайн-клавиатуры с кнопками главного меню
     */
    public InlineKeyboardMarkup mainMenu() {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button("🌐 Перевести текст", "menu:translate"));
        row.add(button("❓ Помощь", "menu:help"));
        return new InlineKeyboardMarkup(List.of(row));
    }

    /**
     * Создаёт клавиатуру выбора языка.
     *
     * <p>Для каждого значения {@link Language} (в порядке объявления в enum)
     * создаётся отдельный ряд с одной кнопкой. Отображаемый текст берётся из
     * {@link Language#getDisplayName()}, а callback-данные формируются как
     * {@code prefix + ":" + code}.</p>
     *
     * @param callbackPrefix префикс callback-данных, определяющий, для чего
     *                       выбирается язык (например, {@code "lang:from"} или
     *                       {@code "lang:to"}); не должен быть {@code null}
     * @return разметка инлайн-клавиатуры со списком доступных языков
     */
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

    /**
     * Создаёт клавиатуру действий с готовым результатом перевода.
     *
     * <p>Состоит из двух рядов:</p>
     * <ul>
     *     <li>«🔁 Перевести ещё» ({@code result:more}) и «🌐 Сменить язык»
     *         ({@code result:change_lang});</li>
     *     <li>«🏠 В меню» ({@code result:menu}).</li>
     * </ul>
     *
     * @return разметка инлайн-клавиатуры с действиями над результатом
     */
    public InlineKeyboardMarkup resultActions() {
        InlineKeyboardRow firstRow = new InlineKeyboardRow();
        firstRow.add(button("🔁 Перевести ещё", "result:more"));
        firstRow.add(button("🌐 Сменить язык", "result:change_lang"));

        InlineKeyboardRow secondRow = new InlineKeyboardRow();
        secondRow.add(button("🏠 В меню", "result:menu"));

        return new InlineKeyboardMarkup(List.of(firstRow, secondRow));
    }

    /**
     * Создаёт инлайн-кнопку с заданным текстом и callback-данными.
     *
     * @param text         подпись на кнопке
     * @param callbackData данные, которые Telegram вернёт в callback-запросе
     * @return готовая кнопка {@link InlineKeyboardButton}
     */
    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}