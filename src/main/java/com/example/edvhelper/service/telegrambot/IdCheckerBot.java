package com.example.edvhelper.service.telegrambot;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.AppUserRoleEnum;
import com.example.edvhelper.service.AppUserService;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeBotEventHandler;
import com.example.edvhelper.service.telegrambot.message.MessageEvent;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
@PropertySource("classpath:secret.properties")
public class IdCheckerBot extends TelegramLongPollingBot {

    private final List<SomeBotEventHandler> handlers;
    private Map<BotEventType, BiConsumer<BotEvent, AppUser>> botEventHandler;
    private final String botUsername;
    private final AppUserService appUserService;
    private final MessageService messageService;

    public IdCheckerBot(@Value("${bot.token}") String botToken,
                        List<SomeBotEventHandler> handlers, @Value("${bot.username}") String botUsername, AppUserService appUserService, MessageService messageService) {
        super(botToken);
        this.handlers = handlers;
        this.botUsername = botUsername;
        this.appUserService = appUserService;
        this.messageService = messageService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        BotEvent botEvent = BotEvent.getTelegramObject(update);
        var appUser = appUserService.saveOrUpdateAppUser(botEvent.getFrom());

        try {
            if (appUser.getRole() == AppUserRoleEnum.ADMIN) {
                var eventEnum = botEvent.getEventType();

                if (BotEventType.DOCUMENT == eventEnum) {
                    botEvent.setFileUrl(downloadFileFromDocument(botEvent.getDocument()));
                }

                var handler = botEventHandler.get(eventEnum);

                handler.accept(botEvent, appUser);
            } else {
                messageService.sendMessageToUser(botEvent.getId(), "Только глупые люди могут писать: \"" + botEvent.getText() + "\"");
            }
        } catch (Exception e) {
            log.error("Ошибка " + e + " при обработке запроса " + update);
        }

        appUserService.save(appUser);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public URL downloadFileFromDocument(Document document) {
        try {
            String fileId = document.getFileId();
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);

            File file = execute(getFileMethod);
            String filePath = file.getFilePath();

            return new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);

        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostConstruct
    private void init() {

        botEventHandler = handlers.stream().collect(Collectors.toMap(
                SomeBotEventHandler::availableFor,
                element -> element::handle
        ));
    }

    @EventListener
    public void handleMessageEvent(MessageEvent<?> event) {
        log.trace("Handling message event: {}", event);

        try {
            switch (event.getMessageType()) {
                case SEND_MESSAGE -> {
                    log.trace("Processing SEND_MESSAGE");
                    event.setResponse(execute(event.getSendMessage()));
                }
                case EDIT_MESSAGE_TEXT -> {
                    log.trace("Processing EDIT_MESSAGE_TEXT");
                    execute(event.getEditMessageText());
                }
                case SEND_AUDIO -> {
                    log.trace("Processing SEND_AUDIO");
                    event.setResponse(execute(event.getSendAudio()));
                }
                case DELETE_MESSAGE -> {
                    log.trace("Processing DELETE_MESSAGE");
                    execute(event.getDeleteMessage());
                }
                case SEND_PHOTO -> {
                    log.trace("Processing SEND_PHOTO");
                    event.setResponse(execute(event.getSendPhoto()));
                }
                case SEND_DOCUMENT -> {
                    log.trace("Processing SEND_DOCUMENT");
                    event.setResponse(execute(event.getSendDocument()));
                }
            }
        } catch (TelegramApiException e) {
            log.error("Error handling message event", e);
        }
    }
}
