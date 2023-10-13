package com.example.edvhelper.service.telegrambot.message.publishers.implementations;

import com.example.edvhelper.service.telegrambot.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * This class represents a photo sender for users.
 */
@Component
@Slf4j
public class SendPhotoForUser extends AbstractMessageEventPublisher {

    public SendPhotoForUser(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    private SendPhoto createPhotoMessage(Long chatId, String filePath, String caption) {
        log.trace("Creating a new photo message");

        InputFile photo = new InputFile(new File(filePath)); // Using InputFile to set the photo

        var sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(photo);
        if (caption != null && !caption.isEmpty()) {
            sendPhoto.setCaption(caption);
        }

        log.debug("Photo message created: {}", sendPhoto);
        return sendPhoto;
    }

    public CompletableFuture<Message> sendPhotoToUser(Long chatId, String filePath, String caption) {
        log.trace("Sending photo from path: {}", filePath);
        return send(createPhotoMessage(chatId, filePath, caption));
    }
}
