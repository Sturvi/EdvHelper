package com.example.edvhelper.service.telegrambot.commands;

import java.util.Arrays;

public enum CallbackQueryDataEnum {

    NEXT("next ", "➡ Следующее"),
    YES("YES ", "✅"),
    NO("NO ", "⛔️"),
    SORT("SORT", "Сортировка: ")
    ;

    private final String data;
    private final String text;

    CallbackQueryDataEnum(String data, String text) {
        this.data = data;
        this.text = text;
    }

    public String getData() {
        return data;
    }

    public String getText() {
        return text;
    }

    public static CallbackQueryDataEnum fromData(String data) {
        return Arrays.stream(CallbackQueryDataEnum.values())
                .filter(b -> data.toLowerCase().startsWith(b.getData().toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public static Long getId(String data) {
        return Arrays.stream(CallbackQueryDataEnum.values())
                .filter(b -> data.toLowerCase().startsWith(b.getData().toLowerCase()))
                .map(b -> data.replaceAll(b.getData(), "").trim())
                .findFirst()
                .map(Long::parseLong)
                .orElse(null);
    }

    public static String getDataParam(String data) {
        return Arrays.stream(CallbackQueryDataEnum.values())
                .filter(b -> data.toLowerCase().startsWith(b.getData().toLowerCase()))
                .map(b -> data.replaceAll(b.getData(), "").trim())
                .findFirst()
                .orElse(null);
    }
}
