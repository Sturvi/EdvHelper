package com.example.edvhelper.service.telegrambot.message;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.telegrambot.message.keyboards.InlineKeyboardMarkupFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class responsible for sending template messages to users.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateMessagesSender {
    private final MessageService messageService;

    public void sendFiscalId (Long chatId, FiscalId fiscalId){

        Path directory = Paths.get("/cheque");
        Path filePath = directory.resolve(fiscalId.getFiscalId() + ".jpeg");

        messageService.sendPhoto(chatId, filePath.toString());
        var keyboard = InlineKeyboardMarkupFactory.getYesOrNoKeyboard(fiscalId.getId().toString());

        String messageText = "Использован ли?            Дата: " + fiscalId.getChequeDate() + "\n\nКрайняя дата: " + fiscalId.getChequeDate().plusDays(89);

        messageService.sendMessageWithKeyboard(chatId, messageText, keyboard);
    }
}
