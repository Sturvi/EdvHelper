package com.example.edvhelper.service.idcheck;

import com.example.edvhelper.exceptions.ChequeExpiredException;
import com.example.edvhelper.exceptions.ReceiptImageNotFoundException;
import com.example.edvhelper.exceptions.VatRefundException;
import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.FiscalIdService;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpRequestScheduler {

    private final FiscalIdService fiscalIdService;
    private final FiscalIdProcessingService fiscalIdProcessingService;
    private final MessageService messageService;

    @Scheduled(fixedRate = 1800000)
    private void reCheck() {
        var fiscalIdOpt = fiscalIdService.getByStatus(FiscalIdStatusEnum.NEW);

        fiscalIdOpt.ifPresent(fiscalId -> {
            log.trace("Processing fiscalId: {}", fiscalId.getId());

            try {
                fiscalIdProcessingService.processFiscalId(fiscalId);
            } catch (ReceiptImageNotFoundException e) {
                fiscalId.setStatus(FiscalIdStatusEnum.NEW);
                log.error(e.getMessage());
            } catch (VatRefundException | ChequeExpiredException e) {
                fiscalId.setStatus(FiscalIdStatusEnum.USED);
            } finally {
                fiscalIdService.save(fiscalId);
            }
        });
    }

    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Baku")
    public void sendNotification () {
        Map<AppUser, BigDecimal> amountForNotification = fiscalIdService.getAllAvailableFiscalId()
                .stream()
                .collect(Collectors.groupingBy(FiscalId::getAppUser,
                        Collectors.mapping(FiscalId::getReturnAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        amountForNotification.forEach((appUser, amount) -> messageService.sendMessageToUser(appUser.getTelegramChatId(),
                "У вас есть непробитых чеков на примерную сумму: " + amount));

    }

    @Scheduled(cron = "0 0 21 * * ?", zone = "Asia/Baku")
    public void sendAlertNotification () {
        String messageText = "Осторожно! У вас есть FiscalId которые скоро могут сгореть";
        Set<Long> amountForNotification = fiscalIdService.getExpiringFiscalId()
                .stream()
                .map(id -> id.getAppUser().getTelegramChatId())
                        .collect(Collectors.toSet());


        amountForNotification.forEach(chatId -> messageService.sendMessageToUser(chatId, messageText));
    }
}