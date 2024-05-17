package pro.sky.javacourse.AnimalShelterBot.telegram_bot;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.*;
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
import pro.sky.javacourse.AnimalShelterBot.exception.ReportMaxMessagesException;
import pro.sky.javacourse.AnimalShelterBot.exception.ReportMaxPhotoException;
import pro.sky.javacourse.AnimalShelterBot.exception.ReportMaxTextException;
import pro.sky.javacourse.AnimalShelterBot.model.*;
import pro.sky.javacourse.AnimalShelterBot.service.BotService;
import pro.sky.javacourse.AnimalShelterBot.telegram_bot.state.BotState;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

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
    private final Map<Long, BotState> botStates = new HashMap<>(); // Key is chatId
    private final Map<Long, Long> shelterIdByChatId = new HashMap<>(); // Key is chatId
    private final Map<Long, Report> tempReports = new HashMap<>(); // Key is chatId


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
        // ALL BOT STATES

//        Code for replying incoming message. If replying user message then reply should be sent to corresponding chat.

        if (update.hasMessage() && update.getMessage().getReplyToMessage() != null && botStates.get(chatId) != BotState.COLLECT_DATA) {
            Long myChatId = update.getMessage().getChatId();
            Long targetChatId;
            try {
                targetChatId = update.getMessage().getReplyToMessage().getForwardFrom().getId();
            } catch (NullPointerException e) {
                logger.info("Error replying to message, getForwardFrom is null, possibly chatId " + myChatId +
                        " is trying to reply his (her) own or bots message");
                sendText(myChatId, ALTERNATIVE_TEXT);
                return;
            }
            User user = update.getMessage().getFrom();
            Integer messageId = update.getMessage().getMessageId();

            if (update.hasMessage()) {
                ForwardMessage forwardMessageText = new ForwardMessage(targetChatId.toString(), myChatId.toString(), messageId);
                try {
                    execute(forwardMessageText);
                } catch (TelegramApiException e) {
                    logger.error("Error executing forward message: " + e.toString());
                }
                logger.info(user.getFirstName() + ", chatId " + chatId + ", sent a reply message to chatId: " + targetChatId);
            }
        }
// End of code for replying incoming messages

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getReplyToMessage() == null) {
            Message message = update.getMessage();
            User user = message.getFrom();
            logger.info(user.getFirstName() + ", chatId " + chatId + ", wrote " + message.getText());

            switch (message.getText()) {
                // "/start" command should close all unfinished tasks, remove ReplyKeyboard and return user to the beginning
                case "/start" -> {
                    if (botStates.get(chatId) == BotState.VOLUNTEER_CHAT) {
                        sendText(chatId, "Чат с волонтером закрыт. Открыть чат можно выбрав приют.");
                    }
                    if (botStates.get(chatId) == BotState.COLLECT_DATA) {
                        sendText(chatId, "Отправка контакта отменена.");
                    }
                    if (botStates.get(chatId) == BotState.REPORT) {
                        deleteReport(chatId);
                        sendText(chatId, "Создание отчета отменено.");
                        botStates.put(chatId, BotState.COMMON);
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
//                    menuReportStart(chatId);
                    return; // return prevents update delivering to another botState onUpdateRecieved methods
                }
                case "/help" -> {
                    if (botStates.get(chatId) == BotState.REPORT) {
                        menuReportHelp(chatId);
                    } else {
                        menuHelp(chatId);
                        return; // return prevents update delivering to another botState onUpdateRecieved methods
                    }
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
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getReplyToMessage() == null) {
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
                    case "Отменить" -> {
                        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
                        SendMessage msg = SendMessage.builder().chatId(chatId).text("Клавиатура удалена.").replyMarkup(keyboardRemove).build();
                        executeAndSendMessage(msg);
                        menuShelterSelect(chatId);
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


//                    menuReportStart(chatId);
                }
                case "REPORT_PET_SELECT" -> {
                    menuReportPetSelect(chatId);
                }
                default -> {
                    if (callbackData.startsWith("SHELTER_LARGE")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuShelter(chatId, shelterId);
                    } else if (callbackData.startsWith("LOCATION_MAP")) {
                        menuLocation(update);
                    } else if (callbackData.startsWith("SHELTER_SMALL")) {
                        menuShelterCurrent(update);
                    } else if (callbackData.startsWith("HOW_TO")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuHowTo(chatId, shelterId);
                    } else if (callbackData.startsWith("ANIMAL_TYPES")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetType(chatId, shelterId);
                    } else if (callbackData.startsWith("СОБАКА")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetSelect(chatId, shelterId, PetType.СОБАКА);
                    } else if (callbackData.startsWith("КОТ")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetSelect(chatId, shelterId, PetType.КОТ);
                    } else if (callbackData.startsWith("ОСТАЛЬНЫЕ")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuPetSelect(chatId, shelterId, PetType.ОСТАЛЬНЫЕ);
                    } else if (callbackData.startsWith("AVATAR_PET")) {
                        Long petId = getIdFromCallbackData(callbackData);
                        menuPetAvatar(chatId, petId);
                    } else if (callbackData.startsWith("VOLUNTEER_CHAT")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuVolunteerChat(chatId, shelterId);
                    } else if (callbackData.startsWith("REPORT")) {
                        Long petId = getIdFromCallbackData(callbackData);
                        menuReportPetCurrent(chatId, petId);
                    } else if (callbackData.startsWith("CONTACT")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuContactRequest(chatId, shelterId);
                    } else if (callbackData.startsWith("SHELTER_REPORT_PET_SELECT")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuReportPetSelect(chatId, shelterId);
                    } else if (callbackData.startsWith("UNVERIFIED")) {
                        Long shelterId = getIdFromCallbackData(callbackData);
                        menuReportSelect(update.getCallbackQuery().getMessage().getChatId(), shelterId);
                    } else if (callbackData.startsWith("CHECK_REPORT")) {
                        menuReportCheck(update);
                    } else if (callbackData.startsWith("APPROVE")) {
                        Long volunteerChatId = update.getCallbackQuery().getMessage().getChatId();
                        Long reportId = getIdFromCallbackData(callbackData);
                        Report report = botService.approveReport(reportId, volunteerChatId);
                        Pet pet = report.getPet();
                        menuShelter(volunteerChatId, pet.getShelter().getId());
                    } else if (callbackData.startsWith("DECLINE")) {
                        Long volunteerChatId = update.getCallbackQuery().getMessage().getChatId();
                        Long reportId = getIdFromCallbackData(callbackData);
                        Report report = botService.declineReport(reportId, volunteerChatId);
                        Pet pet = report.getPet();
                        Long caretakerChatId = pet.getCaretaker().getChatId();
                        sendText(caretakerChatId, "Ваш отчет по питомцу " + pet.getName() +
                                " не принят волонтером. Необходимо предоставить новый отчет в соответсвии " +
                                "с рекоммендациями. В противном случае нам придется лично проверить содержание питомца.");
                        menuShelter(volunteerChatId, pet.getShelter().getId());
                    } else if (callbackData.startsWith("NO_TEXT")) {
                        Long volunteerChatId = update.getCallbackQuery().getMessage().getChatId();
                        Long reportId = getIdFromCallbackData(callbackData);
                        Report report = botService.findReport(reportId);
                        Pet pet = report.getPet();
                        Long caretakerChatId = pet.getCaretaker().getChatId();
                        sendText(caretakerChatId, "Дополните текстовую часть отчета в соответсвии с " +
                                "рекоммендациями. В противном случае нам придется лично проверить содержание питомца.");
                        sendText(volunteerChatId, "Направлено сообщение о неполном тексте опекуну.");
                    } else if (callbackData.startsWith("NO_PHOTO")) {
                        Long volunteerChatId = update.getCallbackQuery().getMessage().getChatId();
                        Long reportId = getIdFromCallbackData(callbackData);
                        Report report = botService.findReport(reportId);
                        Pet pet = report.getPet();
                        Long caretakerChatId = pet.getCaretaker().getChatId();
                        sendText(caretakerChatId, "Дополните отчет информативными фотографиями" +
                                " в соответсвии с рекоммендациями. В противном случае нам придется лично " +
                                "проверить содержание питомца.");
                        sendText(volunteerChatId, "Направлено сообщение о нехватке фото опекуну.");
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
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
            User user = update.getMessage().getFrom();
            Long chatId = update.getMessage().getChatId();
            SendMessage message = SendMessage.builder().chatId(chatId)
                    .text("Отправка сообщений волонтеру отключена")
                    .replyMarkup(keyboardRemove)
                    .build();
            executeAndSendMessage(message);
            logger.info(user + " chatId " + chatId + " closed chat with volunteer.");
            botStates.put(chatId, BotState.COMMON);
        } else if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            Long volunteerChatId = botService.findShelter(shelterIdByChatId.get(chatId)).getMainVolunteer().getChatId();
            User user = update.getMessage().getFrom();
            Integer messageId = update.getMessage().getMessageId();

            if (update.hasMessage()) {
                ForwardMessage forwardMessageText = new ForwardMessage(volunteerChatId.toString(), chatId.toString(), messageId);
                try {
                    execute(forwardMessageText);
                } catch (TelegramApiException e) {
                    logger.error("Error executing forward message: " + e.toString());
                }
                logger.info(user.getFirstName() + ", chatId " + chatId + ", sent a message to volunteer chatId " + volunteerChatId);
            }
        }
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

    private void onUpdateReceivedReport(Update update) {
        Long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
//        closing reportHelp menu
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("DELETE")) {
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            User user = update.getCallbackQuery().getFrom();
            DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatId).messageId(messageId).build();
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                logger.error("Error deleting message: " + e.toString());
            }
            logger.info(user.getFirstName() + ", chatId " + chatId + " deleted message");
            return;
        }

        //  Map tempReports <chatId, Report> must already have a Report to fill. Report was created by menuReportPetCurrent method
        Report report = tempReports.get(chatId);
        Message message = update.getMessage();

// Обрабатываем кнопку сохранения отчета
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("Сохранить отчет")) {
            if (report.getTextLength() < report.getMINTEXTLENGTH()) {
                sendText(chatId, "Текстовая часть отчета должна быть не менее 25 символов.");
                return;
            }
            if (report.getPhotosSize() == 0) {
                sendText(chatId, "К отчету также необходимо приложить новое фото питомца.");
                return;
            }
            botService.saveReport(report);
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
            SendMessage msg = SendMessage.builder().chatId(chatId).text("Отчет сохранен.").replyMarkup(keyboardRemove).build();
            executeAndSendMessage(msg);
            botStates.put(chatId, BotState.COMMON);
            menuShelterSelect(chatId);
        }
// Обрабатываем кнопку отмены отчета
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("Отменить")) {
            deleteReport(chatId);
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true, true);
            SendMessage msg = SendMessage.builder().chatId(chatId).text("Создание отчета отменено.").replyMarkup(keyboardRemove).build();
            executeAndSendMessage(msg);
            botStates.put(chatId, BotState.COMMON);
            menuShelterSelect(chatId);
        }
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/help")) {
            return; // команда /help обработается в onUpdateReceived();
        }
// проверяем есть ли место для нового сообщения в отчете
        if (report.getMessages() != null && report.getMessages().size() >= report.getMAXMESSAGES()) {
            logger.error("Превышен лимит количества сообщений в отчете. ChatId: " + chatId);
            sendText(chatId, "Превышен лимит количества сообщений в отчете.");
            throw new ReportMaxMessagesException();
        }
        // что делать с проброшенным эксепшном?
//   filling report with text messages from updates
        //   !!! don't forget to assign messages to report and report to messages when saving report to database
        // когда получено текстовое сообщение
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getReplyToMessage() == null) {
            // создаем объект ReportMessage для последующего сохранения в базе
            ReportMessage reportMessage = new ReportMessage(update.getMessage().getMessageId());
            reportMessage.setChatId(chatId);
            // проверяем доступное место для текста в отчете
            if (report.getTextLength() + message.getText().length() < report.getMAXTEXTLENGTH()) {
                report.setTextLength(report.getTextLength() + message.getText().length());
            } else {
                logger.error("Ошибка! Превышена общая длина текстовых сообщений в отчете. ChatId: " + chatId);
                sendText(chatId, "Ошибка! Превышена общая длина текстовых сообщений в отчете.");
                throw new ReportMaxTextException();
            }
            // инициализируем поля сообщения
            reportMessage.setId(message.getMessageId());
            reportMessage.setText(message.getText());
            reportMessage.setFirstName(message.getFrom().getFirstName());
            reportMessage.setLastName(message.getFrom().getLastName());
            reportMessage.setUserName(message.getFrom().getUserName());
            reportMessage.setDate(message.getDate());
            // добавляем сообщение в список сообщений в отчете
            report.getMessages().add(reportMessage);
//     filling report with photo messages from updates
            // когда получено фото сообщение
        } else if (update.hasMessage() && update.getMessage().hasPhoto() && update.getMessage().getReplyToMessage() == null) {
            // создаем объект ReportMessage для последующего сохранения в базе
            ReportMessage reportMessage = new ReportMessage(update.getMessage().getMessageId());
            // проверяем есть ли место для фотографий в отчете
            // получаем информацию о фото
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);
            Integer fileSize = photo.getFileSize();
            if (report.getPhotosSize() + fileSize >= report.getMAXPHOTOSIZE()) {
                logger.error("Превышен максимальный объем фотографий в отчете. ChatId: " + chatId);
                sendText(chatId, "Превышен максимальный объем фотографий в отчете.");
                throw new ReportMaxPhotoException();
            } else {
                report.setPhotosSize(report.getPhotosSize() + fileSize);
            }
            // инициализируем поля объекта сообщения
            reportMessage.setChatId(chatId);
            reportMessage.setFirstName(message.getFrom().getFirstName());
            reportMessage.setLastName(message.getFrom().getLastName());
            reportMessage.setUserName(message.getFrom().getUserName());
            reportMessage.setDate(message.getDate());
            if (message.getCaption() != null) {
                reportMessage.setCaption(message.getCaption());
            }
            if (message.getMediaGroupId() != null) {
                reportMessage.setMediaGroupId(message.getMediaGroupId());
            }
            reportMessage.setFileUniqueId(photo.getFileUniqueId());
            reportMessage.setFileSize(fileSize);
// Code for downloading photo from telegram update
// path to download set using template: reportsDir/petId_messageDate_indexOfMessageInList_reportStatus.fileExtension
            String fileId = photo.getFileId();
            GetFile getFileRequest = new GetFile();
            getFileRequest.setFileId(fileId);
            // объявляем объект специального класса телеграмм File
            org.telegram.telegrambots.meta.api.objects.File file;
            // объявляем переменную для хранения пути, чтобы она была доступна после блока try
            String downloadToPath;
            try {
                file = execute(getFileRequest);
                downloadToPath = botService.getReportsDir() + "/" + report.getPet().getId() + "_" + message.getDate() + "_"
                        + report.getMessages().size() + "_" + report.getStatus().name() + "." + botService.getExtensions(file.getFilePath());
                downloadFile(file, new File(downloadToPath));
                logger.info("Успешно загружено фото отчета: " + downloadToPath);
                reportMessage.setFilePath(downloadToPath);
            } catch (TelegramApiException e) {
                logger.error("Ошибка загрузки фото из отчета по питомцу " + report.getPet().getId());
                sendText(chatId, "Ошибка загрузки фото");
                return;
            }
// End of code for downloading photo from telegram update
            // добавляем сообщение в список сообщений в отчете
            report.getMessages().add(reportMessage);
            // сначала нужно сохранить отчет, чтобы можно его id присвоить сообщениям
        } else {
            sendText(chatId, ALTERNATIVE_TEXT);
        }
//        можно для незаконченных отчетов сделать папку INCOMPLETE, т.е. перенести статус в название директории
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
                "Для простого выхода из режима диалога или отчета воспользуйтесь кнопкой внизу. Для отправки отчета также предусмотрена кнопка внизу окна, " +
                "при выходе из отчета командой /start отчет не отправится. В случае разрыва связи или перегруженности бота бот также может " +
                "не реагировать на кнопки. Надеемся на понимание.";

        String[][][] buttons = {{{"Ok", "DELETE"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndSendMessage(message); // отображение сообщения в чате, отправка в чат
    }

    protected void menuReportHelp(Long chatId) {
        String text = "Отправляйте боту текстовые сообщения и фотографии питомца. " +
                "Отменить создание отчета можно командой /start, но тогда отчет придется создавать заново. " +
                "Отчет обязательно должен содержать фото питомца и текст:" +
                "\n- Опишите общее самочувствие животного и привыкание к новому месту." +
                "\n- Опишите рацион животного." +
                "\n- Изменения в поведении: отказ от старых привычек, приобретение новых." +
                "\n- Опишите рацион животного." +
                "\n- Можете добавить то что, Вы считаете нужным или то, с чам Вам нужна консультация или помощь." +
                "\nВ отчете не будут сохранены подписи к фото. Другие типы данных в отчете не сохраняются." +
                "Максимальный размер отчета 50 мегабайт." +
                "\nВАЖНО: Чтобы сохранить и отправить отчет нужно нажать на кнопку внизу окна.";
        String[][][] buttons = {{{"Ok", "DELETE"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndSendMessage(message);
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
            String[][][] buttons = {{{"Схема проезда", "LOCATION_MAP" + shelter.getId()}, {"Выбрать", "SHELTER_LARGE" + shelter.getId()}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            shelterMessage.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(shelterMessage);
        }
        menuReportStart(chatId);
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

        String[][][] buttons = {{{"Схема проезда", "LOCATION_MAP" + shelterId}, {"Выбрать", "SHELTER_LARGE" + shelterId}}};
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
// добавить проверку есть ли в данном приюте непроверенные отчеты


            List<Report> unverifiedReports = botService.findByStatus(ReportStatus.UNVERIFIED).stream()
                    .filter(report -> Objects.equals(report.getPet().getShelter().getId(), shelterId))
                    .toList();
            if (!unverifiedReports.isEmpty()) {
                for (Report report : unverifiedReports) {
                    keyboardRows.add(List.of(createInlineKeyBoardButton("Проверить отчеты опекунов", "UNVERIFIED" + shelterId)));
                }
            }
        }
        if (botService.caretakerHasPets(chatId, shelterId)) { // если у опекуна есть питомцы на испытательном сроке в данном приюте
            keyboardRows.add(List.of(createInlineKeyBoardButton("Сдать отчет", "SHELTER_REPORT_PET_SELECT" + shelterId)));
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

        String[][][] buttons = {{{"Схема проезда", "LOCATION_MAP" + shelterId}, {"Выбрать", "SHELTER_LARGE" + shelterId}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);

        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(shelter.getName() + "\n" + shelter.getAddress() +
                        "\nСхема проезда к приюту " + shelter.getName() + " отправлена в чат.\"")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        executeAndEditMessage(editMessage);
        menuLocationMapPhoto(update);
    }

    protected void menuHowTo(Long chatId, Long shelterId) {
        Shelter shelter = botService.findShelter(shelterId);
        String[][][] buttons = {{{"OK", "SHELTER_LARGE" + shelterId}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text(shelter.getName() + "\n\n" + shelter.getHowTo())
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndSendMessage(message);
    }

    protected void menuPetType(Long chatId, Long shelterId) {
        List<PetType> petTypes = botService.getAvailablePetTypes(shelterId);
        if (petTypes.size() == 1) {
            menuPetSelect(chatId, shelterId, petTypes.get(0));
        } else {

            SendMessage message = SendMessage.builder().chatId(chatId).text("Выберите тип питомца").build();
            String[][][] buttons = new String[petTypes.size() + 1][1][2]; // массив из кнопок по 1 в ряд, количество рядов
            // равно размеру списка типов животных + 1 ряд для кнопки "Назад", callBackData возвращаемая кнопкой с типом
            // животного равна типу животного в верхнем регистре + Id приюта, чтобы сделать выборку.
            for (int i = 0; i < petTypes.size(); i++) {
                buttons[i][0][0] = petTypes.get(i).name();
                buttons[i][0][1] = petTypes.get(i).name() + shelterId; // чтобы кнопки работали типы животных должны
                // быть в enum.
            }
            buttons[petTypes.size()][0][0] = "Назад";
            buttons[petTypes.size()][0][1] = "SHELTER_LARGE" + shelterId;
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(message);
        }
    }

    protected void menuPetSelect(Long chatId, Long shelterId, PetType petType) {
//        retrieving available pets from shelter
        List<Pet> pets = botService.findAvailableByShelterId(shelterId);
//        counting pet types quantity
        long petTypes = pets.stream()
                .map(Pet::getType)
                .distinct()
                .count();
//        removing all pets except of needed type
        pets = pets.stream().filter(pet -> pet.getType() == petType).toList();
//        creating messages for each case: no pets available and for each pet.
        SendMessage message = new SendMessage();
        if (pets.isEmpty()) {
            message = SendMessage.builder().chatId(chatId).text("Нет питомцев для усыновления").build();
            String[][][] buttons = {{{"Назад", "SHELTER_LARGE" + shelterId}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(message);
        } else {
            for (Pet pet : pets) {
                String[][][] buttons = {{{"Выбрать", "AVATAR_PET" + pet.getId()}}};
                message = SendMessage.builder().chatId(chatId)
                        .text(pet.getName() + "\n" + pet.getAbilities() + " Возраст " + pet.getAge() + " полных лет")
                        .replyMarkup(createInlineKeyboardMarkup(buttons))
                        .build();
                executeAndSendMessage(message);
            }
            message = SendMessage.builder().chatId(chatId).text("Вернуться в предыдущее меню").build();
//        if there is only one pet type then return button will guide to shelter menu, else to selectPetType menu.
            if (petTypes == 1) {
                String[][][] buttons = {{{"Назад", "SHELTER_LARGE" + shelterId}}};
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
        shelterIdByChatId.put(chatId, shelterId);
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
        if (botService.caretakerHasPets(chatId)) { // if caretaker has pets on adoption
            SendMessage message = SendMessage.builder().chatId(chatId).text("Сдать отчет").build();
            String[][][] buttons = {{{"Начать", "REPORT_PET_SELECT"}}};
            InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
            message.setReplyMarkup(inlineKeyboardMarkup);
            executeAndSendMessage(message);
        }
    }

    // Этот метод предлагает список всех питомцев для заполнения отчетов
    protected void menuReportPetSelect(Long chatId) {
        List<Pet> pets = botService.caretakerPets(chatId);
        if (pets.size() > 1) {
            for (Pet pet : pets) {
                SendMessage petMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text(pet.getName() + "\n" + pet.getType().name()).build();
                String[][][] buttons = {{{"Выбрать", "REPORT" + pet.getId()}}};
                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
                petMessage.setReplyMarkup(inlineKeyboardMarkup);
                executeAndSendMessage(petMessage);
            }
        } else {
            menuReportPetCurrent(chatId, pets.get(0).getId());
        }
    }

    // Этот метод предлагает список питомцев из конкретного приюта для заполнения отчетов
// Практически полностью дублирует menuReportPetSelect(Long chatId)
    protected void menuReportPetSelect(Long chatId, Long shelterId) {
        List<Pet> pets = botService.caretakerPets(chatId, shelterId);
        if (pets.size() > 1) {
            for (Pet pet : pets) {
                SendMessage petMessage = SendMessage.builder()
                        .chatId(chatId)
                        .text(pet.getName() + "\n" + pet.getType().name()).build();
                String[][][] buttons = {{{"Выбрать", "REPORT" + pet.getId()}}};
                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
                petMessage.setReplyMarkup(inlineKeyboardMarkup);
                executeAndSendMessage(petMessage);
            }
        } else {
            menuReportPetCurrent(chatId, pets.get(0).getId());
        }
    }

    //    Этот метод открывает чат для отправки reportMessages
    protected void menuReportPetCurrent(Long chatId, Long petId) {
        Pet pet = botService.findPet(petId); // Будет использован при отправке отчета.
        try {
            chatId = botService.findChatIdByPetId(petId); // throws NPE when pet no longer have caretaker
        } catch (NullPointerException e) {
            return;
        }
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text("Кличка питомца - " + pet.getName() + "\nОтправьте сообщение с текстом отчета:\n" +
                        "- Опишите рацион животного.\n" +
                        "- Общее самочувствие и привыкание к новому месту." +
                        "- Изменения в поведении: отказ от старых привычек, приобретение новых." +
                        "\n Отменить отправку отчета Вы можете по кнопке внизу окна." +
                        "\n ВАЖНО! Перед сохранением отчета убедитесь, что ваши фото загрузились на сервер.").build();
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();


        KeyboardRow row = new KeyboardRow();
        row.addAll(List.of("Сохранить отчет"));
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.addAll(List.of("Отменить"));
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true); // resizes keyboard to smaller size
        keyboardMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(keyboardMarkup);
        executeAndSendMessage(message);
//        CREATING REPORT TO WORK WITH
        botStates.put(chatId, BotState.REPORT);
        tempReports.put(chatId, new Report(botService.findPet(petId), botService.findCaretakerByChatId(chatId)));
    }

    //    Этот метод открывает чат для отправки контактных данных TelegramContact в приют
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

    // Этот метод показывает карту проезда к приюту
    private void menuLocationMapPhoto(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long shelterId = getIdFromCallbackData(callbackData);
        Shelter shelter = botService.findShelter(shelterId);

        String filePath = shelter.getLocationMapFilePath();
        InputFile image = new InputFile(new File(filePath));
        String caption = "Приют \"" + shelter.getName() + "\". " + shelter.getAddress() + " " + shelter.getRegime();
        String[][][] buttons = {{{"Закрыть", "DELETE"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendPhoto locationMap = SendPhoto.builder()
                .chatId(chatId)
                .photo(image)
                .caption(caption)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        try {
            execute(locationMap);
        } catch (TelegramApiException e) {
            logger.error("Error executing photo message: " + e.toString());
        }
    }

    // Этот метод показывает подробную информацию о питомце и его аватар
    private void menuPetAvatar(Long chatId, Long petId) {
        Pet pet = botService.findPet(petId);
        String filePath = pet.getAvatarFilePath();
        InputFile image = new InputFile(new File(filePath));

        String caption = pet.getName() + "\n" + pet.getAbilities() + " Ограничения: " + pet.getRestrictions() +
                " Условия содержания или транспортировки: " + pet.getConditions();
        String[][][] buttons = {{{"Закрыть", "DELETE"}}};
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendPhoto locationMap = SendPhoto.builder()
                .chatId(chatId)
                .photo(image)
                .caption(caption)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        try {
            execute(locationMap);
        } catch (TelegramApiException e) {
            logger.error("Error executing photo message: " + e.toString());
        }
    }

    //    Этот метод очищает память от отчетов, либо сохраненных, либо отмененных
    private void deleteReport(Long chatId) {
        // сделать удаление временно загруженных фотографий
        tempReports.remove(chatId);
        logger.info("Report for chatId({}) deleted", chatId);
    }

    //    Этот метод выводит список непроверенных отчетов
    protected void menuReportSelect(Long chatId, Long shelterId) {
        if (botService.isVolunteer(chatId, shelterId)) {
            List<Report> unverifiedReports = botService.findByStatus(ReportStatus.UNVERIFIED).stream()
                    .filter(report -> Objects.equals(report.getPet().getShelter().getId(), shelterId))
                    .toList();
            if (!unverifiedReports.isEmpty()) {
                for (Report report : unverifiedReports) {
                    menuReportSmall(chatId, report);
                }
                String[][][] buttons = {{{"Назад", "SHELTER_LARGE" + shelterId}}};
                InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
                SendMessage message = SendMessage.builder().chatId(chatId)
                        .text("Вернуться в предыдущее меню.")
                        .replyMarkup(inlineKeyboardMarkup).build();
                executeAndSendMessage(message);
            }
        }
    }

    // Этот метод создает один элемент меню из метода menuReportSelect
    protected void menuReportSmall(Long chatId, Report report) {
        Pet pet = report.getPet();
        String[][][] buttons = {{{"Проверить", "CHECK_REPORT" + report.getId()}}}; // сделать точку входа
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text(report.getCreationTime().toString() + "-" + pet.getName() + "-" + "ID " + pet.getId())
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndSendMessage(message);
    }

    //    Это метод по проверке, согласованию или отклонению отчета
    protected void menuReportCheck(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long reportId = getIdFromCallbackData(callbackData);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        sendText(chatId, "----------Начало отчета-----------");
        // получаем список сообщений в отчете
        List<ReportMessage> messages = botService.findReportMessages(reportId);
// для каждого сообщения выполняем либо sendText, либо execute(SendPhoto)
        for (ReportMessage message : messages) {
            if (message.getText() != null) {
                sendText(chatId, message.getText());
            } else if (message.getFilePath() != null) {
                InputFile image = new InputFile(new File(message.getFilePath()));
                SendPhoto messagePhoto = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(image)
                        .build();
                if (message.getCaption() != null) {
                    messagePhoto.setCaption(message.getCaption());
                }
                try {
                    execute(messagePhoto);
                } catch (TelegramApiException e) {
                    logger.error("Error executing report message: " + e.toString());
                }
            }
        }
        // выводим кнопки с действиями с отчетом
        String[][][] buttons = {{{"Утвердить", "APPROVE" + reportId}, {"Отказать", "DECLINE" + reportId}},
                {{"Неполный текст", "NO_TEXT" + reportId}, {"Нужны фото", "NO_PHOTO" + reportId}}};
        // сделать точку входа
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(buttons);
        SendMessage message = SendMessage.builder().chatId(chatId)
                .text("-------Проверить отчет--------")
                .replyMarkup(inlineKeyboardMarkup).build();
        executeAndSendMessage(message);
    }


}


//    TO DO:
//    Сделать проверку отчетов. (report state, volunteer state)
//    Сделать отрисовку изображений в сообщениях с отчетами. Возможно в чате с волонтером.
//    Когда пропущены отчеты, тогда должны присылаться сообщения с предупреждениями опекуну и главному волонтеру приюта.
//    Перед сохранением отчета должна быть проверка на минимальный текст и наличие фото.
//    Попробовать поменять hibernate на create
// Внимание, метод ReportMessageRepository и метод ReportRepository.save может работать некорректно в связи с тем,
// что я не заменяю переданный объект вновь полученным из базы, следовательно, может вернуться исходный объект без Id
// /help сохраняется в отчете



