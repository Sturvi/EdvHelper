package com.example.edvhelper.service.telegrambot.handlers.interfaces;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.BotEvent;

public interface SomeTextCommandHandler {

    void handle(BotEvent botEvent, AppUser appUser);
    TextCommandsEnum availableFor();
}
