package com.example.edvhelper.service;


import com.example.edvhelper.mapper.AppUserMapper;
import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.AppUserRoleEnum;
import com.example.edvhelper.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AppUser saveOrUpdateAppUser(User user) {
        log.trace("Saving or updating AppUser");

        AppUser appUser = appUserRepository.findByTelegramChatId(user.getId()).map(existingUserEntity -> {
            log.debug("Updating existing AppUser in the database");
            updateAppUserInDataBase(user, existingUserEntity);
            return appUserRepository.save(existingUserEntity);
        }).orElseGet(() -> {
            log.debug("Saving new AppUser to the database");
            return saveAppUser(user);
        });

        log.trace("AppUser saved or updated successfully");

        return appUser;
    }

    public Optional<AppUser> getAppUser (Long telegramChatId) {
        return appUserRepository.findByTelegramChatId(telegramChatId);
    }

    private void updateAppUserInDataBase(User user, AppUser userEntity) {
        log.trace("Updating AppUser in the database");
        log.debug("Updating user entity from Telegram user: {}", user);
        appUserMapper.updateExistingUserEntityFromTelegramUser(user, userEntity);
        log.debug("Updated user entity: {}", userEntity);
        log.trace("AppUser updated successfully");
    }

    private AppUser saveAppUser(User user) {
        log.trace("Saving AppUser");
        AppUser appUser = appUserRepository.saveAndFlush(appUserMapper.mapNewUserToUserEntity(user));
        log.debug("AppUser saved successfully");
        return appUser;
    }

    public void save(AppUser appUser) {
        appUserRepository.save(appUser);
    }

    public List<AppUser> getAdministratorsList () {
        return appUserRepository.findAllByRole(AppUserRoleEnum.ADMIN);
    }
}
