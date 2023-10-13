package com.example.edvhelper.repository;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.AppUserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramChatId(Long telegramChatId);

    List<AppUser> findAllByRole(AppUserRoleEnum role);
}
