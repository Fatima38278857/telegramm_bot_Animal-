package com.example.ShelterBot.bot.implementation;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 *
 */
@Slf4j
@Component
public class ImplementationBot extends TelegramLongPollingBot {

    private final Logger logger = LoggerFactory.getLogger(ImplementationBot.class);
    @Value("${bot.name}")
    private String nameBot;


    public ImplementationBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }


    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */

    // В методе onUpdateReceived  будету обрабатывать полученные сообщения и фотографии.
    //if (update.hasMessage() && update.getMessage().hasPhoto()) {
    //     Здесь логика по извлечению фото и отправке его в базу данных
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();




            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    sendMessage(chatId, "Sorry, command was not recognized");
            }
        }
    }

    public void startCommandReceived(long chatId, String text) {
        String answer = "Привет пользователь";
        sendMessage(chatId, answer);

    }
        /**
         * Метод для определения, зарегестрирован ли пользователь
         *
         * @param msg Данные о пользователе
         */
//    private void registerUser(Message msg) {
//        if (userRepository.findById(msg.getChatId()).isEmpty()) {
//
//            var chatId = msg.getChatId();
//            var chat = msg.getChat();
//
//            User user = new User();
//
//            user.setChatId(chatId);
//            user.setFirstName(chat.getFirstName());
//            user.setLastName(chat.getLastName());
//            user.setName(chat.getUserName());
//            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
//
//            userRepository.save(user);
//            logger.info("user saved: " + user);
//        }
//    }



        /**
         * Метод для настройки сообщения и его отправки.
         *
         * @param chatId     id чата
         * @param textToSend Строка, которую необходимот отправить в качестве сообщения.
         */
        public void sendMessage ( long chatId, String textToSend){
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(textToSend);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                logger.error("Отправить сообщение не удалось" + textToSend);
            }
        }


        @Override
        public String getBotUsername () {
            return nameBot;
        }
    }

