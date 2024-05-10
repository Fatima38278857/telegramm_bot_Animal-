package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.TelegramContact;
import pro.sky.javacourse.AnimalShelterBot.telegram_bot.TelegramBot;

import java.util.List;

@Service
public class BotServiceImpl implements BotService {
    private final TelegramContactService telegramContactService;
    private final ShelterService shelterService;
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    public BotServiceImpl(TelegramContactService telegramContactService, ShelterService shelterService) {
        this.telegramContactService = telegramContactService;
        this.shelterService = shelterService;
    }

    @Override
    public Boolean isVolunteer(Long chatId, Long shelterId) {
        return chatId == 1722853186L; // Здесь будет метод по проверке chatId по базе данных волонтеров.
    }

    @Override
    public List<Shelter> findShelters() {
        return shelterService.getAll();
    }

    @Override
    public Shelter findShelter(Long id) {
        return shelterService.find(id);
    }

    @Override
    public Shelter findShelterByName(String name) {
        return shelterService.findShelterByName(name);
    }

    @Override
    public TelegramContact saveContact(Contact contact, Shelter shelter) {
        TelegramContact telegramContact = new TelegramContact(contact.getUserId(), contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber(), shelter);
        logger.info("Was invoked method telegramContactService.add({})", contact);
        return telegramContactService.add(telegramContact);
    }

}
