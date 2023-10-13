package com.example.edvhelper.service.telegrambot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.*;

import java.net.URL;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotEvent {

    private BotEventType eventType = BotEventType.NONE;
    private Long id;
    private Integer messageId;
    private String text;
    private String data;
    private String userName;
    private User from;
    private Contact contact;
    private String phoneNumber;
    private Document document;
    private String fileName;
    @Setter
    private URL fileUrl;

    public static BotEvent getTelegramObject(Update update) {
        log.debug("Creating BotEvent object from the Update object");
        BotEvent botEvent = new BotEvent();

        if (isDocument(update)) {
            botEvent.eventType = BotEventType.DOCUMENT;
        } else if (isMessageWithText(update)) {
            botEvent.eventType = BotEventType.MESSAGE;
        } else if (isCallbackWithData(update)) {
            botEvent.eventType = BotEventType.CALLBACK_QUERY;
        } else if (isDeactivationQuery(update)) {
            botEvent.eventType = BotEventType.DEACTIVATION_QUERY;
        }

        botEvent.initTelegramObject(update);

        log.debug("BotEvent object created successfully");
        return botEvent;
    }

    private void initUnsubscriptionObject(ChatMemberUpdated chatMemberUpdated) {
        log.debug("Initializing unsubscription object");
        User user = chatMemberUpdated.getFrom();
        this.id = user.getId();
        this.from = user;
        this.userName = user.getUserName();
        log.debug("Unsubscription object initialized");
    }

    private void initTelegramObject(Update update) {
        log.debug("Initializing telegram object based on update type");
        switch (eventType) {
            case MESSAGE -> initMessageObject(update.getMessage());
            case CALLBACK_QUERY -> initCallbackQueryObject(update.getCallbackQuery());
            case DEACTIVATION_QUERY -> initUnsubscriptionObject(update.getMyChatMember());
            case DOCUMENT -> initDocumentObject(update.getMessage());
        }
        log.debug("Telegram object initialized");
    }

    private void initMessageObject(Message message) {
        log.debug("Initializing message object");
        id = message.getChatId();
        userName = message.getFrom().getUserName();
        messageId = message.getMessageId();
        text = message.getText();
        from = message.getFrom();
        if (message.hasContact()) {
            contact = message.getContact();
            phoneNumber = contact.getPhoneNumber();
        }
        log.debug("Message object initialized");
    }

    private void initCallbackQueryObject(CallbackQuery callbackQuery) {
        log.debug("Initializing callback query object");
        id = callbackQuery.getFrom().getId();
        userName = callbackQuery.getFrom().getUserName();
        messageId = callbackQuery.getMessage().getMessageId();
        text = callbackQuery.getMessage().getText();
        data = callbackQuery.getData();
        from = callbackQuery.getFrom();
        log.debug("Callback query object initialized");
    }

    private void initDocumentObject(Message message) {
        log.trace("Initializing document object");
        document = message.getDocument();
        if (document != null) {
            fileName = document.getFileName();
        }
        id = message.getChatId();
        userName = message.getFrom().getUserName();
        messageId = message.getMessageId();
        from = message.getFrom();
        text = message.getText();
        log.trace("Document object initialized");
    }

    private static boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage();
    }

    private static boolean isCallbackWithData(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery == null) {
            return false;
        }
        String data = callbackQuery.getData();
        return data != null && !data.isEmpty();
    }

    private static boolean isDeactivationQuery(Update update) {
        if (update.hasMyChatMember()) {
            return update.getMyChatMember().getNewChatMember().getStatus().equals("kicked");
        }
        return false;
    }

    private static boolean isDocument(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasDocument();
    }
}
