package com.example.edvhelper.service;

import com.example.edvhelper.exceptions.ChequeExpiredException;
import com.example.edvhelper.exceptions.ReceiptImageNotFoundException;
import com.example.edvhelper.exceptions.VatRefundException;
import com.example.edvhelper.mapper.FiscalIdMapper;
import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.idcheck.FiscalIdProcessingService;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FiscalIdHandlerService {

    private final FiscalIdMapper fiscalIdMapper;
    private final FiscalIdProcessingService fiscalIdProcessingService;
    private final MessageService messageService;
    private final FiscalIdService fiscalIdService;

    private static final Pattern COMPILED_PATTERN = Pattern.compile("^[a-zA-Z0-9]{12}$");
    private static final int VAT_REFUND_DAYS = 30;
    private static final int CHEQUE_EXPIRATION_DAYS = 90;

    public void handleFiscalId(String messageText, AppUser appUser) {
        if (messageText.contains("monitoring.e-kassa.gov.az")) {
            messageText = extractParameterValue(messageText);
        }

        Matcher matcher = COMPILED_PATTERN.matcher(messageText);

        if (matcher.matches()) {

            if (fiscalIdService.getFiscalId(messageText).isPresent()) {
                messageService.sendMessageToUser(appUser.getTelegramChatId(), "Данный Id уже добавлен в Базу данных!");
                return;
            }

            var newFiscalId = fiscalIdMapper.creatNewFiscalId(appUser, messageText);

            try {
                fiscalIdProcessingService.processFiscalId(newFiscalId);

                newFiscalId.setStatus(FiscalIdStatusEnum.UNUSED);
                sendMessage(appUser,
                        "Чек добавлен. \n" +
                                "Дата возвращения НДС: " + newFiscalId.getChequeDate().plusDays(VAT_REFUND_DAYS) +
                                "\nПреполагаемая сумма возврата: " + newFiscalId.getReturnAmount() +
                                "\n\n*Сумма посчитана примерно и может вообще не соответствовать действительности.");
            } catch (ReceiptImageNotFoundException e) {
                sendMessage(appUser,
                        "Возникла ошибка при попытке получения данных о данном чеке: " + newFiscalId + " из портала мониторинга. Мы повторим попытку чуть позже.");

            } catch (VatRefundException e) {
                sendMessage(appUser,
                        "НДС по чеку: " + newFiscalId + " уже возвращен.");
                return;
            } catch (ChequeExpiredException e) {
                sendMessage(appUser,
                        "С пробития чека " + newFiscalId + " прошло более " + CHEQUE_EXPIRATION_DAYS + " дней. Чек просрочен. ");
                return;
            }

            fiscalIdService.save(newFiscalId);
        }
    }

    private void sendMessage(AppUser appUser, String message) {
        messageService.sendMessageToUser(appUser.getTelegramChatId(), message);
    }

    private String extractParameterValue(String url) {
        int index = url.indexOf('=');
        if (index != -1) {
            return url.substring(index + 1, index + 13);
        }
        return null;
    }
}
