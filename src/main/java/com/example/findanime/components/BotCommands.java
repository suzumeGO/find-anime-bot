package com.example.findanime.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "bot info")
    );

    String HELP_TEXT = "Добро пожаловать в бот для просмотра прогноза погоды. " +
            "Для использования данного бота отправьте свою геолокацию в сообщении.";
}
