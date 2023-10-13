package com.example.edvhelper.mapper;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.AppUserRoleEnum;
import com.example.edvhelper.model.AppUserStateEnum;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class AppUserMapper {

    public AppUser mapNewUserToUserEntity(User user) {
        AppUser newUser = AppUser.builder()
                .telegramChatId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUserName())
                .userStatus(true)
                .userState(AppUserStateEnum.MAIN)
                .role(AppUserRoleEnum.USER)
                .build();

        return newUser;
    }


    /**
     * Updates an existing UserEntity entity with data from a Telegram user.
     *
     * @param user       Telegram user object
     * @param userEntity User entity that needs to be updated
     */
    public void updateExistingUserEntityFromTelegramUser(User user, AppUser userEntity) {
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUserName());
        userEntity.setUserStatus(true);
    }
}
