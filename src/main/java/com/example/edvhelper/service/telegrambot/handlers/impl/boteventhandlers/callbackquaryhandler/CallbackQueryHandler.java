package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.callbackquaryhandler;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.telegrambot.commands.CallbackQueryDataEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeBotEventHandler;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeCallbackQueryHandler;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.BotEventType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CallbackQueryHandler implements SomeBotEventHandler {
    private final List<SomeCallbackQueryHandler> handlers;
    private Map<CallbackQueryDataEnum, BiConsumer<BotEvent, AppUser>> callbackQueryCommandsHandler;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        CallbackQueryDataEnum incomingCommand = CallbackQueryDataEnum.fromData(botEvent.getData());

        var handlerMethod = callbackQueryCommandsHandler.get(incomingCommand);

        if (handlerMethod != null) {
            handlerMethod.accept(botEvent, appUser);
        }
    }

    @Override
    public BotEventType availableFor() {
        return BotEventType.CALLBACK_QUERY;
    }

    @PostConstruct
    private void init() {
        callbackQueryCommandsHandler = handlers.stream().collect(Collectors.toMap(
                SomeCallbackQueryHandler::availableFor,
                element -> element::handle
        ));
    }
}
