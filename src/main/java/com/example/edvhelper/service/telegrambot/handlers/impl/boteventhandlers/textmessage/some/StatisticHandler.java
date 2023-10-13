package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.textmessage.some;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.service.FiscalIdService;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeTextCommandHandler;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticHandler implements SomeTextCommandHandler {
    private final FiscalIdService fiscalIdService;
    private final MessageService messageService;
    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        List<FiscalId> waitingList = fiscalIdService.getWaitingList(appUser);
        List<FiscalId> availableList = fiscalIdService.getAvailableFiscalId(appUser);

        BigDecimal waitingAmount = waitingList
                .stream()
                .map(FiscalId::getReturnAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableAmount = availableList
                .stream()
                .map(FiscalId::getReturnAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String messageText = "Чеков в ожидании: " + waitingList.size() + "\n" +
                "Примерная сумма в ожидании: " + waitingAmount + "\n" +
                "Доступно чеков для пробития: " + availableList.size() + "\n" +
                "Примерная доступная сумма: " + availableAmount;

        messageService.sendMessageToUser(botEvent.getId(), messageText);
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.STATISTICS;
    }
}
