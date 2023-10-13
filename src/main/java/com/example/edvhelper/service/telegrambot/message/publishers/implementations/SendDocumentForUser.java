package com.example.edvhelper.service.telegrambot.message.publishers.implementations;

import com.example.edvhelper.service.telegrambot.message.publishers.AbstractMessageEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class SendDocumentForUser extends AbstractMessageEventPublisher {
    public SendDocumentForUser(ApplicationEventPublisher eventPublisher) {
        super(eventPublisher);
    }

    private SendDocument createDocumentMessage(Long chatId, File documentFile, String caption) {
        log.trace("Creating a new document message");
        InputFile document = new InputFile(documentFile);
        var sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(document);
        if (caption != null && !caption.isEmpty()) {
            sendDocument.setCaption(caption);
        }
        log.debug("Document message created: {}", sendDocument);
        return sendDocument;
    }

    public CompletableFuture<Message> sendDocumentToUser(Long chatId, File documentFile, String caption) {
        log.trace("Sending document: {}", documentFile.getName());
        return send(createDocumentMessage(chatId, documentFile, caption));
    }
}
