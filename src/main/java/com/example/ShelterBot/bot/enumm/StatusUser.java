package com.example.ShelterBot.bot.enumm;
/**
 * Перечесление которая отвечает за
 * статус пользователя
 *
 */
public enum StatusUser {
    // статусы пользователя

    POTENTIAL("потенциальный усыновитель"),
    PROBATION("на испытательном сроке"),
    UNTRUSTED("не надежный");

    public final String status;

    StatusUser(String status) {
        this.status = status;
    }
}
