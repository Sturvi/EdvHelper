package com.example.edvhelper.service.telegrambot.handlers.interfaces;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.BotEventType;

public interface SomeBotEventHandler {

    void handle(BotEvent botEvent, AppUser appUser);
    BotEventType availableFor();
}
