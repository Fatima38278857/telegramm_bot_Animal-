package com.example.ShelterBot.bot.enumm;
/**
 * Перечесление которая отвечает за
 * статус собаки
 *
 */
public enum StatusDog {

    ADOPTED("Усынасленный"),
    DURING_USER_TRIAL_PERIOD("На испытательном сроке у пользователя ");

    public final String statusDog;

    StatusDog(String statusDog) {
        this.statusDog = statusDog;
    }
}
