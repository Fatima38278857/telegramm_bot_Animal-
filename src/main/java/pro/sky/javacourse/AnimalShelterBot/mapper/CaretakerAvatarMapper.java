package pro.sky.javacourse.AnimalShelterBot.mapper;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.dto.CaretakerAvatarDto;
import pro.sky.javacourse.AnimalShelterBot.model.CaretakerAvatar;
@Service
public class CaretakerAvatarMapper {
    public CaretakerAvatarDto mapToDTO(CaretakerAvatar avatar) {
        CaretakerAvatarDto avatarDTO = new CaretakerAvatarDto();
        avatarDTO.setId(avatar.getId());
        avatarDTO.setFileSize(avatar.getFileSize());
        avatarDTO.setMediaType(avatar.getMediaType());
        avatarDTO.setCaretakerID(avatar.getCaretaker().getId());

        return avatarDTO;
    }
}
