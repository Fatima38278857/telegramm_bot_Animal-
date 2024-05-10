package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.TelegramContact;

import java.util.Collection;
import java.util.List;

public interface TelegramContactService {
    public List<TelegramContact> getAll();

    Collection<TelegramContact> getUnsolved();

    TelegramContact findById(String phoneNumber);

    TelegramContact solve(TelegramContact contact);

    TelegramContact add(TelegramContact telegramContact);
}
