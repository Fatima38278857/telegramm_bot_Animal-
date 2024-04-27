package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;

import java.util.List;
import java.util.Optional;

public interface CaretakerService {

    Caretaker addCaretaker(Long chatId,String name, String address);
    Caretaker addCaretaker(Caretaker caretaker);

    void delete(Long id);
    String getNameById(Long id);
    Optional<Caretaker> getById(Long id);

    List<Caretaker> getAllCaretaker();

    void setProbation(boolean bol, String name);

}
