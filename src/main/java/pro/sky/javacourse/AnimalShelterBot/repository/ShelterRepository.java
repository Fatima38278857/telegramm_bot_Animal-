package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter,Long> {
    Shelter findByName(String name);

    Optional<Shelter> findById(Long id);

    List<Shelter> findAll();

    void deleteById(Long id);

    @Query(value = "select volunteers from shelter",nativeQuery = true)
    Set<Volunteer> getVolunteer();
}
