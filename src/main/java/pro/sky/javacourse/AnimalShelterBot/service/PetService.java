package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Pet;

import java.util.List;

public interface PetService {
    Pet add(Pet pet);
    Pet get(Long id);
    Pet update(Long id, Pet pet);
    void delete(Long id);
    List<Pet> getByType(String type);
    Shelter getShelter(Long id);
    int getPetsCount();

}
