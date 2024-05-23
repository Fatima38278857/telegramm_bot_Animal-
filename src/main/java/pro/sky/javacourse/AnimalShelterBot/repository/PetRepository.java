package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.PetStatus;
import pro.sky.javacourse.AnimalShelterBot.model.PetType;

import java.util.Collection;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Collection<Pet> findByStatus(PetStatus status);
    Collection<Pet> findByName(String name);
    Collection<Pet> findByShelterId(Long shelterId);
    Collection<Pet> findByCaretakerId(Long caretakerId);
    Collection<Pet> findByCaretakerIdAndShelterId(Long caretakerId, Long shelterId);
    @Query(value = "SELECT * FROM pets WHERE pets.status = 'ДОСТУПЕН' AND pets.shelter_id = :shelterId", nativeQuery = true)
    List<Pet> findAvailableByShelterId(Long shelterId);
    @Query(value = "SELECT DISTINCT pets.type FROM pets WHERE pets.status = 'ДОСТУПЕН' AND pets.shelter_id = :shelterId", nativeQuery = true)
    List<PetType> getAvailablePetsTypesByShelterId(Long shelterId);
    @Query(value = "SELECT caretakers.chat_id FROM caretakers JOIN pets ON caretakers.id = pets.caretaker_id WHERE pets.id = :petId", nativeQuery = true)
    Long findChatIdById(Long petId) throws NullPointerException;
}
