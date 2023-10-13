package com.example.edvhelper.service.telegrambot.message;


import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.message.publishers.implementations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final EditMessageForUser editMessageForUser;
    private final SendAudioForUser sendAudioForUser;
    private final SendMessageForUser sendMessageForUser;
    private final DeleteMessageForUser deleteMessageForUser;
    private final SendPhotoForUser sendPhotoForUser;
    private final SendDocumentForUser sendDocumentForUser;


    public CompletableFuture<Message> sendMessageToUser(Long chatId, String messageText) {
        log.trace("Sending message: {}", messageText);
        return sendMessageForUser.sendMessageToUserWithKeyboard(chatId, messageText);
    }

    public CompletableFuture<Message> sendMessageWithKeyboard(Long chatId, String messageText, ReplyKeyboard keyboard) {
        log.trace("Sending message with inline keyboard: {}", messageText);
        return sendMessageForUser.sendMessageWithInlineKeyboard(chatId, messageText, keyboard);
    }

    public void editMessageWithInlineKeyboard(BotEvent botEvent, String messageText, InlineKeyboardMarkup keyboard) {
        editMessageForUser.editMessageWithInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), messageText, keyboard);
    }

    public void deleteInlineKeyboard(BotEvent botEvent) {
        editMessageForUser.editTextAndDeleteInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), botEvent.getText());
    }

    public void editTextAndDeleteInlineKeyboard(BotEvent botEvent, String messageText) {
        editMessageForUser.editTextAndDeleteInlineKeyboard(botEvent.getId(), botEvent.getMessageId(), messageText);
    }

    public void deleteMessage (Long chatId, Integer messageId) {
        deleteMessageForUser.deleteMessage(chatId, messageId);
    }

    public CompletableFuture<Message> sendAudio(Long chatId, String title, File audioFile) {
        return sendAudioForUser.sendAudio(chatId, title, audioFile);
    }

    public CompletableFuture<Message> sendPhoto (Long chatId, String filePath, String caption) {
        return sendPhotoForUser.sendPhotoToUser(chatId, filePath, caption);
    }

    public CompletableFuture<Message> sendPhoto (Long chatId, String filePath) {
        return sendPhotoForUser.sendPhotoToUser(chatId, filePath, null);
    }

    public CompletableFuture<Message> sendDocument(Long chatId, File documentFile, String caption) {
        return sendDocumentForUser.sendDocumentToUser(chatId, documentFile, caption);
    }

    public CompletableFuture<Message> sendDocument(Long chatId, File documentFile) {
        return sendDocumentForUser.sendDocumentToUser(chatId, documentFile, null);
    }
}
