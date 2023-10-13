package com.example.edvhelper.service.telegrambot.message.keyboards;

import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ReplyKeyboardMarkupFactory is a class responsible for creating a predefined keyboard markup for Telegram bot.
 */
@Slf4j
public class ReplyKeyboardMarkupFactory {

    /**
     * Creates a ReplyKeyboardMarkup object which holds the keyboard layout to be shown to the Telegram users.
     *
     * @return a ReplyKeyboardMarkup object representing a specific keyboard layout.
     */
    public static ReplyKeyboardMarkup getUserReplyKeyboardMarkup() {
        log.debug("Start creating a new keyboard markup");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
/*        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton(TextCommandsEnum.ADD.getCommand()));
        keyboardSecondRow.add(new KeyboardButton(TextCommandsEnum.GET.getCommand()));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);*/

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        log.debug("Successfully created a new keyboard markup");

        try {
            return replyKeyboardMarkup;
        } catch (Exception e) {
            log.error("An error occurred while creating the keyboard markup", e);
            throw e;
        }
    }
}
