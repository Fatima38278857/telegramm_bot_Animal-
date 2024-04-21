package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;

@Service
public interface CaretakerService {

    Caretaker addCaretaker(Long chatId, String name, String address);

    void addContacts(Long chatId, String contacts);

    void addContactsOnMap(Long chatId, String contacts);

}
