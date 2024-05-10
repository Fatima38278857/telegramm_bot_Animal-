package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.VolunteerRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.List;

@Service
public class ShelterServiceImpl implements ShelterService {
    private final ShelterRepository shelterRepository;
    private final VolunteerRepository volunteerRepository;
    private final Logger logger = LoggerFactory.getLogger(ShelterRepository.class);

    public ShelterServiceImpl(ShelterRepository shelterRepository, VolunteerRepository volunteerRepository) {
        this.shelterRepository = shelterRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public List<Shelter> getAll() {
        logger.info("Was invoked method ShelterService.getAll()");
        return shelterRepository.findAll();
    }

    @Override
    @Transactional
    public Shelter findShelterByName(String name) {
        logger.info("Was invoked method ShelterService.findShelterByName()({})", name);
        return shelterRepository.findByName(name);
    }

    @Override
    public Shelter find(Long id) {
        logger.info("Was invoked method ShelterService.find({})", id);
        return shelterRepository.findById(id).orElse(null);
    }

    @Override
    public Shelter add(Shelter shelter) {
        logger.info("Was invoked method ShelterService.add({})", shelter);
        Shelter shelterToAdd = new Shelter();
        shelterToAdd.setName(shelter.getName());
        shelterToAdd.setAddress(shelter.getAddress());
        shelterToAdd.setRegime(shelter.getRegime());
        shelterToAdd.setHowTo((shelter.getHowTo()));
        return shelterRepository.save(shelterToAdd);
    }

    @Override
    public Shelter edit(Long id, Shelter shelter) {
        logger.info("Was invoked method ShelterService.edit({}, {})", id, shelter);
        return shelterRepository.findById(id)
                .map(found -> {
                    found.setName(shelter.getName());
                    found.setAddress(shelter.getAddress());
                    found.setRegime(shelter.getRegime());
                    found.setHowTo((shelter.getHowTo()));
                    found.setMainVolunteer((shelter.getMainVolunteer()));
                    return shelterRepository.save(found);
                }).orElse(null);
    }

    @Override
    public void uploadLocationMap(Long shelterId, MultipartFile locationMapFile) throws IOException {
        logger.info("Was invoked method ShelterService.uploadAvatar({}, avatarFile)", shelterId);
        Shelter shelter = find(shelterId);
        String fileName = "shelter" + shelterId + "." + getExtensions(locationMapFile.getOriginalFilename());
        shelter.setLocationMapFileName(fileName);
        shelter.setLocationMapFileSize(locationMapFile.getSize());
        shelter.setLocationMapMediaType(locationMapFile.getContentType());

        try (
                InputStream is = locationMapFile.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            ImageIO.write(image, getExtensions(fileName), baos);
            shelter.setLocationMap(baos.toByteArray());
            shelterRepository.save(shelter);
        }
    }

    @Override
    public void setMainVolunteer(Long shelterId, Long mainVolunteerId) {
        Volunteer volunteer = volunteerRepository.findById(mainVolunteerId).orElse(null);
        Shelter shelter = shelterRepository.findById(shelterId).orElse(null);
        if (volunteer == null || shelter == null) {
            logger.error(volunteer == null ? "volunteer" : "shelter" + " not found");
        } else {
            shelter.setMainVolunteer(volunteer);
            shelterRepository.save(shelter);
        }
    }

    @Override
    public Volunteer getMainVolunteer(Long shelterId) {
        Shelter shelter = find(shelterId);
        if (shelter == null) {
            return null;
        }
        return shelter.getMainVolunteer();
    }

    @Override
    public Collection<Volunteer> getVolunteers(Long shelterId) {
        Shelter shelter = find(shelterId);
        if (shelter == null) {
            return null;
        }
        return shelter.getVolunteerSet();
    }

    private String getExtensions(String fileName) {
        logger.info("Was invoked method AvatarService.getExtensions({})", fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public void delete(Long id) {
        logger.info("Was invoked method ShelterService.delete({})", id);
        logger.debug("Method delete(id) executes findById(id)");
        shelterRepository.deleteById(id);
        logger.info("Was invoked method ShelterService.delete({})", id);
    }
}
