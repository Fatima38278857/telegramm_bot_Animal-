package pro.sky.javacourse.AnimalShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.javacourse.AnimalShelterBot.model.CaretakerAvatar;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<CaretakerAvatar,Long> {

    Optional<CaretakerAvatar> findByCaretakerId(Long caretakerId);
}
