package pro.sky.javacourse.AnimalShelterBot.telegram_bot;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.sky.javacourse.AnimalShelterBot.telegram_bot.menu.BotState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private BotState keyboardState;
    private final Map<Long, BotState> botStates = new HashMap<Long, BotState>();
    private final Map<Long, Long> shelterIdByChatId = new HashMap<Long, Long>();


    /**
     * Constructor for configure bot with botToken and botUserName.
     *
     * @param botToken    telegram bot token from BotFather
     * @param botUserName name of bot and name of postgreSQL dataBase
     */
    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${spring.datasource.username}") String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }


    // overriding getBotToken() method is deprecated

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
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    /**
     * onUpdateReceived method handles all updates, new updates should be parsed into this method.
     */
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        if (!botStates.containsKey(chatId)) {
            botStates.put(chatId, BotState.COMMON);
        }
        // all bot states
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote " + message.getText());

            switch (message.getText()) {
                // "/start" command should close all unfinished tasks, remove ReplyKeyboard and return user to the beginning
                case "/start" -> {
                    if (botStates.get(chatId) == BotState.VOLUNTEER_CHAT) {
                        sendText(chatId, "Чат с волонтером закрыт. Открыть чат можно выбрав приют.");
                    }
                    botStates.put(chatId, BotState.COMMON);
                    ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
                    SendMessage msg = SendMessage.builder().chatId(chatId).text(WELCOME).replyMarkup(keyboardRemove).build();
                    try {
                        execute(msg);
                    } catch (TelegramApiException e) {
                        logger.error("Error removing keyboard by start command: " + e.toString());
                    }
                    logger.info(user.getFirstName() + ", chatId " + chatId + ", has removed keyboard using start command");
                    menuShelterSelect(chatId);
                    return;
                }
                case "/help" -> {
                    help(chatId);
                    return;
                }
            }
        }
        // particular bot states
        switch (botStates.get(chatId)) {
            case COMMON -> onUpdateReceivedCommon(update);
            case VOLUNTEER_CHAT -> onUpdateReceivedVolunteerChat(update);
        }
    }


    //    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            Message message = update.getMessage();
//            User user = message.getFrom();
//            Long chatId = update.getMessage().getChatId();
//            logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote " + message.getText());
//            switch (message.getText()) {
//                // "/start" command should close all unfinished tasks anr return user to the beginning
//                case "/start" -> {
//                    if (keyboardStates.get(chatId) != BotState.COMMON) {
//                        keyboardStates.put(chatId, BotState.COMMON);
//                    }
//                    sendText(chatId, WELCOME);
//                    menuShelterSelect(chatId);
//                }
//                case "/help" -> help(chatId);
//                case "Закрыть чат" -> {
//                    ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
//                    SendMessage msg = new SendMessage();
//                    msg.setText("Чат с волонтером закрыт. Для открытия чата с приютом воспользуйтесь меню приюта.");
//                    msg.setChatId(chatId);
//                    msg.setReplyMarkup(keyboardRemove);
//                    executeAndSendMessage(msg);
//                    keyboardStates.put(chatId, BotState.COMMON);
//                    //////////////// надо как то вернуться в меню приюта, т.е. нужен callback
//                }
//                default -> {
//                    if (keyboardStates.get(chatId) == BotState.COMMON) sendText(chatId, ALTERNATIVE_TEXT);
//                    if (keyboardStates.get(chatId) == BotState.VOLUNTEER_CHAT) {
//                        sendText(volunteerChatId, message.getText());
//                    }
//                }
//            }
//        // common state
//        if (keyboardStates.get(chatId) == BotState.COMMON) {
//            if (keyboardStates.get(chatId) == BotState.COMMON) {
//                switch (message.getText()) {
//
//                    case "Убрать клавиатуру" -> {
//// This case is unfinished
//                        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
//                        SendMessage msg = new SendMessage();
//                        msg.setText("Клавиатура удалена.");
//                        msg.setChatId(chatId);
//                        msg.setReplyMarkup(keyboardRemove);
//                        executeAndSendMessage(msg);
//                    }
//                    default -> sendText(chatId, ALTERNATIVE_TEXT);
//                }
//            } else if (update.hasCallbackQuery()) {
//                String callbackData = update.getCallbackQuery().getData();
//                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
//                Long chatId = update.getCallbackQuery().getMessage().getChatId();
//
//                switch (callbackData) {
//                    case "HELP_BUTTON" -> {
//                        String text = "Здесь должна быть помощь";
//                        EditMessageText messageText = new EditMessageText();
//                        messageText.setChatId(chatId);
//                        messageText.setText(text);
//                        messageText.setMessageId(messageId);
//                        executeAndEditMessage(messageText);
//                    }
//                    case "CLEAR_BUTTON" -> {
//                        String text = "Точно всё понятно?";
//                        EditMessageText messageText = new EditMessageText();
//                        messageText.setChatId(chatId);
//                        messageText.setText(text);
//                        messageText.setMessageId(messageId);
//                        executeAndEditMessage(messageText);
//                    }
//                    case "SHELTER_SELECT" -> {
//                        sendText(chatId, "Выберите приют:");
//                        menuShelterSelect(chatId);
//                    }
//                    case "VOLUNTEER_CHAT" -> {
//                        this.keyboardState = BotState.VOLUNTEER_CHAT;
//                        menuVolunteerChat(chatId);
//                    }
//                    default -> {
//                        if (callbackData.startsWith("SHELTER")) {
//                            Long shelterId = getIdFromCallbackData(callbackData);
//                            menuShelter(chatId, shelterId);
//                        } else if (callbackData.startsWith("LOCATION_MAP")) {
//                            menuLocation(update);
//                        } else if (callbackData.startsWith("SEPARATE_SHELTER")) {
//                            menuShelterSelect(update);
//                        } else if (callbackData.startsWith("HOW_TO")) {
//                            Long shelterId = getIdFromCallbackData(callbackData);
//                            menuHowTo(chatId, shelterId);
//                        } else if (callbackData.startsWith("ANIMAL_TYPES")) {
//                            Long shelterId = getIdFromCallbackData(callbackData);
//                            menuPetType(chatId, shelterId);
//                        } else if (callbackData.startsWith("СОБАК")) {
//                            Long shelterId = getIdFromCallbackData(callbackData);
//                            menuPetSelect(chatId, shelterId, "Собака");
//                        } else if (callbackData.startsWith("КОШК")) {
//                            Long shelterId = getIdFromCallbackData(callbackData);
//                            menuPetSelect(chatId, shelterId, "Кот");
//                        } else if (callbackData.startsWith("ОСТАЛЬНЫЕ")) {
//                            Long shelterId = getIdFromCallbackData(callbackData);
//                            menuPetSelect(chatId, shelterId, "Остальные");
//                        } else {
//                            sendText(chatId, ALTERNATIVE_TEXT);
//                        }
//                    }
//                }
//            }
//        }
//
//
//    }
//
//}
    private void onUpdateReceivedCommon(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                User user = message.getFrom();
                Long chatId = update.getMessage().getChatId();
                logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote from common state " + message.getText());
                switch (message.getText()) {
                    case "Выйти из чата" -> {
                        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
                        SendMessage msg = new SendMessage();
                        msg.setText("Клавиатура удалена.");
                        msg.setChatId(chatId);
                        msg.setReplyMarkup(keyboardRemove);
                        executeAndSendMessage(msg);
                    }
                    default -> sendText(chatId, ALTERNATIVE_TEXT);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            User user = update.getCallbackQuery().getFrom();

            switch (callbackData) {
                case "HELP_BUTTON" -> {
                    String text = "Здесь должна быть помощь";
                    EditMessageText messageText = new EditMessageText();
                    messageText.setChatId(chatId);
                    messageText.setText(text);
                    messageText.setMessageId(messageId);
                    executeAndEditMessage(messageText);
                }
                case "DELETE" -> {
                    DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatId).messageId(messageId).build();
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        logger.error("Error deleting message: " + e.toString());
                    }
                    logger.info(user.getFirstName() + ", chatId " + chatId + " deleted help message");
                }
                case "SHELTER_SELECT" -> {
                    sendText(chatId, "Выберите приют:");
                    menuShelterSelect(chatId);
                }
                default -> {
                    if (callbackData.startsWith("SHELTER")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuShelter(chatId, shelterId);
                    } else if (callbackData.startsWith("LOCATION_MAP")) {
                        menuLocation(update);
                    } else if (callbackData.startsWith("SEPARATE_SHELTER")) {
                        menuShelterSelect(update);
                    } else if (callbackData.startsWith("HOW_TO")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuHowTo(chatId, shelterId);
                    } else if (callbackData.startsWith("ANIMAL_TYPES")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetType(chatId, shelterId);
                    } else if (callbackData.startsWith("СОБАК")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetSelect(chatId, shelterId, "Собака");
                    } else if (callbackData.startsWith("КОШК")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetSelect(chatId, shelterId, "Кот");
                    } else if (callbackData.startsWith("ОСТАЛЬНЫЕ")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetSelect(chatId, shelterId, "Остальные");
                    } else if (callbackData.startsWith("VOLUNTEER_CHAT")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuVolunteerChat(chatId, shelterId);
                    } else {
                        sendText(chatId, ALTERNATIVE_TEXT);
                    }
                }
            }
        }
    }

    private void onUpdateReceivedVolunteerChat(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().equals("Выйти из чата")) {

            Long volunteerChatId = 1722853186L; // must be retrieved fom DB using shelterId
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
            SendMessage message = new SendMessage();
            Long chatId = update.getMessage().getChatId();
            User user = update.getMessage().getFrom();
            message.setText("Отправка сообщений волонтеру отключена");
            message.setChatId(chatId);
            message.setReplyMarkup(keyboardRemove);
            executeAndSendMessage(message);
            logger.info(user + " chatId " + chatId + " closed chat with volunteerId " + volunteerChatId);
            botStates.put(chatId, BotState.COMMON);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            Long volunteerChatId = 1722853186L; // must be retrieved fom DB using shelterId
            User user = update.getMessage().getFrom();
            Long chatId = update.getMessage().getChatId();
            Integer messageId = update.getMessage().getMessageId();
            ForwardMessage forwardMessage = new ForwardMessage(volunteerChatId.toString(), chatId.toString(), messageId);


            // ЭТО КОД ДЛЯ ОТПРАВКИ ОТВЕТА НА СООБЩЕНИЕ, ЕГО БУДЕТ ИСПОЛЬВОВАТЬ ВОЛОНТЕР ПРИ ОБЩЕНИИ С КЛИЕНТОМ
            if (update.getMessage().getReplyToMessage() != null) {

                System.out.println(update.getMessage().getFrom());
                System.out.println();
                System.out.println(update.getMessage().getReplyToMessage().getForwardFrom().getId());

                SendMessage m = new SendMessage();
                m.setChatId(6725110697L);
                m.setText("Working");
                executeAndSendMessage(m);
            }


            // КОНЕЦ КОДА ДЛЯ ОТВЕТА НА СООБЩЕНИЕ


            try {
                execute(forwardMessage);
            } catch (TelegramApiException e) {
                logger.error("Error executing message: " + e.toString());
            }
            logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote to volunteer chatId " + volunteerChatId + " " + update.getMessage().getText());
        }
    }

    private void onUpdateReceivedReport(Update update) {

    }

    private void onUpdateReceivedCollectData(Update update) {

    }

    private void onUpdateVolunteer(Update update) {

    }


    private void help(Long chatId) {
        String text = "Взаимодействие с ботом производится путем нажатия кнопок под соответствующим сообщением. " +
                "\nИногда кнопки могут быть заблокированы, если Вы находитесь в режиме прямого диалога с сотрудником приюта или " +
                "создаете отчет об усыновлении питомца. Воспользуйтесь командой /start, если хотите выйти в начало из меню диалога или отчета. " +
                "Для простого выхода из режима диалога воспользуйтесь кнопкой внизу. Для отправки отчета также предусмотрена кнопка внизу окна, " +
                "при выходе из отчета командой /start отчет не отправится. В случае разрыва связи или перегруженности бота бот также может " +
                "не реагировать на кнопки. Надеемся на понимание.";

        String[][][] buttons = {{{"Ok", "DELETE"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId).text(text).replyMarkup(inlineKeyboardMarkup).build();
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

    private Long getIdFromCallbackData(String callbackData) {
        return Long.parseLong(callbackData.replaceAll("[^0-9]", ""), 10);
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
                {{"Позвать волонтера", "VOLUNTEER_CHAT" + shelter.get(2)}},
                {{"Вернуться к выбору приюта", "SHELTER_SELECT"}}
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

    private void menuPetType(Long chatId, Long shelterId) {
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
            // if there is only one type of animal bot will execute menuPetSelect method with this type
            String petType = "";
            switch (petTypes.get(0)) {
                case "Собаки" -> {
                    petType = "Собака";
                }
                case "Кошки" -> {
                    petType = "Кот";
                }
                default -> {
                    petType = "Остальные";
                }
            }
            menuPetSelect(chatId, shelterId, petType);
        } else {

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

    private void menuPetSelect(Long chatId, Long shelterId, String petType) {
        List<List<String>> allPets = new ArrayList<>();
        // Long petId | String name | String petType | String info | Integer age | Long shelterId
        allPets.add(List.of("0", "Шарик", "Собака", "Добрый, отзывчивый пес.", "10", "0"));
        allPets.add(List.of("1", "Барбос", "Собака", "Надежный охранник.", "3", "0"));
        allPets.add(List.of("2", "Василий", "Кот", "Ответственный и важный кот.", "4", "0"));
        allPets.add(List.of("3", "Машка", "Кот", "Общительная кошка.", "3", "0"));
        allPets.add(List.of("4", "Кеша", "Остальные", "Волнистый попугайчик.", "1", "0"));

        allPets.add(List.of("5", "Тортилла", "Остальные", "Мудрая черепаха.", "20", "1"));
        allPets.add(List.of("6", "Пеппа", "Остальные", "Морская свинка.", "1", "1"));
        allPets.add(List.of("7", "Матроскин", "Кот", "Кот. Очень деловой.", "3", "1"));
        allPets.add(List.of("8", "Мурка", "Кот", "Замурчательная кошка.", "4", "1"));
        allPets.add(List.of("9", "Марсик", "Кот", "Большой, пушистый кот.", "5", "1"));

        allPets.add(List.of("10", "Тайга", "Собака", "Тренированная охотничья собака. Девочка.", "6", "2"));
        allPets.add(List.of("11", "Ипполит", "Собака", "Комнатный пес, очень общительный.", "4", "2"));
        allPets.add(List.of("12", "Пуля", "Собака", "Смесь пуделя и болонки, очень активная.", "5", "2"));
        allPets.add(List.of("13", "Элвис", "Остальные", "Обаятельный хомяк", "1", "2"));

        List<List<String>> pets = allPets.stream()
                .filter(pet -> pet.get(5).equals(shelterId.toString()) && pet.get(2).equals(petType))
                .toList();
        long petTypes = allPets.stream()
                .filter(pet -> pet.get(5).equals(shelterId.toString()))
                .map(p -> p.get(2))
                .distinct()
                .count();
        // питомца могут забрать и список может внезапно оказаться пустым
        SendMessage message = new SendMessage();
        if (pets.isEmpty()) {
            message = createMessage(chatId, "Нет питомцев для усыновления");
            String[][][] buttons = {{{"Назад", "SHELTER" + shelterId}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(message);
        } else {
            for (List<String> pet : pets) {
                message = createMessage(chatId, pet.get(1) + "\n\n" + pet.get(3) +
                        " Возраст " + pet.get(5) + " полных лет");
                executeAndSendMessage(message);
            }
            message = createMessage(chatId, "Вернуться в предыдущее меню");
            // for one type of animals pet type selection menu won't be created
            if (petTypes == 1) {
                String[][][] buttons = {{{"Назад", "SHELTER" + shelterId}}};
                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
                message.setReplyMarkup(inlineKeyboardMarkup);
                executeAndSendMessage(message);
            } else {
                String[][][] buttons = {{{"Назад", "ANIMAL_TYPES" + shelterId}}};
                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
                message.setReplyMarkup(inlineKeyboardMarkup);
                executeAndSendMessage(message);
            }
        }
    }

    private void menuVolunteerChat(Long chatId, Long shelterId) {
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

        botStates.put(chatId, BotState.VOLUNTEER_CHAT);
        SendMessage message = createMessage(chatId, "Теперь Ваши сообщения будут доставлены в приют дежурному волонтеру из приюта " + shelter.get(0) + "\nДля выхода из чата с волонтером нажмите кнопку \"Выйти из чата\" или отправьте сообщение с текстом \"Выйти из чата\"");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.addAll(List.of("Выйти из чата"));
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true); // resizes keyboard to smaller size
        keyboardMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(keyboardMarkup);
        executeAndSendMessage(message); // отображение сообщения в чате, отправка в чат
    }
}
