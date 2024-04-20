package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.modal.Caretaker;

public interface CaretakerService {

    Caretaker addCaretaker(Long chatId,String name, String address);
    void getNameById(Long id);

}
