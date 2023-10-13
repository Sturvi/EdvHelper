package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.textmessage.some;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeTextCommandHandler;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartHandler implements SomeTextCommandHandler {
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        messageService.sendMessageToUser(botEvent.getId(), "Текстовый ответ на старт");

    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.START;
    }
}
