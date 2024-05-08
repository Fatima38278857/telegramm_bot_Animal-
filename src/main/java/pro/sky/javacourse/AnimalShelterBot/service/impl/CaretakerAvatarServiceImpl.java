package pro.sky.javacourse.AnimalShelterBot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.model.CaretakerAvatar;
import pro.sky.javacourse.AnimalShelterBot.repository.AvatarRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerAvatarService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
@Service
public class CaretakerAvatarServiceImpl implements CaretakerAvatarService {

    private static final Logger logger = LoggerFactory.getLogger(CaretakerAvatarServiceImpl.class);

    @Value("${path.to.caretaker.avatars.folder}")
    private String avatarsDir;

    private final AvatarRepository avatarRepository;
    private final CaretakerRepository caretakerRepository;

    public CaretakerAvatarServiceImpl(AvatarRepository avatarRepository, CaretakerRepository caretakerRepository) {
        this.avatarRepository = avatarRepository;
        this.caretakerRepository = caretakerRepository;
    }

    /**
     * find avatar by id
     * @param caretakerId
     * @return
     */
    @Override
    public CaretakerAvatar findAvatar(long caretakerId) {
        logger.info("findAvatar method was invoked");
        return avatarRepository.findByCaretakerId(caretakerId).orElseThrow();
    }
    @Override
    public void uploadAvatar(Long caretakerId, MultipartFile file) throws IOException {
        logger.info("uploadAvatar method was invoked");
        Caretaker caretaker = caretakerRepository.findById(caretakerId).orElseThrow();

        Path filePath = Path.of(avatarsDir, caretakerId + "." + getExtension(Objects.requireNonNull(file.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }  CaretakerAvatar avatar = avatarRepository.findByCaretakerId(caretakerId).orElseGet(CaretakerAvatar::new);
        avatar.setCaretaker(caretaker);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    public String getExtension(String fileName) {
        logger.info("getExtension method was invoked");
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public List<CaretakerAvatar> getPaginatedAvatar(int pageNumber, int pageSize) {
        logger.info("getPaginatedAvatar method was invoked");
        PageRequest pageRequest = PageRequest.of(pageNumber-1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}
