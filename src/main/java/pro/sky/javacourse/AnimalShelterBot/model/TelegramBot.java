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
import java.util.regex.Pattern;

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


    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${spring.datasource.username}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    public BotKeyboardState getKeyboardState() {
        return keyboardState;
    }

    public void setKeyboardState(BotKeyboardState keyboardState) {
        this.keyboardState = keyboardState;
    }

    // getBotToken() is deprecated
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


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            Long chatId = update.getMessage().getChatId();
            logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote " + message.getText());

            switch (message.getText()) {
                case "/start" -> {
                    sendText(chatId, WELCOME);
                    menuShelterSelect(chatId);
                }
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
                case "SHELTER_SELECT" -> {
                    sendText(chatId, "Выберите приют:");
                    menuShelterSelect(chatId);
                }
                default -> {
                    if (callbackData.startsWith("SHELTER")) {
                        Long shelterId = Long.parseLong(callbackData.replaceAll("[^0-9]", ""), 10);
                        menuShelter(chatId, shelterId);
                    } else {
                        sendText(chatId, "Произошла ошибка");
                    }
                }
            }
        }
    }

    private void help(Long chatId) {
        SendMessage helpMessage = createMessage(chatId, "Инструкция по применению.");
        String[][][] buttons = {{{"Help", "HELP_BUTTON"}, {"Ok", "CLEAR_BUTTON"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
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

    public SendMessage createMessage(Long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        return message;
    }

    private InlineKeyboardButton createInlineKeyBoardButton(String name, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(callbackData);
        return button;
    }

    // Creating inlineKeyboardMarkup from Array containing of Arrays of rows,
    // where each row contains of Arrays of buttons,
    // each button is an array of {String buttonName, and String callbackData}.
    // Array must be instantiated before passed to this method.
    private InlineKeyboardMarkup createInlineKeyboardMarkup(String[][]... rows) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        for (String[][] row : rows) {
            List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
            for (String[] button : row) {
                InlineKeyboardButton b = createInlineKeyBoardButton(button[0], button[1]);
                buttonsRow.add(b);
            }
            keyboardRows.add(buttonsRow);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private void executeAndSendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error executing message: " + e.toString());
        }
    }

    private void executeAndEditMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error executing editMessage: " + e.toString());
        }
    }


    // Retrieving particular menu / bot message

    private void menuShelterSelect(Long chatId) {
        List<List<String>> shelters = new ArrayList<>();
        shelters.add(List.of("Солнышко", "Адрес: Москва, ул. Академика Королева, 13", "0"));
        shelters.add(List.of("Дружок", "Адрес: Ижевск, ул. Боевой славы, 5", "1"));
        shelters.add(List.of("На Невском", "Адрес: Санкт-Петербург, Невский проспект, 18", "2"));

        for (List<String> shelter : shelters) {
            SendMessage shelterMessage = createMessage(chatId, shelter.get(0) + "\n\n" + shelter.get(1));
            String[][][] buttons = {{{"Схема проезда", "SHELTER_LOCATION_MAP" + shelter.get(2)}, {"Выбрать", "SHELTER" + shelter.get(2)}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            shelterMessage.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(shelterMessage);
        }
    }

    private void menuShelter(Long chatId, Long shelterId) {
        List<List<String>> shelters = new ArrayList<>();
        shelters.add(List.of("Солнышко", "Адрес: Москва, ул. Академика Королева, 13", "0", "Режим работы:\nПонедельник - пятница: с 7-00 до 20-00 без обеда и выходных."));
        shelters.add(List.of("Дружок", "Адрес: Ижевск, ул. Боевой славы, 5", "1", "Режим работы:\nПонедельник - пятница: с 8-00 до 21-00 без обеда и выходных."));
        shelters.add(List.of("На Невском", "Адрес: Санкт-Петербург, Невский проспект, 18", "2", "Режим работы:\nПонедельник - пятница: с 6-00 до 20-00 без обеда и выходных."));
        List<String> shelter = new ArrayList<>();
        try {
            shelter = shelters.stream()
                    .filter(s -> Long.valueOf(s.get(2)).equals(shelterId))
                    .findFirst()
                    .orElseThrow(NullPointerException::new);
        } catch (NullPointerException e) {
            logger.error("Shelter id: " + shelterId.toString() + " not found.");
            e.printStackTrace();
        }

        SendMessage shelterMessage = createMessage(chatId,
                shelter.get(0) + "\n" + shelter.get(3));

        String[][][] buttons = {{{"Как взять животное из приюта?", "HOW_TO"}},
                {{"Выбрать питомца", "ANIMAL_TYPES" + shelter.get(2)}},
                {{"Оставьте свой номер и мы Вам перезвоним", "COLLECT_DATA"}},
                {{"Вернуться в предыдущее меню", "SHELTER_SELECT"}}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        shelterMessage.setReplyMarkup(inlineKeyboardMarkup);
        executeAndSendMessage(shelterMessage);
    }


}
