package com.example.edvhelper.service.telegrambot.message.keyboards;

import com.example.edvhelper.service.telegrambot.commands.CallbackQueryDataEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class InlineKeyboardMarkupFactory extends AbstractInlineKeyboardMarkupFactory {

    /**
     * Creates a keyboard with options for "Yes" or "No" with a specific identifierInData.
     *
     * @param identifierInData The specific identifierInData to be used.
     * @return A keyboard with "Yes" and "No" options.
     */
    public static InlineKeyboardMarkup getYesOrNoKeyboard(String identifierInData) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, CallbackQueryDataEnum.YES, identifierInData);
        addButtonToCurrentLine(keyboard, CallbackQueryDataEnum.NO, identifierInData);

        return keyboard;
    }

    /**
     * Creates a keyboard with a "Next" option.
     *
     * @return A keyboard with the "Next" option.
     */
    public static InlineKeyboardMarkup getNextKeyboard() {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, CallbackQueryDataEnum.NEXT);

        return keyboard;
    }

    public static InlineKeyboardMarkup getNextKeyboard(String dataParametr) {
        var keyboard = creatNewInlineKeyboard();

        addButtonToNewLine(keyboard, CallbackQueryDataEnum.NEXT, dataParametr);

        return keyboard;
    }
}
