package com.example.findanime;

import com.example.findanime.DTO.AnimeInfo;
import com.example.findanime.annotations.BotCommandHandler;
import com.example.findanime.components.BotCommands;
import com.example.findanime.config.BotConfig;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot implements BotCommands {
    private final BotConfig config;
    private final AnimeProxy proxy;

    public Bot(BotConfig config, AnimeProxy proxy) {
        this.config = config;
        this.proxy = proxy;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId;
        String receivedMessage;
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            var photo = update.getMessage().getPhoto().get(update.getMessage().getPhoto().size() - 1).getFileId();

        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            receivedMessage = update.getCallbackQuery().getData();
            answerUtils(receivedMessage, chatId);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            receivedMessage = update.getMessage().getText();
            if (LIST_OF_COMMANDS.stream().anyMatch(x -> x.getCommand().equals(receivedMessage))) {
                answerUtils(receivedMessage, chatId);
            } else {
                getAnimeByUrl(receivedMessage, chatId);
            }
        }
    }

    private void getAnimeByUrl(String url, long chatId) {
        AnimeInfo animeInfo = proxy.getAnimeByUrl(url);
        formAnimeDescription(animeInfo).forEach(el -> {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(el);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        });
    }

    private List<String> formAnimeDescription(AnimeInfo animeInfo) {
        List<String> messages = new ArrayList<>();
        animeInfo.getResult().forEach(
                similar -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Anilist ID: ").append(similar.getAnilist())
                            .append("\n").append("Название: ").append(similar.getFilename())
                            .append("\n").append("Эпизод: ").append(similar.getEpisode())
                            .append("\n").append("Тайм-код: ").append(setFormat(similar.getFrom())).append("-").append(setFormat(similar.getTo()))
                            .append("\n").append("Соответствие: ").append(similar.getSimilarity())
                            .append("\n").append("Видео: ").append(similar.getVideo())
                            // .append("\n").append("Изображение: ").append(similar.getImage())
                            .append("\n");
                    messages.add(stringBuilder.toString());
                });
        return messages;
    }

    private String setFormat(double time) {
        int h, min, sec;
        String hStr, minStr, secStr;
        double res = time / 3600;
        double drob = res % 1;
        h = (int) res;
        res = drob * 60;
        drob = res % 1;
        min = (int) res;
        res = drob * 60;
        sec = (int) Math.round(res);
        hStr = String.valueOf(h);
        minStr = String.valueOf(min);
        secStr = String.valueOf(sec);
        if (h < 10) {
            hStr = 0 + hStr;
        }
        if (min < 10) {
            minStr = 0 + minStr;
        }
        if (sec < 10) {
            secStr = 0 + secStr;
        }
        return h == 0 ? minStr + ":" + secStr : hStr + ":" + minStr + ":" + secStr;
    }

    private void answerUtils(String messageText, long chatId) {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(BotCommandHandler.class)) {
                BotCommandHandler commandHandler = method.getAnnotation(BotCommandHandler.class);
                if (commandHandler.value().equals(messageText)) {
                    try {
                        method.invoke(this, chatId);
                        return;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
    }

    @BotCommandHandler(value = "/help")
    private void helpCommandHandler(long chatId) {
        log.info("help command was used");
        SendMessage message = new SendMessage();
        message.setText(HELP_TEXT);
        message.setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @BotCommandHandler(value = "/start")
    private void startCommandHandler(long chatId) {
        log.info("start command was used");
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Отправьте URL изображения или загрузите его");
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
