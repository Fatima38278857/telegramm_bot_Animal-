package pro.sky.javacourse.AnimalShelterBot.telegramBot.menu;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class TelegramBotMenu {

    private String state = "start";
    private String botToken;
    private String botUserName;
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    List<KeyboardRow> keyboardRows = new ArrayList<>();

    public TelegramBotMenu(String botToken, String botUserName) {
        this.botToken = botToken;
        this.botUserName = botUserName;
    }

    KeyboardRow row = new KeyboardRow();

//    row.addAll(List.of("Button 1","Button 2"));
//    row.add("Button 2");
}
