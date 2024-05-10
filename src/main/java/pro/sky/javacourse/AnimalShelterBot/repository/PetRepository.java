package pro.sky.javacourse.AnimalShelterBot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pro.sky.javacourse.AnimalShelterBot.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}
