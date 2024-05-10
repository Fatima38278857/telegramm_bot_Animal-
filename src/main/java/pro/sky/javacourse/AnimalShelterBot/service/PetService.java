package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.PetStatus;

import java.io.IOException;
import java.util.Collection;

public interface PetService {
    Pet add(Pet pet);
    void uploadAvatar(Long petId, MultipartFile avatarFile) throws IOException;
    Pet find(Long id);
    Pet find(String name);
    Collection<Pet> getAll();
    Collection<Pet> findByStatus(PetStatus status);
    Pet edit(Long id, Pet pet);
    Pet startTrial(Pet pet, Caretaker caretaker);
    Pet trialAdd15(Pet pet);
    Pet trialAdd30(Pet pet);
    Pet adopt(Pet pet);
    Pet returned(Pet pet);
    Pet suspended(Pet pet);
    Pet available(Pet pet);
    Pet ill(Pet pet);
}
