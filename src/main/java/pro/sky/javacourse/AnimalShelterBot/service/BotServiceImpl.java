package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import pro.sky.javacourse.AnimalShelterBot.model.*;

import java.util.Comparator;
import java.util.List;

@Service
public class BotServiceImpl implements BotService {
    private final TelegramContactService telegramContactService;
    private final ShelterService shelterService;
    private final CaretakerService caretakerService;
    private final PetService petService;
    private final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);

    public BotServiceImpl(TelegramContactService telegramContactService, ShelterService shelterService, CaretakerService caretakerService, PetService petService) {
        this.telegramContactService = telegramContactService;
        this.shelterService = shelterService;
        this.caretakerService = caretakerService;
        this.petService = petService;
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
    public boolean caretakerHasPets(Long chatId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        if (caretaker == null) return false;
        return !petService.findByCaretakerId(caretaker.getId()).isEmpty();
    }

    @Override
    public boolean caretakerHasPets(Long chatId, Long shelterId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        if (caretaker == null) return false;
        return !petService.findByCaretakerIdAndShelterId(caretaker.getId(), shelterId).isEmpty();
    }

    @Override
    public TelegramContact saveContact(Contact contact, Shelter shelter) {
        TelegramContact telegramContact = new TelegramContact(contact.getUserId(), contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber(), shelter);
        logger.info("Was invoked method telegramContactService.add({})", contact);
        return telegramContactService.add(telegramContact);
    }

    @Override
    public Pet findPet(Long petId) {
        return petService.find(petId);
    }

    @Override
    public List<PetType> getAvailablePetTypes(Long shelterId) {
        return petService.getAvailablePetTypes(shelterId);
    }

    @Override
    public List<Pet> findAvailableByShelterId(Long shelterId) {
        return petService.findAvailableByShelterId(shelterId);
    }

    @Override
    public List<Pet> caretakerPets(Long chatId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        return petService.findByCaretakerId(caretaker.getId()).stream().toList();
    }

    @Override
    public List<Pet> caretakerPets(Long chatId, Long shelterId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        return petService.findByCaretakerIdAndShelterId(caretaker.getId(), shelterId).stream().toList();
    }

    @Override
    public Long findChatIdByPetId(Long petId) {
        return petService.findChatIdByPetId(petId);
    }

}
