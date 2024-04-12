package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.sky.javacourse.AnimalShelterBot.model.menu.BotKeyboardState;

import java.util.ArrayList;
import java.util.List;

@Component
// Наследуем TelegramLongPollingBot - абстрактный класс Telegram API
public class TelegramBot extends TelegramLongPollingBot {
    private final String botUserName;
    private String botToken;
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String WELCOME = "Привет!\n\nЭто телеграм бот для поиска питомца. " +
            "Наша команда волонтеров будет счастлива, если удастся найти новый дом для любого из наших подопечных! " +
            "\nПожалуйста, выберите приют.";
    private final String ALTERNATIVE_TEXT = "Неизвестная команда!";
    private BotKeyboardState keyboardState;

    // menu items
    private final List<String> shelterNames = List.of("Солнышко", "Дружок", "На Невском");
    private final List<String> sheltersInfo = List.of("Адрес: Москва, ул. Академика Королева, 13", "Адрес: Ижевск, ул. Боевой славы, 5", "Адрес: Санкт-Петербург, Невский проспект, 18");
    private final List<String> sheltersMaps = List.of("Схема проезда к приюту Солнышко", "Схема проезда к приюту Дружок", "Схема проезда к приюту На Невском");


    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${spring.datasource.username}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            logger.error("Error in bot init() method: " + e.toString());
        }
        this.keyboardState = BotKeyboardState.SHELTER_SELECT;
    }

    public BotKeyboardState getKeyboardState() {
        return keyboardState;
    }

    public void setKeyboardState(BotKeyboardState keyboardState) {
        this.keyboardState = keyboardState;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            Long chatId = update.getMessage().getChatId();
            logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote " + message.getText());

            switch (message.getText()) {
                case "/start" -> sendText(chatId, WELCOME);
                case "/help" -> help(chatId);
                default -> sendText(chatId, ALTERNATIVE_TEXT);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Integer messageID = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (callbackData) {
                case "HELP_BUTTON" -> {
                    String text = "Здесь должна быть помощь";
                    EditMessageText messageText = new EditMessageText();
                    messageText.setChatId(chatId);
                    messageText.setText(text);
                    messageText.setMessageId(messageID);
                    executeAndEditMessage(messageText);
                }
                case "CLEAR_BUTTON" -> {
                    String text = "Точно всё понятно?";
                    EditMessageText messageText = new EditMessageText();
                    messageText.setChatId(chatId);
                    messageText.setText(text);
                    messageText.setMessageId(messageID);
                    executeAndEditMessage(messageText);
                }
                default -> sendText(chatId, "Произошла ошибка");
            }
        }
    }

    private void help(Long chatId) {
        SendMessage helpMessage = new SendMessage();
        helpMessage.setChatId(chatId);
        helpMessage.setText("Инструкция по применению.");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton firstButton = new InlineKeyboardButton();
        firstButton.setText("Help");
        firstButton.setCallbackData("HELP_BUTTON"); // это id кнопки
        InlineKeyboardButton secondButton = new InlineKeyboardButton();
        secondButton.setText("Понятно.");
        secondButton.setCallbackData("CLEAR_BUTTON"); // это id кнопки
        row.add(firstButton);
        row.add(secondButton);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        helpMessage.setReplyMarkup(inlineKeyboardMarkup);


        executeAndSendMessage(helpMessage); // отображение сообщения в чате, отправка в чат
    }

    public void sendText(Long chatId, String message) {
        logger.info("Bot sendText() to chatId " + chatId + " with message: " + message);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message).build();

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        List<String> buttonNames = new ArrayList<>();
//        switch (keyboardState) {
//            case SHELTER_SELECT -> {
//                KeyboardRow row = new KeyboardRow();
//                row.addAll(buttonNames);
//                keyboardRows.add(row);
//
//            }
//
//                    sendText(chatId, WELCOME);
//
//            case "/help" -> sendText(chatId, WELCOME);
//            default -> sendText(chatId, ALTERNATIVE_TEXT);
//        }
//


        KeyboardRow row = new KeyboardRow();
        row.addAll(List.of("Старт"));
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.addAll(List.of("Button 2", "Button 3"));
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.addAll(List.of("Button 4", "Button 5"));
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(keyboardMarkup);

        executeAndSendMessage(sendMessage);
    }

    public void executeAndSendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error executing message: " + e.toString());
        }
    }

    public void executeAndEditMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error executing editMessage: " + e.toString());
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    // getBotToken() is deprecated

}
