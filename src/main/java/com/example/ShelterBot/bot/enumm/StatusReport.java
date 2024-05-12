package com.example.ShelterBot.bot.enumm;
/**
 * Перечесление которая отвечает за
 * статус отчета
 *
 */
public enum StatusReport {

    NOT_VERIFIED("Не проверенный"),
    VERIFIED("Проверенный");

    public final String status;

    StatusReport(String status) {
        this.status = status;
    }
}
