package com.example.edvhelper.service.telegrambot.commands;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum TextCommandsEnum {
    START("/start"),
    GET("/get"),
    STATISTICS("/statistic");

    private final String command;

    TextCommandsEnum(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    private static final Map<String, TextCommandsEnum> commands =
            Arrays.stream(TextCommandsEnum.values())
                    .collect(Collectors.toMap(
                            TextCommandsEnum::getCommand,
                            element -> element
                    ));

    public static TextCommandsEnum fromString(String textCommand) {
        return commands.get(textCommand);
    }
}
