package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.PetStatus;
import pro.sky.javacourse.AnimalShelterBot.model.PetType;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface PetService {
    Pet add(Pet pet);
    Pet uploadAvatar(Long petId, MultipartFile avatarFile) throws IOException;
    Pet find(Long id);
    Collection<Pet> find(String name);
    Collection<Pet> getAll();
    Collection<Pet> findByStatus(PetStatus status);
    List<Pet> findAvailableByShelterId(Long shelterId);
    Collection<Pet> findByCaretakerId(Long caretakerId);
    Collection<Pet> findByShelterId(Long shelterId);
    Collection<Pet> findByCaretakerIdAndShelterId(Long id, Long shelterId);
    Pet edit(Pet pet);
    Pet startTrial(Pet pet, Long caretakerId);
    Pet trialAdd15(Pet pet);
    Pet trialAdd30(Pet pet);
    Pet adopt(Pet pet);
    Pet returned(Pet pet);
    Pet suspended(Pet pet);
    Pet available(Pet pet);
    Pet ill(Pet pet);
    void delete(Pet pet);
    List<PetType> getAvailablePetTypes(Long shelterId);
    Long findChatIdByPetId(Long petId);
}
