package pro.sky.javacourse.AnimalShelterBot.telegram_bot;

import jakarta.annotation.PostConstruct;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.service.BotService;
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
    private final BotService botService;
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String WELCOME = "Привет!\n\nЭто телеграм бот для поиска питомца. " +
            "Наша команда волонтеров будет счастлива, если удастся найти новый дом для любого из наших подопечных! " +
            "\nПожалуйста, выберите приют.";
    private final String ALTERNATIVE_TEXT = "Неизвестная команда!";
    private BotState keyboardState;
    private final Map<Long, BotState> botStates = new HashMap<Long, BotState>();
//    private final Map<Long, Long> shelterIdByChatId = new HashMap<Long, Long>();


    /**
     * Constructor for configure bot with botToken and botUserName.
     *
     * @param botToken    telegram bot token from BotFather
     * @param botUserName name of bot and name of postgreSQL dataBase
     */
    public TelegramBot(@Value("${telegram.bot.token}") String botToken, @Value("${spring.datasource.username}") String botUserName, BotService botService) {
        super(botToken);
        this.botUserName = botUserName;
        this.botService = botService;
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
                    if (botStates.get(chatId) == BotState.VOLUNTEER_CHAT) {
                        sendText(chatId, "Отправка контакта отменена.");
                    }
                    if (botStates.get(chatId) == BotState.REPORT) {
                        sendText(chatId, "Создание отчета отменено.");
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
                    menuReportStart(chatId);
                    return;
                }
                case "/help" -> {
                    menuHelp(chatId);
                    return;
                }
            }
        }
        // particular bot states
        switch (botStates.get(chatId)) {
            case COMMON -> onUpdateReceivedCommon(update);
            case COLLECT_DATA -> onUpdateReceivedCollectData(update);
            case VOLUNTEER_CHAT -> onUpdateReceivedVolunteerChat(update);
            case REPORT -> onUpdateReceivedReport(update);
        }
    }

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
                case "DELETE" -> {
                    DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatId).messageId(messageId).build();
                    try {
                        execute(deleteMessage);
                    } catch (TelegramApiException e) {
                        logger.error("Error deleting message: " + e.toString());
                    }
                    logger.info(user.getFirstName() + ", chatId " + chatId + " deleted message");
                }
                case "SHELTER_SELECT" -> {
                    sendText(chatId, "Выберите приют:");
                    menuShelterSelect(chatId);
                }
                case "REPORT_PET_SELECT" -> {
                    menuReportPetSelect(chatId);
                }
                default -> {
                    if (callbackData.startsWith("SHELTER")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuShelter(chatId, shelterId);
                    } else if (callbackData.startsWith("LOCATION_MAP")) {
                        menuLocation(update);
                    } else if (callbackData.startsWith("SEPARATE_SHELTER")) {
                        menuShelterCurrent(update);
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
                    } else if (callbackData.startsWith("REPORT")) {
                        Long petId = getIdFromCallbackData(callbackData);
                        menuReportPetCurrent(petId);
                    } else if (callbackData.startsWith("CONTACT")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuContactRequest(chatId, shelterId);
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

// как я могу получить shelter, если udate содержит только "выйти из чата"???????????
            Long volunteerChatId = 1722853186L; // must be retrieved fom DB using shelterId

            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
            User user = update.getMessage().getFrom();
            Long chatId = update.getMessage().getChatId();
            SendMessage message = SendMessage.builder().chatId(chatId)
                    .text("Отправка сообщений волонтеру отключена")
                    .replyMarkup(keyboardRemove)
                    .build();
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            Long chatId = update.getMessage().getChatId();
            logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote from common state " + message.getText());
            if (message.getText().equals("Отмена")) {
                ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
                SendMessage msg = SendMessage.builder().chatId(chatId)
                        .text("Отправка контакта отменена.")
                        .replyMarkup(keyboardRemove).build();
                executeAndSendMessage(msg);
                botStates.put(chatId, BotState.COMMON);
            } else {
                SendMessage msg = SendMessage.builder().chatId(chatId)
                        .text(ALTERNATIVE_TEXT).build();
                executeAndSendMessage(msg);
            }
        } else if (update.getMessage().getReplyToMessage().getText().startsWith("Чтобы оставить заявку на телефонный звонок, нажмите на кнопку с названием ")) {
            String shelterName = update.getMessage().getReplyToMessage().getText()
                    .replace("Чтобы оставить заявку на телефонный звонок, нажмите на кнопку с названием ", "")
                    .replace(".", "");
            Shelter shelter = botService.findShelterByName(shelterName);
            Long chatId = update.getMessage().getChatId();
            if (shelter == null) {
                logger.error("Shelter " + shelterName + " not found.");
                SendMessage msg = SendMessage.builder().chatId(chatId)
                        .text(ALTERNATIVE_TEXT).build();
                executeAndSendMessage(msg);
            } else {
                Contact contact = update.getMessage().getContact();
                botService.saveContact(contact, shelter);
                ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
                SendMessage m = SendMessage.builder().chatId(chatId)
                        .text("Ваши данные отправлены в приют " + shelterName + ".")
                        .replyMarkup(keyboardRemove).build();
                executeAndSendMessage(m);
                botStates.put(chatId, BotState.COMMON);
            }
        }
    }

    private void onUpdateReceivedVolunteer(Update update) {

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

    private boolean isVolunteer(Long chatId, Long shelterId) {
        return botService.isVolunteer(chatId, shelterId);
    }

    private Long getIdFromCallbackData(String callbackData) {
        return Long.parseLong(callbackData.replaceAll("[^0-9]", ""), 10);
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


    protected void menuHelp(Long chatId) {
        String text = "Взаимодействие с ботом производится путем нажатия кнопок под соответствующим сообщением. " +
                "\nИногда кнопки могут быть заблокированы, если Вы находитесь в режиме прямого диалога с сотрудником приюта или " +
                "создаете отчет об усыновлении питомца. Воспользуйтесь командой /start, если хотите выйти в начало из меню диалога или отчета. " +
                "Для простого выхода из режима диалога воспользуйтесь кнопкой внизу. Для отправки отчета также предусмотрена кнопка внизу окна, " +
                "при выходе из отчета командой /start отчет не отправится. В случае разрыва связи или перегруженности бота бот также может " +
                "не реагировать на кнопки. Надеемся на понимание.";

        String[][][] buttons = {{{"Ok", "DELETE"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndSendMessage(message); // отображение сообщения в чате, отправка в чат
    }

    /**
     * Specific method for construct and send Shelter select menu<br>
     * This method uses custom methods:<br>{@link TelegramBot#createInlineKeyboardMarkup(String[][]...)}
     * to create buttons
     * and {@link TelegramBot#executeAndSendMessage(SendMessage)} to send message<br>
     *
     * @param chatId chat identity of type Long
     */
    protected void menuShelterSelect(Long chatId) {
        List<Shelter> shelters = botService.findShelters();
        for (Shelter shelter : shelters) {
            SendMessage shelterMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(shelter.getName() + "\n\n" + shelter.getAddress()).build();
            String[][][] buttons = {{{"Схема проезда", "LOCATION_MAP" + shelter.getId()}, {"Выбрать", "SHELTER" + shelter.getId()}}};
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
    protected void menuShelterCurrent(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long shelterId = getIdFromCallbackData(callbackData);

        Shelter shelter = botService.findShelter(shelterId);

        String[][][] buttons = {{{"Схема проезда", "LOCATION_MAP" + shelterId}, {"Выбрать", "SHELTER" + shelterId}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        EditMessageText messageText = EditMessageText.builder().chatId(chatId)
                .messageId(messageId)
                .text(shelter.getName() + "\n\n" + shelter.getAddress())
                .replyMarkup(inlineKeyboardMarkup).build();
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
    protected void menuShelter(Long chatId, Long shelterId) {
        Shelter shelter = botService.findShelter(shelterId);
        SendMessage shelterMessage = SendMessage.builder()
                .chatId(chatId)
                .text(shelter.getName() + "\n" + shelter.getRegime()).build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        if (botService.isVolunteer(chatId, shelterId)) {
            keyboardRows.add(List.of(createInlineKeyBoardButton("Проверить отчеты опекунов", "REPORT_CHECK" + shelterId)));
        }
        if (true) { // если у опекуна есть питомцы на испытательном сроке
            keyboardRows.add(List.of(createInlineKeyBoardButton("Сдать отчет", "REPORT_PET_SELECT")));
        }

        keyboardRows.add(List.of(createInlineKeyBoardButton("Как взять животное из приюта?", "HOW_TO" + shelterId)));
        keyboardRows.add(List.of(createInlineKeyBoardButton("Выбрать питомца", "ANIMAL_TYPES" + shelterId)));
        keyboardRows.add(List.of(createInlineKeyBoardButton("Оставьте Ваш номер и мы Вам перезвоним", "CONTACT" + shelterId)));
        keyboardRows.add(List.of(createInlineKeyBoardButton("Позвать волонтера", "VOLUNTEER_CHAT" + shelterId)));
        keyboardRows.add(List.of(createInlineKeyBoardButton("Вернуться к выбору приюта", "SHELTER_SELECT")));
        inlineKeyboardMarkup.setKeyboard(keyboardRows);
        shelterMessage.setReplyMarkup(inlineKeyboardMarkup);
        executeAndSendMessage(shelterMessage);
    }

    /**
     * Specific method that edits message of particular shelter before edit to show roadmap to that shelter
     *
     * @param update of Update class transfers information like chatId, messageId, callbackData including shelterId
     */
    protected void menuLocation(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long shelterId = getIdFromCallbackData(callbackData);
        Shelter shelter = botService.findShelter(shelterId);

        String[][][] buttons = {{{"Назад", "SEPARATE_SHELTER" + shelterId}, {"Выбрать", "SHELTER" + shelterId}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(shelter.getName() + "\n" + shelter.getRegime() + "\n\nЗдесь должна быть схема проезда к приюту \""
                        + shelter.getName() + "\"")
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndEditMessage(editMessage);
    }

    protected void menuHowTo(Long chatId, Long shelterId) {
        Shelter shelter = botService.findShelter(shelterId);
        String[][][] buttons = {{{"OK", "SHELTER" + shelterId}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text(shelter.getName() + "\n\n" + shelter.getHowTo())
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndSendMessage(message);
    }

    protected void menuPetType(Long chatId, Long shelterId) {
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

            SendMessage message = SendMessage.builder().chatId(chatId).text("Выберите тип питомца").build();
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

    protected void menuPetSelect(Long chatId, Long shelterId, String petType) {
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
            message = SendMessage.builder().chatId(chatId).text("Нет питомцев для усыновления").build();
            String[][][] buttons = {{{"Назад", "SHELTER" + shelterId}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(message);
        } else {
            for (List<String> pet : pets) {
                message = SendMessage.builder().chatId(chatId)
                        .text(pet.get(1) + "\n\n" + pet.get(3) + " Возраст " + pet.get(5) + " полных лет").build();
                executeAndSendMessage(message);
            }
            message = SendMessage.builder().chatId(chatId).text("Вернуться в предыдущее меню").build();
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

    protected void menuVolunteerChat(Long chatId, Long shelterId) {
        Shelter shelter = botService.findShelter(shelterId);
        botStates.put(chatId, BotState.VOLUNTEER_CHAT);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text("Теперь Ваши сообщения будут доставлены дежурному волонтеру в приют \"" + shelter.getName()
                        + "\"\nДля выхода из чата с волонтером нажмите кнопку \"Выйти из чата\" или отправьте сообщение с текстом \"Выйти из чата\"")
                .build();
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.addAll(List.of("Выйти из чата"));
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true); // resizes keyboard to smaller size
        keyboardMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(keyboardMarkup);
        executeAndSendMessage(message);
    }

    protected void menuReportStart(Long chatId) {
        if (true) { // если у опекуна есть питомцы на испытательном сроке
            SendMessage message = SendMessage.builder().chatId(chatId).text("Сдать отчет").build();
            String[][][] buttons = {{{"Начать", "REPORT_PET_SELECT"}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(message);
        }
    }

    protected void menuReportPetSelect(Long chatId) {
        List<List<String>> petsOnAdoption = new ArrayList<>();
        // Long petId | String name | String petType | String info | Integer age | Long shelterId | Long caretakerChatId
        // вероятно придется получать caretakerChatId из базы по caretakerId
        petsOnAdoption.add(List.of("0", "Шарик", "Собака", "Добрый, отзывчивый пес.", "10", "0", "1722853186"));
        petsOnAdoption.add(List.of("1", "Барбос", "Собака", "Надежный охранник.", "3", "0", "6725110697"));
        petsOnAdoption.add(List.of("10", "Тайга", "Собака", "Тренированная охотничья собака. Девочка.", "6", "2", "6725110697"));
        List<List<String>> pets = petsOnAdoption.stream()
                .filter(p -> (Long.parseLong(p.get(6)) == chatId))
                .toList();
        if (pets.size() > 1) {
            for (List<String> pet : petsOnAdoption) {
                SendMessage petMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text(pet.get(1) + "\n" + pet.get(2)).build();
                String[][][] buttons = {{{"Выбрать", "REPORT" + pet.get(0)}}};
                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
                petMessage.setReplyMarkup(inlineKeyboardMarkup);
                executeAndSendMessage(petMessage);
            }
        } else {
            menuReportPetCurrent(Long.parseLong(petsOnAdoption.get(0).get(0)));
        }
    }

    protected void menuReportPetCurrent(Long petId) {
        List<List<String>> petsOnAdoption = new ArrayList<>();
        // нужно получить chatId из объекта Pet из базы
        petsOnAdoption.add(List.of("0", "Шарик", "Собака", "Добрый, отзывчивый пес.", "10", "0", "1722853186"));
        petsOnAdoption.add(List.of("1", "Барбос", "Собака", "Надежный охранник.", "3", "0", "6725110697"));
        petsOnAdoption.add(List.of("10", "Тайга", "Собака", "Тренированная охотничья собака. Девочка.", "6", "2", "6725110697"));
        Long chatId = petsOnAdoption.stream()
                .filter(pet -> Long.parseLong(pet.get(0)) == petId).findFirst().map(p -> Long.parseLong(p.get(6))).get();

        SendMessage message = SendMessage.builder().chatId(chatId)
                .text("Отправьте сообщение с текстом отчета:\n" +
                        "- Опишите рацион животного.\n" +
                        "- Общее самочуствие и привыкание к новому месту." +
                        "- Изменения в поведении: отказ от старых привычек, приобретение новых." +
                        "\n Отменить отправку отчета Вы можете по кнопке внизу окна.").build();
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.addAll(List.of("Отменить отчет"));
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true); // resizes keyboard to smaller size
        keyboardMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(keyboardMarkup);
        executeAndSendMessage(message);
    }

    protected void menuContactRequest(Long chatId, Long shelterId) {
        Shelter shelter = botService.findShelter(shelterId);
        botStates.put(chatId, BotState.COLLECT_DATA);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text("Чтобы оставить заявку на телефонный звонок, нажмите на кнопку с названием " + shelter.getName() + ".")
                .build();

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = KeyboardButton.builder().text(shelter.getName()) // Added button with SHELTER NAME
                .requestContact(true)
                .build();
        row.add(keyboardButton);
        row.addAll(List.of("Отмена"));
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true); // resizes keyboard to smaller size
        keyboardMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(keyboardMarkup);
        executeAndSendMessage(message);
    }

    // ИЗБАВИТЬСЯ ОТ ЗАХАРДКОЖЕНОГО АЙДИ ВОЛОНТЕРА 1722853186
//    И АЙДИ ПОЛЬЗОВАТЕЛЯ(УСЫНОВИТЕЛЯ) 6725110697


}


