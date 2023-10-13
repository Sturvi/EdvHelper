package com.example.edvhelper.configurations;

import com.example.edvhelper.service.telegrambot.IdCheckerBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class BotConfigurations {

    private final IdCheckerBot idCheckerBot;

    @Bean
    public TelegramBotsApi idCheckerTelegramBot () {
        try {
            log.info("Creating and registering the bot with the API.");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(idCheckerBot);
            return botsApi;
        } catch (Exception e) {
            log.error("Error encountered while registering the bot.", e);
            throw new IllegalStateException(e);
        }
    }
}
