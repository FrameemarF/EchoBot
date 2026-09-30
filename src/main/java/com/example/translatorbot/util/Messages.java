package com.example.translatorbot.util;

public class Messages {

    public String start() {
        return "👋 Привет! Это бот TranslateBot.\n" +
                "Я помогу перевести текст между русским и английским языками.\n" +
                "Выбери действие:";
    }

    public String help() {
        return "ℹ️ Список команд:\n" +
                "/start      — запуск бота и меню\n" +
                "/translate  — начать перевод: выбор языка\n" +
                "/help       — помощь\n" +
                "/back       — вернуться в главное меню";
    }

    public String chooseSourceLanguage() { return "🌐 Выбери язык, С КОТОРОГО переводим:"; }

    public String chooseTargetLanguage() { return "🌐 Выбери язык, НА КОТОРЫЙ переводим:"; }

    public String sendText() {
        return "✏️ Отправь текст для перевода.";
    }

    public String emptyText() {
        return "⚠️ Текст пустой. Отправь что-нибудь осмысленное.";
    }

    public String tooLong(int max) {
        return "⚠️ Слишком длинный текст. Максимум — " + max + " символов.";
    }

    public String translationResult(String translated) {
        return "✅ Перевод: " + translated;
    }

    public String translationError() {
        return "❌ Сервис перевода временно недоступен. Попробуй позже.";
    }

    public String unknownCommand() {
        return "⚠️ Команда не распознана.";
    }

    public String backToMenu() {
        return "↩️ Возвращаю в главное меню.";
    }
}