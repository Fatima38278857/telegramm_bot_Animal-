package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
// Наследуем TelegramLongPollingBot - абстрактный класс Telegram API
public class TelegramBot extends TelegramLongPollingBot {
    private final String botUserName;
    private String botToken;
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String WELCOME = "Привет!";
    private final String ALTERNATIVE_TEXT = "Неизвестная команда!";

    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${spring.datasource.username}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        User user = message.getFrom();
        Long chatId = update.getMessage().getChatId();
        logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote " + message.getText());

        switch (message.getText()) {
            case "/start" -> sendText(chatId, WELCOME);
            case "/help" -> sendText(chatId, WELCOME);
            default -> sendText(chatId, ALTERNATIVE_TEXT);
        }
    }

    public void sendText(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    // getBotToken() is deprecated
}
