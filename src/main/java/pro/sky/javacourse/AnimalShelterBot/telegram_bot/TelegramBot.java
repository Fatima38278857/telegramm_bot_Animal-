package pro.sky.javacourse.AnimalShelterBot.telegram_bot;

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
import pro.sky.javacourse.AnimalShelterBot.telegram_bot.menu.BotKeyboardState;

import java.util.ArrayList;
import java.util.List;

// Class TelegramBot extends abstract class TelegramLongPollingBot from Telegram API
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final String botUserName;
    private String botToken;
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String WELCOME = "Привет!\n\nЭто телеграм бот для поиска питомца. " +
            "Наша команда волонтеров будет счастлива, если удастся найти новый дом для любого из наших подопечных! " +
            "\nПожалуйста, выберите приют.";
    private final String ALTERNATIVE_TEXT = "Неизвестная команда!";
    private BotKeyboardState keyboardState;


    /**
     * Constructor for configure bot with botToken and botUserName.
     *
     * @param botToken
     * @param botUserName
     */
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

    /**
     * Method for register bot. No configuration class were used.
     *
     * @throws TelegramApiException
     */
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

    /**
     * onUpdateReceived method handles all updates, new updates should be parsed into this method.
     */
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
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "HELP_BUTTON" -> {
                    String text = "Здесь должна быть помощь";
                    EditMessageText messageText = new EditMessageText();
                    messageText.setChatId(chatId);
                    messageText.setText(text);
                    messageText.setMessageId(messageId);
                    executeAndEditMessage(messageText);
                }
                case "CLEAR_BUTTON" -> {
                    String text = "Точно всё понятно?";
                    EditMessageText messageText = new EditMessageText();
                    messageText.setChatId(chatId);
                    messageText.setText(text);
                    messageText.setMessageId(messageId);
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
                    } else if (callbackData.startsWith("LOCATION_MAP")) {
                        menuLocation(update);
                    } else if (callbackData.startsWith("SEPARATE_SHELTER")) {
                        menuShelterSelect(update);
                    } else if (callbackData.startsWith("HOW_TO")) {
                        Long shelterId = Long.parseLong(callbackData.replaceAll("[^0-9]", ""), 10);
                        menuHowTo(chatId, shelterId);
                    } else if (callbackData.startsWith("ANIMAL_TYPES")) {
                        Long shelterId = Long.parseLong(callbackData.replaceAll("[^0-9]", ""), 10);
                        menuAnimalType (chatId, shelterId);
                    } else {
                        sendText(chatId, "Произошла ошибка");
                    }
                }
            }
        }
    }

    private void help(Long chatId) {
        SendMessage message = createMessage(chatId, "Инструкция по применению.");
        String[][][] buttons = {{{"Help", "HELP_BUTTON"}, {"Ok", "CLEAR_BUTTON"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        message.setReplyMarkup(inlineKeyboardMarkup);
        executeAndSendMessage(message); // отображение сообщения в чате, отправка в чат
    }

    /**
     * Example of method that creates keyboard buttons menu on the bottom of telegram chat window.
     * Will be refactored or deleted later.
     *
     * @param chatId  chat identificator of type Long
     * @param message String message that should be entered by telegram user to start this method.
     */
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

    /**
     * Creates inlineKeyboardMarkup from Array containing of Arrays of rows,
     * where each row contains of Arrays of buttons,
     * each button is an array of {String buttonName, String callbackData}.<br>
     * Array <u>must</u> be instantiated before passed to this method.<br>
     * InlineKeyboardMarkup can be invoked on Message obj to add inline keyboard buttons.
     *
     * @param rows {{{row1Btn1Name, row1Btn1Callback},{row1Btn2Name, row1Btn2Callback}},{{row2Btn1Name, row3Btn1Callback}}}
     * @return InlineKeyboardMarkup obj
     */
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

    /**
     * Method for execute telegram SendMessage message
     * and handle {@link TelegramApiException} in a single line of code.
     *
     * @param message {@link SendMessage} message
     */
    private void executeAndSendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error executing message: " + e.toString());
        }
    }

    /**
     * Method for edit text in message and execute telegram message
     * while handling {@link TelegramApiException}.
     *
     * @param message {@link EditMessageText} message
     */
    private void executeAndEditMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error executing editMessage: " + e.toString());
        }
    }


    // Retrieving particular menu / bot message

    /**
     * Specific method for construct and send Shelter select menu<br>
     * This method uses custom methods:<br>{@link TelegramBot#createInlineKeyboardMarkup(String[][]...)}
     * to create buttons
     * and {@link TelegramBot#executeAndSendMessage(SendMessage)} to send message<br>
     *
     * @param chatId chat identity of type Long
     */
    private void menuShelterSelect(Long chatId) {
        List<List<String>> shelters = new ArrayList<>();
        shelters.add(List.of("Солнышко", "Адрес: Москва, ул. Академика Королева, 13", "0"));
        shelters.add(List.of("Дружок", "Адрес: Ижевск, ул. Боевой славы, 5", "1"));
        shelters.add(List.of("На Невском", "Адрес: Санкт-Петербург, Невский проспект, 18", "2"));

        for (List<String> shelter : shelters) {
            SendMessage shelterMessage = createMessage(chatId, shelter.get(0) + "\n\n" + shelter.get(1));
            String[][][] buttons = {{{"Схема проезда", "LOCATION_MAP" + shelter.get(2)}, {"Выбрать", "SHELTER" + shelter.get(2)}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            shelterMessage.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(shelterMessage);
        }
    }

    /**
     * Specific method that returns message of separate shelter from roadmap menu to its original condition in shelter select menu
     * reverses the effect of {@link TelegramBot#menuLocation(Update)} method
     *
     * @param update of Update class transfers information like chatId, messageId, callbackData including shelterId
     */
    private void menuShelterSelect(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long shelterId = Long.parseLong(callbackData.replaceAll("[^0-9]", ""), 10);

        List<List<String>> shelters = new ArrayList<>();
        shelters.add(List.of("Солнышко", "Адрес: Москва, ул. Академика Королева, 13", "0"));
        shelters.add(List.of("Дружок", "Адрес: Ижевск, ул. Боевой славы, 5", "1"));
        shelters.add(List.of("На Невском", "Адрес: Санкт-Петербург, Невский проспект, 18", "2"));

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

        String text = shelter.get(0) + "\n\n" + shelter.get(1);

        EditMessageText messageText = new EditMessageText();
        messageText.setChatId(chatId);
        messageText.setText(text);
        messageText.setMessageId(messageId);

        String[][][] buttons = {{{"Схема проезда", "LOCATION_MAP" + shelter.get(2)}, {"Выбрать", "SHELTER" + shelter.get(2)}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        messageText.setReplyMarkup(inlineKeyboardMarkup);
        executeAndEditMessage(messageText);
    }

    /**
     * Specific method for construct and send selected Shelter menu.<br>
     * This method uses custom methods:<br>{@link TelegramBot#createInlineKeyboardMarkup(String[][]...)}
     * to create buttons
     * and {@link TelegramBot#executeAndSendMessage(SendMessage)} to send message<br>
     *
     * @param chatId chat identity of type Long, shelterId - shelter identity of type Long
     */
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

        String[][][] buttons = {{{"Как взять животное из приюта?", "HOW_TO" + shelter.get(2)}},
                {{"Выбрать питомца", "ANIMAL_TYPES" + shelter.get(2)}},
                {{"Оставьте свой номер и мы Вам перезвоним", "COLLECT_DATA"}},
                {{"Вернуться в предыдущее меню", "SHELTER_SELECT"}}
        };
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        shelterMessage.setReplyMarkup(inlineKeyboardMarkup);
        executeAndSendMessage(shelterMessage);
    }

    /**
     * Specific method that edits message of particular shelter before edit to show roadmap to that shelter
     *
     * @param update of Update class transfers information like chatId, messageId, callbackData including shelterId
     */
    private void menuLocation(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long shelterId = Long.parseLong(callbackData.replaceAll("[^0-9]", ""), 10);

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

        String text = shelter.get(0) + "\n" + shelter.get(3) + "\n\nЗдесь должна быть схема проезда к приюту " + shelter.get(0);
        EditMessageText messageText = new EditMessageText();
        messageText.setChatId(chatId);
        messageText.setText(text);
        messageText.setMessageId(messageId);

        String[][][] buttons = {{{"Назад", "SEPARATE_SHELTER" + shelter.get(2)}, {"Выбрать", "SHELTER" + shelter.get(2)}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        messageText.setReplyMarkup(inlineKeyboardMarkup);
        executeAndEditMessage(messageText);
    }

    private void menuHowTo(Long chatId, Long shelterId) {
        List<List<String>> shelters = new ArrayList<>();
        // String howTo should be text from database, that can be unique for each shelter
        String howTo = "Чтобы взять животное из приюта необходимо будет посетить приют и выбрать питомца. Адрес и режим работы приюта можно узнать, просмотрев информацию из предыдущих меню.\n" +
                "Краткую информацию о содержащихся в приюте питомцах можно узнать, проследовав в предыдущем меню по кнопке «Выбрать питомца».\n" +
                "Также в нашем боте можно получить рекомендации специалистов по общим вопросам содержания отдельных видов и пород животных. \n" +
                "Для оформления документов от нам потребуется Ваш паспорт гражданина РФ.\n" +
                "В приюте Вы познакомитесь с питомцем и сможете вместе провести время, например, сходить на прогулку.\n" +
                "В отдельных случаях, от Вас потребуется подтвердить наличие условий содержания животного, либо наличие специальных предметов для транспортировки животного. С данными условиями Вы можете также кратко ознакомиться, перейдя в предыдущем меню по кнопке «Выбрать питомца».\n" +
                "После заключения договора Вы сможете забрать питомца в его новый дом. Вам будет назначен испытательный срок, в течение которого Вы обязаны ежедневно присылать отчет о состоянии питомца с фотографиями. Отчет также можно передать через данного бота в Телеграмм. Замечания или пожелания от волонтеров, просматривающих отчеты, также поступят в Ваш чат с ботом.\n" +
                "И главное, Вам могут отказать в передаче питомца или обязать вернуть питомца в приют без объяснения причин. Таково наше условие, с которым необходимо согласиться.\n" +
                "После прохождения испытательного срока от Вас потребуется посетить приют вместе с питомцем для завершения оформления документов.\n" +
                "По всем дополнительным вопросам Вы также можете связаться с нашими волонтерами используя чат бота либо можете оставить свои контактные данные, и мы Вам перезвоним.\n";

        shelters.add(List.of("Солнышко", "Адрес: Москва, ул. Академика Королева, 13", "0", howTo));
        shelters.add(List.of("Дружок", "Адрес: Ижевск, ул. Боевой славы, 5", "1", howTo));
        shelters.add(List.of("На Невском", "Адрес: Санкт-Петербург, Невский проспект, 18", "2", howTo));

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

        SendMessage message = createMessage(chatId, shelter.get(0) + "\n\n" + shelter.get(3));
        String[][][] buttons = {{{"OK", "SHELTER" + shelter.get(2)}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        message.setReplyMarkup(inlineKeyboardMarkup);
        executeAndSendMessage(message);
    }

    private void menuAnimalType(Long chatId, Long shelterId) {
// нужно получить отдельные (distinct) типы животных из базы данных в зависимости от shelterId,
//        у меня пока что shelterId от 0 до 2 включительно, поэтому я приравниваю его к индексу,
//        но в будущем это не сработает
        List<List<String>> allPetTypes = new ArrayList<>();
        allPetTypes.add(List.of("Собаки", "Кошки", "Остальные"));
        allPetTypes.add(List.of("Кошки", "Остальные"));
        allPetTypes.add(List.of("Собаки", "Остальные"));
        List<String> petTypes = allPetTypes.get(Integer.parseInt(shelterId.toString(), 10)); // получаем перечень
        // типов животных в приюте с shelterId
        if (petTypes.size() == 1) {
            // здесь должен быть метод по отображению непосредственно питомцев
            // для того чтобы не предлагать выбирать тип животного из одного варианта
            return;
        }
        SendMessage message = createMessage(chatId, "Выберите тип питомца");

        String[][][] buttons = new String[petTypes.size() + 1][1][2]; // массив из кнопок по 1 в ряд, количество рядов
        // равно размеру списка типов животных + 1 ряд для кнопки "Назад", callBackData возвращаемая кнопкой с типом
        // животного равна типу животного в верхнем регистре + Id приюта, чтобы сделать выборку.
        for (int i = 0; i < petTypes.size(); i++) {
            buttons[i][0][0] = petTypes.get(i);
            buttons[i][0][1] = petTypes.get(i).toUpperCase() + shelterId; // чтобы кнопки работали типы животных должны
            // быть в enum.
        }
        buttons[petTypes.size()][0][0] = "Назад";
        buttons[petTypes.size()][0][1] = "SHELTER" + shelterId;
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        message.setReplyMarkup(inlineKeyboardMarkup);
        executeAndSendMessage(message);
    }
}
