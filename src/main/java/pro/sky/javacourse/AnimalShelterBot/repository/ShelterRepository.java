package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter,Long> {
    Shelter findByName(String name);

    Optional<Shelter> findById(Long id);

    List<Shelter> findAll();

    void deleteById(Long id);
}
