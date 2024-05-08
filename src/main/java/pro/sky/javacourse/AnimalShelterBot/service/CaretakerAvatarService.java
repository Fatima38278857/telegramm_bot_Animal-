package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.CaretakerAvatar;

import java.io.IOException;
import java.util.List;

public interface CaretakerAvatarService {
    CaretakerAvatar findAvatar(long studentId);

    void uploadAvatar(Long id, MultipartFile avatar) throws IOException;

    String getExtension(String fileName);

    List<CaretakerAvatar> getPaginatedAvatar(int pageNumber, int pageSize);
}
