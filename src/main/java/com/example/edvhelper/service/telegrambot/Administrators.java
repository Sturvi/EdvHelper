package com.example.edvhelper.service.telegrambot;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.service.AppUserService;
import com.example.edvhelper.service.telegrambot.message.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class Administrators {
    private final AppUserService appUserService;
    private final MessageService messageService;

    private List<AppUser> getAdministratorsList (){
        return appUserService.getAdministratorsList();
    }

    public void messageToAdmins (String messageText) {
        getAdministratorsList()
                .stream()
                .map(AppUser::getTelegramChatId)
                .forEach(id -> messageService.sendMessageToUser(id, messageText));

    }
}
