package com.example.edvhelper.service.idcheck;

import com.example.edvhelper.exceptions.ChequeExpiredException;
import com.example.edvhelper.exceptions.ReceiptImageNotFoundException;
import com.example.edvhelper.exceptions.VatRefundException;
import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.AppUserService;
import com.example.edvhelper.service.FiscalIdService;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpRequestScheduler {

    private final FiscalIdService fiscalIdService;
    private final FiscalIdProcessingService fiscalIdProcessingService;
    private final MessageService messageService;
    private final ProxyService proxyService;
    private final AppUserService appUserService;

    @Scheduled(fixedRate = 1800000)
    private void processNewFiscalIds() {
        log.info("Starting to process fiscal IDs with status NEW");
        processFiscalIds(FiscalIdStatusEnum.NEW);
        log.info("Finished processing fiscal IDs with status NEW");
    }

    @Scheduled(fixedRate = 1800000)
    private void processApiNewFiscalIds() {
        log.info("Starting to process fiscal IDs with status FROM_API_NEW");
        processFiscalIds(FiscalIdStatusEnum.FROM_API_NEW);
        log.info("Finished processing fiscal IDs with status FROM_API_NEW");
    }

    private void processFiscalIds(FiscalIdStatusEnum status) {
        fiscalIdService.getByStatus(status)
                .forEach(fiscalId -> {
                    processSingleFiscalId(fiscalId, status);
                });
    }

    private void processSingleFiscalId(FiscalId fiscalId, FiscalIdStatusEnum status) {
        log.trace("Processing fiscalId: {}", fiscalId.getId());

        try {
            fiscalIdProcessingService.processFiscalId(fiscalId);
        } catch (ReceiptImageNotFoundException e) {
            fiscalId.setStatus(status);
            log.error("Receipt image not found: {}", e.getMessage());
        } catch (VatRefundException | ChequeExpiredException e) {
            fiscalId.setStatus(FiscalIdStatusEnum.USED);
            log.warn("VAT refund or cheque expired: {}", e.getMessage());
        } finally {
            fiscalIdService.save(fiscalId);
        }
    }

    @Scheduled(fixedRate = 1800000)
    public void performTask() {
        log.trace("Scheduled task started");

        Optional<AppUser> appUserOptional = appUserService.getAppUser(526369804L);

        if (appUserOptional.isPresent()) {
            log.debug("AppUser is present, ID: {}", 526369804L);

            WebClient webClient = WebClient.create();
            ResponseEntity<List> response = webClient.get()
                    .uri("http://10.8.0.1:8081/fiscals/526369804")
                    .retrieve()
                    .toEntity(List.class)
                    .block();

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                log.info("API call successful, Status Code: {}", response.getStatusCode());

                List<String> idList = response.getBody();

                if (idList == null) {
                    log.warn("Received null idList from API");
                    return;
                }

                var ficalIdList = idList.stream()
                        .map(id -> id.substring(0, 12))
                        .map(idString -> FiscalId
                                .builder()
                                .appUser(appUserOptional.get())
                                .fiscalId(idString)
                                .status(FiscalIdStatusEnum.FROM_API_NEW)
                                .build())
                        .peek(fiscalId -> {
                            fiscalIdService.save(fiscalId);
                            log.debug("Saved new FiscalId: {}", fiscalId.getFiscalId());
                        })
                        .toList();

                ficalIdList.stream()
                        .peek(id -> {
                            processSingleFiscalId(id, FiscalIdStatusEnum.FROM_API_NEW);
                            log.trace("Processed FiscalId: {}", id.getFiscalId());
                        })
                        .peek(id -> {
                            if (id.getStatus() == FiscalIdStatusEnum.UNUSED) {
                                id.setStatus(FiscalIdStatusEnum.FROM_API_UNUSED);
                                log.info("Status changed to FROM_API_UNUSED for FiscalId: {}", id.getFiscalId());
                            }
                        })
                        .forEach(fiscalId -> {
                            fiscalIdService.save(fiscalId);
                            log.debug("Updated FiscalId: {}", fiscalId.getFiscalId());
                        });
            } else {
                log.error("API call unsuccessful, Status Code: {}", response != null ? response.getStatusCode() : "null");
            }
        } else {
            log.warn("AppUser is not present, ID: {}", 526369804L);
        }

        log.trace("Scheduled task ended");
    }

    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Baku")
    public void sendNotification() {
        Map<AppUser, BigDecimal> amountForNotification = fiscalIdService.getAllAvailableFiscalId()
                .stream()
                .collect(Collectors.groupingBy(FiscalId::getAppUser,
                        Collectors.mapping(FiscalId::getReturnAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        amountForNotification.forEach((appUser, amount) -> messageService.sendMessageToUser(appUser.getTelegramChatId(),
                "У вас есть непробитых чеков на примерную сумму: " + amount));

    }

    @Scheduled(cron = "0 0 21 * * ?", zone = "Asia/Baku")
    public void sendAlertNotification() {
        String messageText = "Осторожно! У вас есть FiscalId которые скоро могут сгореть";
        Set<Long> amountForNotification = fiscalIdService.getExpiringFiscalId()
                .stream()
                .map(id -> id.getAppUser().getTelegramChatId())
                .collect(Collectors.toSet());


        amountForNotification.forEach(chatId -> messageService.sendMessageToUser(chatId, messageText));
    }
}