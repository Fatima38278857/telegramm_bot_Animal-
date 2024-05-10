package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;

import java.util.Collection;
import java.util.List;

public interface VolunteerService {
    List<Volunteer> getAll();

    Volunteer add(Volunteer volunteer);

    Volunteer find(Long id);

    Volunteer findByChatId(Long chatId);

    Collection<Shelter> getShelters(Long id);

    Volunteer edit(Long id, Volunteer volunteer);

    void addToShelter(Long id, Long shelterId);

    void removeFromShelter(Long id, Long shelterId);

    void delete(Long id);
}
