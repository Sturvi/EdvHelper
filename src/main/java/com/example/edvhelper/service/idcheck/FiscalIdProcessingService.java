package com.example.edvhelper.service.idcheck;

import com.example.edvhelper.exceptions.ReceiptImageNotFoundException;
import com.example.edvhelper.exceptions.VatRefundException;
import com.example.edvhelper.exceptions.ChequeExpiredException;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.ImageToTextService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FiscalIdProcessingService {
    private static final String VAT_REFUND = "vat refund date";
    private final ProxyService proxyService;
    private final FiscalDocumentService fiscalDocumentService;
    private final ImageToTextService imageToTextService;

    public FiscalIdProcessingService(ProxyService proxyService, FiscalDocumentService fiscalDocumentService, ImageToTextService imageToTextService) {
        this.proxyService = proxyService;
        this.fiscalDocumentService = fiscalDocumentService;
        this.imageToTextService = imageToTextService;
    }

    public void processFiscalId(FiscalId fiscalId) throws ReceiptImageNotFoundException, VatRefundException, ChequeExpiredException {
        File image = sendRequest(fiscalId.getFiscalId());


        if (image == null || !image.exists()) {
            throw new ReceiptImageNotFoundException("Unable to fetch or process image for fiscalId: " + fiscalId);
        }

        String chequeText = processFiscalIdImage(image);
        chequeText = chequeText.toLowerCase();

        if (chequeText.contains(VAT_REFUND)) {
            throw new VatRefundException("VAT on this receipt has already been refunded. VAT refunded fiscalId: " + fiscalId);
        }

        addChequeDate(fiscalId, chequeText);

        long days = ChronoUnit.DAYS.between(fiscalId.getChequeDate(), LocalDate.now());

        if (days > 90) {
            throw new ChequeExpiredException("The cheque with fiscalId: " + fiscalId + " has expired. Days since cheque date: " + days);
        }

        addReturnAmount(fiscalId, chequeText);
        fiscalId.setStatus(FiscalIdStatusEnum.UNUSED);
    }

    private void addReturnAmount(FiscalId fiscalId, String chequeText) {
        String[] lines = chequeText.split("\n");

        BigDecimal cashless = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (String line : lines) {
            if (line.contains("cashless:")) {
                cashless = new BigDecimal(line.split(":")[1].trim());
            }
            else if (line.contains("total tax =")) {
                totalTax = new BigDecimal(line.split("=")[1].trim());
            }
        }

        BigDecimal returnAmount = cashless.compareTo(BigDecimal.ZERO) > 0 ? totalTax.multiply(BigDecimal.valueOf(0.175)) : totalTax.multiply(BigDecimal.valueOf(0.05));

        fiscalId.setReturnAmount(returnAmount);
    }

    private void addChequeDate(FiscalId fiscalId, String chequeText) {
        Pattern pattern = Pattern.compile("cashier.*date: (\\d{2}\\.\\d{2}\\.\\d{4})");
        Matcher matcher = pattern.matcher(chequeText);

        if (matcher.find()) {
            String dateString = matcher.group(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            fiscalId.setChequeDate(LocalDate.parse(dateString, formatter));
        } else {
            fiscalId.setChequeDate(LocalDate.now());
        }
    }


    private File sendRequest(String fiscalId) {
        WebClient webClient = proxyService.getWebClient();
        if (webClient == null) {
            throw new IllegalStateException("WebClient is null. Ensure that the ProxyService is correctly configured.");
        }

        try {
            return fiscalDocumentService.fetchAndSaveDocument(webClient, fiscalId);
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while fetching and saving document for fiscalId: " + fiscalId, ex);
        }
    }

    private String processFiscalIdImage(File image) {
        return imageToTextService.extractText(image).toLowerCase();
    }
}
