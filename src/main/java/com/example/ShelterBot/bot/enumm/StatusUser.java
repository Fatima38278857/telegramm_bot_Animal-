package com.example.ShelterBot.bot.enumm;

public enum StatusUser {
    // статусы пользователя

    POTENTIAL("потенциальный усыновитель"),
    PROBATION("на испытательном сроке"),
    TRUSTED("проверенный"),
    UNTRUSTED("не надежный");

    public final String status;

    StatusUser(String status) {
        this.status = status;
    }
}
