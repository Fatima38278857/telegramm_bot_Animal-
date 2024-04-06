package pro.sky.javacourse.AnimalShelterBot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.service.BotSender;

import java.util.List;

@Service
public class BotListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(BotListener.class);

    private final TelegramBot telegramBot;
    private final BotSender botSender;
    private final String WELCOMING_TEXT = "Message received";
    private final String ALTERNATIVE_TEXT = "Unknown command";

    public BotListener(TelegramBot telegramBot, BotSender botSender) {
        this.telegramBot = telegramBot;
        this.botSender = botSender;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            String message = update.message().text();
            Long chatId = update.message().chat().id();
            logger.info("Received: " + message);

            if (message.equals("/start") || message.equals("/help")) {
                botSender.send(chatId, WELCOMING_TEXT);
            } else {
                botSender.send(chatId, ALTERNATIVE_TEXT);
            }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
