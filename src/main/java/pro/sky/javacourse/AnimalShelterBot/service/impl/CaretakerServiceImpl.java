package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

public class CaretakerServiceImpl implements CaretakerService {
    private CaretakerRepository caretakerRepository;

    public CaretakerServiceImpl(CaretakerRepository caretakerRepository) {
        this.caretakerRepository = caretakerRepository;
    }


    @Override
    public Caretaker addCaretaker(Long chatId, String name, String address) {
        Caretaker caretaker = new Caretaker(chatId, name, address);
        return caretakerRepository.save(caretaker);
    }

    @Override
    public void addContacts(Long chatId, String contacts) {
        caretakerRepository.findByChatId(chatId).setContacts(contacts);

    }

    @Override
    public void addContactsOnMap(Long chatId, String contacts) {
        Caretaker.getMapWithContacts().put(chatId, contacts);
    }

}
