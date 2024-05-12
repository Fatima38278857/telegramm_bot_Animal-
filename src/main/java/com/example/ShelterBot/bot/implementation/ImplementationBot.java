package com.example.ShelterBot.bot.implementation;

import com.example.ShelterBot.bot.enumm.StatusReport;
import com.example.ShelterBot.bot.enumm.StatusUser;
import com.example.ShelterBot.bot.exception.ValueZeroException;
import com.example.ShelterBot.bot.model.Report;
import com.example.ShelterBot.bot.model.User;
import com.example.ShelterBot.bot.repository.ReportRepository;
import com.example.ShelterBot.bot.repository.UserRepository;
import com.example.ShelterBot.bot.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.example.ShelterBot.bot.enumm.Command.USER_DOES_NOT_HAVE_ACCESS;
import static com.example.ShelterBot.bot.enumm.StatusUser.POTENTIAL;
import static com.example.ShelterBot.bot.enumm.StatusUser.PROBATION;


/**
 * Класс отвечает за работу с телеграмм
 */
@Slf4j
@Component
public class ImplementationBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReportRepository reportRepository;


    private final Map<Long, String> user = new HashMap<>();
    private final Map<Long, String> userText = new HashMap<>();
    private final Map<Long, byte[]> userPhoto = new HashMap<>();
    @Value("${bot.name}")
    private String nameBot;
    private final Logger logger = LoggerFactory.getLogger(ImplementationBot.class);

    public ImplementationBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }


    // Часть, отвечающая за обработку входящих сообщений
    @Override
    public void onUpdateReceived(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (messageText != null && messageText.equals("/start")) {
            registerUser(update.getMessage());
            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            textMessage(update);
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            photoMessage(update);
        }
    }

    /**
     * Обрабатывает текстовые сообщения от пользователей.
     *
     * @param update объект Update, содержащий информацию о входящем сообщении.
     *               Процессор текстовых сообщений реагирует на команду /report,
     *               проверяет на наличие соответствущего статуса,
     *               а также управляет процессом получения текста отчета после
     *               предварительной отправки фото.
     */

    private void textMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        // Проверяем, является ли входящее текстовое сообщение командой /report
        if (update.getMessage().getText().equals("/report")) {
            User user = userRepository.findByChatId(chatId).orElseThrow(ValueZeroException::new);
            // Проверяем, имеет ли пользователь статус PROBATION
            if (user.getStatus() == PROBATION) {
                // Здесь this подчеркивает, что мы работаем с полем объекта user
                // Устанавливаем флаг ожидания фото для данного пользователя
                this.user.put(chatId, "AWAITING_PHOTO");
                sendMessage(chatId, "Пожалуйста, отправьте фото.");
            } else {
                sendMessage(chatId, "У вас нет доступа к отправке отчетов. Эта функция доступна только пользователям со статусом PROBATION.");
            }
        } else if ("AWAITING_TEXT".equals(this.user.get(chatId))) {
            // Обработка случая, когда пользователь уже отправил фото и ожидается текст отчета
            // Пользователь уже отправил фото, теперь ожидаем текст
            userText.put(chatId, update.getMessage().getText());
            this.user.remove(chatId);
            savePhotoAndText(chatId);// для сохранения данных в базу данных
        } else {
            System.out.println("Текущее состояние в this.user для chatId: " + this.user.get(chatId));
            sendMessage(chatId, "Что-то не так");
        }
    }

    /**
     * Обрабатывает фотосообщения от пользователей.
     *
     * @param update объект Update, содержащий информацию о входящем фотосообщении.
     *               Процессор фотосообщений извлекает file_id фотографии, загружает
     *               ее и переводит пользователя в состояние ожидания текста отчета.
     *               В методе происходит получение фото из сервера телеграмм и переобразование
     *               его в массив byte
     */

    private void photoMessage(Update update) {
        Long chatId = update.getMessage().getChatId();

        try {
            // Получаем file_id первой фотографии в сообщении
            String fileId = update.getMessage().getPhoto().get(0).getFileId();
            // Инициализируем объект запроса на получение файла
            GetFile getFileRequest = new GetFile();
            // Устанавливаем идентификатор файла, который необходимо получить
            getFileRequest.setFileId(fileId);
            // Получаем File объект, который содержит информацию о файле, включая путь к файлу
            File file = execute(getFileRequest);

            // Загружаем файл по полученному пути и читаем его содержимое в массив байт
            InputStream fileStream = downloadFileAsStream(file.getFilePath());

            // Чтение файла и запись в массив байтов
            // Создается объект ByteArrayOutputStream, который будет использоваться для записи содержимого файла.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             // Объявляется переменная read, которая будет использоваться для хранения количества байтов,
            // прочитанных из потока в каждой итерации цикла.
            int read;
            //  Создается буфер для чтения размером 1024 байта.
            //  В этом буфере будет храниться прочитанное содержимое
            //  файла перед записью в ByteArrayOutputStream.
            byte[] data = new byte[1024]; // Буфер для чтения
             // Это цикл, который читает содержимое файла из fileStream в буфер data.
            // Метод read() читает данные из потока в буфер data, возвращает количество
            // прочитанных байтов и -1 в случае, если конец потока был достигнут. Цикл продолжается,
            // пока не будет достигнут конец файла.
            while ((read = fileStream.read(data, 0, data.length)) != -1) {
                // Прочитанные данные записываются в ByteArrayOutputStream.
                // Метод write() записывает данные из буфера data в buffer.
                buffer.write(data, 0, read);
            }
            // Этот метод очищает буфер вывода, заставляя все байты быть записанными в массив.
            // Говорят что это не обязательно, но это хорошая практика
            buffer.flush();
            // Здесь содержимое ByteArrayOutputStream преобразуется в массив байтов с помощью метода toByteArray().
            // Этот массив byteArray содержит данные файла в виде массива байтов.
            byte[] byteArray = buffer.toByteArray();
            // Сохраняем массив байт фотографии и переводим статус пользователя
            userPhoto.put(chatId, byteArray);
            user.put(chatId, "AWAITING_TEXT");
            sendMessage(chatId, "Теперь отправьте текст вашего отчёта.");
        } catch (TelegramApiException e) {
            logger.error("Ошибка Telegram API");
        } catch (IOException e) {
            logger.error("Ошибка чтения файла");
            throw new RuntimeException(e);
        }
    }
    /**
     * Сохраняет фотографию и текст отчета, предоставленные пользователем.
     *
     * @param chatId идентификатор чата, используется для получения данных отчета и
     *               последующего сохранения в базе данных.
     */

    private void savePhotoAndText(Long chatId) {
        // Проверяем, имеются ли записи текста и фото для текущего пользователя
        // Код внутри блока if будет выполнен, если для пользователя с данным chatId
        // сохранены как текстовые, так и фотографические данные,
        // что необходимо для формирования полноценного отчета.
        if (userText.containsKey(chatId) && userPhoto.containsKey(chatId)) {

            User user = userRepository.findByChatId(chatId).orElseThrow(ValueZeroException::new);

            try {
                saveReport(user, userText.get(chatId), userPhoto.get(chatId));
                sendMessage(chatId, "Ваш отчёт сохранен. Спасибо!");
            } catch (Exception e) {
                logger.error("Ошибка при сохранении в базу данных", e);
                sendMessage(chatId, "Ошибка при сохранении отчета: " + e.getMessage());
            }
            // После сохранения данных можно очистить карты
            //  Удаление ключей из мап после сохранения обеспечивает очистку памяти
            //  и предотвращает замену
            //  уже сохраненных данных.
            userText.remove(chatId);
            userPhoto.remove(chatId);
        } else {
            logger.error("Данные для chatId={} не полные", chatId);
        }

    }


    private void saveReport(User user, String text, byte[] photo) {
        Report report = new Report();
        report.setText(text); // Это может быть текст сообщения или базовое обозначение, что отчет содержит фото
        report.setPhoto(photo); // Это  фото
        report.setUser(user); // Связываем отчет с пользователем
        report.setDateTime(LocalDateTime.now()); // Задаем время создания отчета
        report.setStatusReport(StatusReport.NOT_VERIFIED); // При сохронении отчета в бд, присваивается статус не проверенно
        reportRepository.save(report); // Сохраняем отчет
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
    private void registerUser(Message msg) {

        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = new User();


            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            user.setStatus(PROBATION);
            userRepository.save(user);
            logger.info("user saved: " + user);
        }
    }


    /**
     * Метод для настройки сообщения и его отправки.
     *
     * @param chatId     id чата
     * @param textToSend Строка, которую необходимот отправить в качестве сообщения.
     */
    public void sendMessage(long chatId, String textToSend) {
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
    public String getBotUsername() {
        return nameBot;
    }
}