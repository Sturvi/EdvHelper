package com.example.edvhelper.service.telegrambot.handlers.impl.boteventhandlers.textmessage.some;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import com.example.edvhelper.service.FiscalIdService;
import com.example.edvhelper.service.QRCodeService;
import com.example.edvhelper.service.telegrambot.BotEvent;
import com.example.edvhelper.service.telegrambot.commands.TextCommandsEnum;
import com.example.edvhelper.service.telegrambot.handlers.interfaces.SomeTextCommandHandler;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import com.example.edvhelper.service.telegrambot.message.keyboards.InlineKeyboardMarkupFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBestSecretHandler implements SomeTextCommandHandler {
    private final FiscalIdService fiscalIdService;
    private final MessageService messageService;
    private final QRCodeService qrCodeService;

    @Override
    public void handle(BotEvent botEvent, AppUser appUser) {
        if (appUser.getTelegramChatId().equals(526369804L)){
            var getAvaibleCount = fiscalIdService.getAvaibleSecretCount(appUser);
            var getAvaibleSum = fiscalIdService.getAvaibleSecretSum(appUser);

            if (getAvaibleCount > 0) {
                var fiscalId = fiscalIdService.getFiscalIdWithMaxReturnAmount(appUser);

                if (fiscalId.isPresent()) {
                    FiscalId bestSecret = fiscalId.get();
                    var qrCode = qrCodeService.generateQRCode(bestSecret.getFiscalId());
                    StringBuilder messageText = new StringBuilder();
                    messageText.append("Количество доступных секретов: ").append(getAvaibleCount - 1).append("\n")
                                    .append("Сумма доступных секретов: ").append(getAvaibleSum.subtract(bestSecret.getReturnAmount())).append("\n\n");
                    String fiscalIdMessageText = "<pre>" + bestSecret.getFiscalId() + "</pre>";

                    var keyboard = InlineKeyboardMarkupFactory.getNextKeyboard("secret");

                    messageService.sendMessageToUser(botEvent.getId(), messageText.toString());
                    messageService.sendPhoto(botEvent.getId(), qrCode.getPath(), "");
                    messageService.sendMessageWithKeyboard(botEvent.getId(), fiscalIdMessageText, keyboard);

                    bestSecret.setStatus(FiscalIdStatusEnum.FROM_API_USED);
                    fiscalIdService.save(bestSecret);
                } else {
                    messageService.sendMessageToUser(botEvent.getId(), "Нет доступных секретов");
                }
            }

        }
    }

    @Override
    public TextCommandsEnum availableFor() {
        return TextCommandsEnum.GET_BEST_SECRET;
    }
}
