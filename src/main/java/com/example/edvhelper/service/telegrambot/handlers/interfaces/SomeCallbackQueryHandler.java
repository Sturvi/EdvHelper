package com.example.edvhelper.service.telegrambot.handlers.interfaces;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.commands.CallbackQueryDataEnum;

public interface SomeCallbackQueryHandler {
    void handle(BotEvent botEvent, AppUser appUser);
    CallbackQueryDataEnum availableFor();
}
