package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
@Repository
public interface CaretakerRepository extends JpaRepository<Caretaker,Long> {
    Caretaker findByChatId(Long chatId);
}
