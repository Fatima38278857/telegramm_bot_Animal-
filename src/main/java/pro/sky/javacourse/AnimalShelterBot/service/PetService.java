package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Pet;

public interface PetService {
    Pet add(Pet pet);
    Pet get(Long id);
    Pet update(Long id, Pet pet);
    void delete(Long id);

}
