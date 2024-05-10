package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.javacourse.AnimalShelterBot.model.TelegramContact;
@Repository
public interface TelegramContactRepository extends JpaRepository<TelegramContact, String> {
//    TelegramContact findByPhoneNumber(String phoneNumber);
}
