package com.example.edvhelper.service.telegrambot.message.keyboards;

import com.example.edvhelper.service.telegrambot.commands.CallbackQueryDataEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract factory class for creating InlineKeyboardMarkup objects.
 */
@Slf4j
public abstract class AbstractInlineKeyboardMarkupFactory {

    /**
     * Creates a new InlineKeyboardMarkup object with an empty keyboard.
     *
     * @return new InlineKeyboardMarkup object.
     */
    protected static InlineKeyboardMarkup creatNewInlineKeyboard() {
        log.debug("Creating new empty InlineKeyboardMarkup");
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    /**
     * Adds a button to the InlineKeyboardMarkup object with the given parameters.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     * @param identifierInData                 Word for the button text.
     * @param isNewLine            Whether to add the button to a new line.
     */
    protected static void addButton(InlineKeyboardMarkup inlineKeyboardMarkup, CallbackQueryDataEnum callbackQueryDataEnum, String identifierInData, boolean isNewLine) {
        var data = identifierInData != null ? callbackQueryDataEnum.getData() + " " + identifierInData : callbackQueryDataEnum.getData();
        var text = callbackQueryDataEnum.getText();
        log.debug("Adding button to {} line: text={}, data={}", isNewLine ? "new" : "current", text, data);
        var keyboardRoad = isNewLine ? getNewKeyboardRoad(inlineKeyboardMarkup) : getCurrentKeyboardRoad(inlineKeyboardMarkup);

        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(data);
        keyboardRoad.add(button);
    }

    /**
     * Adds a button with custom text to the InlineKeyboardMarkup object with the given parameters.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param customText           Custom text for the button.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     * @param identifierInData     Identifier in data for callback.
     * @param isNewLine            Whether to add the button to a new line.
     */
    private static void addCustomTextButton(InlineKeyboardMarkup inlineKeyboardMarkup, String customText, CallbackQueryDataEnum callbackQueryDataEnum, String identifierInData, boolean isNewLine) {
        var data = identifierInData != null ? callbackQueryDataEnum.getData() + " " + identifierInData : callbackQueryDataEnum.getData();
        log.debug("Adding button with custom text to {} line: text={}, data={}", isNewLine ? "new" : "current", customText, data);
        var keyboardRoad = isNewLine ? getNewKeyboardRoad(inlineKeyboardMarkup) : getCurrentKeyboardRoad(inlineKeyboardMarkup);

        InlineKeyboardButton button = new InlineKeyboardButton(customText);
        button.setCallbackData(data);
        keyboardRoad.add(button);
    }

    /**
     * Adds a button with custom text to a new line in the InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param customText           Custom text for the button.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     */
    protected static void addCustomTextButtonToNewLine(InlineKeyboardMarkup inlineKeyboardMarkup, String customText, CallbackQueryDataEnum callbackQueryDataEnum) {
        addCustomTextButton(inlineKeyboardMarkup, customText, callbackQueryDataEnum, null, true);
    }

    /**
     * Adds a button with custom text to the current line in the InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param customText           Custom text for the button.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     */
    protected static void addCustomTextButtonToCurrentLine(InlineKeyboardMarkup inlineKeyboardMarkup, String customText, CallbackQueryDataEnum callbackQueryDataEnum) {
        addCustomTextButton(inlineKeyboardMarkup, customText, callbackQueryDataEnum, null, false);
    }

    /**
     * Adds a button to a new line in the InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     * @param word                 Word for the button text.
     */
    protected static void addButtonToNewLine(InlineKeyboardMarkup inlineKeyboardMarkup, CallbackQueryDataEnum callbackQueryDataEnum, String word) {
        addButton(inlineKeyboardMarkup, callbackQueryDataEnum, word, true);
    }

    /**
     * Adds a button to a new line in the InlineKeyboardMarkup object without a specific word.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     */
    protected static void addButtonToNewLine(InlineKeyboardMarkup inlineKeyboardMarkup, CallbackQueryDataEnum callbackQueryDataEnum) {
        addButton(inlineKeyboardMarkup, callbackQueryDataEnum, null, true);
    }

    /**
     * Adds a button to the current line in the InlineKeyboardMarkup object without a specific word.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     */
    protected static void addButtonToCurrentLine(InlineKeyboardMarkup inlineKeyboardMarkup, CallbackQueryDataEnum callbackQueryDataEnum) {
        addButton(inlineKeyboardMarkup, callbackQueryDataEnum, null, false);
    }

    /**
     * Adds a button to the current line in the InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to add the button to.
     * @param callbackQueryDataEnum     Enum representing the data for the button.
     * @param word                 Word for the button text.
     */
    protected static void addButtonToCurrentLine(InlineKeyboardMarkup inlineKeyboardMarkup, CallbackQueryDataEnum callbackQueryDataEnum, String word) {
        addButton(inlineKeyboardMarkup, callbackQueryDataEnum, word, false);
    }

    /**
     * Gets a new keyboard row for the given InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to get the new row for.
     * @return new keyboard row.
     */
    private static List<InlineKeyboardButton> getNewKeyboardRoad(InlineKeyboardMarkup inlineKeyboardMarkup) {
        log.debug("Getting new keyboard row");
        List<InlineKeyboardButton> keyboardRoad = new ArrayList<>();
        inlineKeyboardMarkup.getKeyboard().add(keyboardRoad);
        return keyboardRoad;
    }

    /**
     * Gets the current keyboard row for the given InlineKeyboardMarkup object.
     *
     * @param inlineKeyboardMarkup InlineKeyboardMarkup object to get the current row for.
     * @return current keyboard row.
     */
    private static List<InlineKeyboardButton> getCurrentKeyboardRoad(InlineKeyboardMarkup inlineKeyboardMarkup) {
        log.debug("Getting current keyboard row");
        var keyboard = inlineKeyboardMarkup.getKeyboard();
        return keyboard.get(keyboard.size() - 1);
    }
}
