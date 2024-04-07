package pro.sky.javacourse.AnimalShelterBot.service;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.TelegramBot;

@Component
public class BotSender {
        private final TelegramBot bot;

        public BotSender(TelegramBot bot) {
            this.bot = bot;
        }

        public void send(Long chatId, String text) {
            SendMessage message = new SendMessage(chatId, text);
            SendResponse response = bot.execute(message);
        }

    }