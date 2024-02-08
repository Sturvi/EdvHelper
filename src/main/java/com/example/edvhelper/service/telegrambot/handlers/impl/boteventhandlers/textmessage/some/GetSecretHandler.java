package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.textmessage.some;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.FiscalIdService;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeTextCommandHandler;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetSecretHandler implements SomeTextCommandHandler {
    private final FiscalIdService fiscalIdService;
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        if (appUser.getTelegramChatId().equals(526369804L)){
            var fiscalIdList = fiscalIdService.getByAppUserStatusAndDate(appUser, FiscalIdStatusEnum.FROM_API_UNUSED, LocalDate.now().minusDays(87));

            if (!fiscalIdList.isEmpty()) {
                StringBuilder messageText = new StringBuilder();

                BigDecimal amount = fiscalIdList.stream()
                        .peek(id -> id.setStatus(FiscalIdStatusEnum.FROM_API_USED))
                        .peek(id -> messageText.append("<pre>").append(id.getFiscalId()).append("</pre>").append("\n"))
                        .peek(fiscalIdService::save)
                        .map(FiscalId::getReturnAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                String messageHeader = "Список доступных для загрузки FiscalId.\nПримерная сумма которая вернется: " + amount + "\n";

                messageText.insert(0, messageHeader);

                messageService.sendMessageToUser(botEvent.getId(), messageText.toString());
            } else {
                messageService.sendMessageToUser(botEvent.getId(), "В данный момент нет доступных Id");
            }


        }

    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.GET_SECRET;
    }
}
