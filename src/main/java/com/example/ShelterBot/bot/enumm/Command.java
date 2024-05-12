package com.example.ShelterBot.bot.enumm;
/**
* Перечесление которая отвечает за
* текст
*
 */
public enum Command {


    PRIVEDSTVIE("Пришлите отчет в последовательности Фото, текст"),
    USER_DOES_NOT_HAVE_ACCESS("У ваш не соответствует  статус");



    private final String messageText;

    Command(String messageText) {
        this.messageText = messageText;
    }



    public String getMessageText() {
        return messageText;
    }
}


