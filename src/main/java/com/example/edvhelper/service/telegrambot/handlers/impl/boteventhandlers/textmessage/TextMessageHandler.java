package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.textmessage;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeBotEventHandler;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeTextCommandHandler;
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
public class TextMessageHandler implements SomeBotEventHandler {
    private final List<SomeTextCommandHandler> handlers;
    private Map<TextCommandsEnum, BiConsumer<BotEvent, AppUser>> textCommandsHandler;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        TextCommandsEnum incomingCommand = TextCommandsEnum.fromString(botEvent.getText());

        var handlerMethod = textCommandsHandler.get(incomingCommand);

        handlerMethod.accept(botEvent, appUser);
    }

    @Override
    public BotEventType availableFor() {
        return BotEventType.MESSAGE;
    }

    @PostConstruct
    private void init() {

        textCommandsHandler = handlers.stream().collect(Collectors.toMap(
                SomeTextCommandHandler::availableFor,
                element -> element::handle
        ));
    }
}
