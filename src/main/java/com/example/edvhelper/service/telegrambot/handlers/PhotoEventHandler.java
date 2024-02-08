package com.example.edvhelper.service.telegrambot.handlers;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.FiscalIdHandlerService;
import com.example.edvhelper.service.QRCodeReaderComponent;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.BotEventType;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeBotEventHandler;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoEventHandler implements SomeBotEventHandler {
    private final QRCodeReaderComponent qrCodeReader;
    private final FiscalIdHandlerService fiscalIdHandlerService;
    private final MessageService messageService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        var textInQRCode = qrCodeReader.readQRCode(botEvent.getFile());

        if (textInQRCode == null || textInQRCode.isEmpty()) {
            messageService.sendMessageToUser(botEvent.getId(), "Не удалось обработать QR-Код. Попробуйте снова или пришлите FiscalId в виде текста.");
            return;
        }

        if (textInQRCode.contains("monitoring.e-kassa.gov.az")) {
            fiscalIdHandlerService.handleFiscalId(textInQRCode, appUser);
        } else {
            messageService.sendMessageToUser(botEvent.getId(), "В QR-код не удалось обнаружить FiscalId. Возможно неверный QR-код.");
        }
    }

    @Override
    public BotEventType availableFor() {
        return BotEventType.PHOTO;
    }
}
