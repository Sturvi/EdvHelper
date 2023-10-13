package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.textmessage.some;

import com.example.edvhelper.exceptions.ChequeExpiredException;
import com.example.edvhelper.exceptions.ReceiptImageNotFoundException;
import com.example.edvhelper.exceptions.VatRefundException;
import com.example.edvhelper.mapper.FiscalIdMapper;
import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.FiscalIdService;
import com.example.edvhelper.service.idcheck.FiscalIdProcessingService;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeTextCommandHandler;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultHandler implements SomeTextCommandHandler {
    private static final Pattern COMPILED_PATTERN = Pattern.compile("^[a-zA-Z0-9]{12}$");
    private final FiscalIdMapper fiscalIdMapper;
    private final FiscalIdService fiscalIdService;
    private final MessageService messageService;
    private final FiscalIdProcessingService fiscalIdProcessingService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        String messageText = botEvent.getText().trim();

        if (messageText.contains("monitoring.e-kassa.gov.az")) {
            int index = messageText.indexOf('=');
            if (index != -1) {
                messageText = messageText.substring(index + 1, index + 13);
            }
        }

        Matcher matcher = COMPILED_PATTERN.matcher(messageText);

        if (matcher.matches()) {
            var newFiscalId = fiscalIdMapper.creatNewFiscalId(appUser, messageText);

            try {
                fiscalIdProcessingService.processFiscalId(newFiscalId);

                newFiscalId.setStatus(FiscalIdStatusEnum.UNUSED);
                messageService.sendMessageToUser(
                        botEvent.getId(),
                        "Чек добавлен. \n" +
                                "Дата возвращения НДС: " + newFiscalId.getChequeDate().plusDays(30) +
                                "\nПреполагаемая сумма возврата: " + newFiscalId.getReturnAmount() +
                                "\n\n*Сумма посчитана примерно и может вообще не соответствовать действительности.");
            } catch (ReceiptImageNotFoundException e) {
                messageService.sendMessageToUser(
                        botEvent.getId(),
                        "Возникла ошибка при попытке получения данных о данном чеке: " + newFiscalId + " из портала мониторинга. Мы повторим попытку чуть позже.");

                log.error(e.getMessage());
            } catch (VatRefundException e) {
                messageService.sendMessageToUser(
                        botEvent.getId(),
                        "НДС по чеку: " + newFiscalId + " уже возвращен.");
                return;
            } catch (ChequeExpiredException e) {
                messageService.sendMessageToUser(
                        botEvent.getId(),
                        "С пробития чека " + newFiscalId + " прошло более 90 дней. Чек просрочен. ");
                return;
            }

            fiscalIdService.save(newFiscalId);
        }
    }

    @Override
    public TextCommandsEnum availableFor() {
        return null;
    }
}
