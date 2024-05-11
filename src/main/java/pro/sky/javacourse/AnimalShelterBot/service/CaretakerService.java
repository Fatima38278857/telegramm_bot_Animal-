package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;

import java.util.Collection;

public interface CaretakerService {
    Caretaker add(Caretaker caretaker);
    void delete(Caretaker caretaker);
    Collection<Caretaker> getAll();
    Caretaker find(Long id);
    Caretaker findByChatId(Long chatId);
    Caretaker edit(Caretaker caretaker);
}
