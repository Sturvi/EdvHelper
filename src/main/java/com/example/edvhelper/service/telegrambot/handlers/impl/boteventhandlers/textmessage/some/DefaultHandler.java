package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.textmessage.some;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.FiscalIdHandlerService;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeTextCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultHandler implements SomeTextCommandHandler {
    private final FiscalIdHandlerService fiscalIdHandlerService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        String messageText = botEvent.getText().trim();
        fiscalIdHandlerService.handleFiscalId(messageText, appUser);
    }

    @Override
    public TextCommandsEnum availableFor() {
        return null;
    }
}
