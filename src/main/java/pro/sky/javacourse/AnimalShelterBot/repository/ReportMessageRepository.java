package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.javacourse.AnimalShelterBot.model.ReportMessage;

@Repository
public interface ReportMessageRepository extends JpaRepository<ReportMessage, Long> {
}
