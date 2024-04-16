package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
