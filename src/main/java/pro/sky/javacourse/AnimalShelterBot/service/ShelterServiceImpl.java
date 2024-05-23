package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.VolunteerRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class ShelterServiceImpl implements ShelterService {
    @Value("${locations.dir.path}")
    private String locationMapsDir;

    private final ShelterRepository shelterRepository;
    private final VolunteerRepository volunteerRepository;
    private final Logger logger = LoggerFactory.getLogger(ShelterServiceImpl.class);

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
        return shelterRepository.save(shelter);
    }

    @Override
    public Shelter edit(Shelter shelter) {
        logger.info("Was invoked method ShelterService.edit({})", shelter);
        return shelterRepository.findById(shelter.getId())
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
    public Shelter uploadLocationMap(Long shelterId, MultipartFile locationMapFile) throws IOException {
        logger.info("Was invoked method ShelterService.uploadLocationMap({}, locationMapFile)", shelterId);
        Shelter shelter = find(shelterId);
        if (shelter == null) {
            logger.error("Shelter id({}) not found", shelterId);
            throw new NullPointerException();
        }
        Path locationMapFilePath = Path.of(locationMapsDir, "shelter" + shelterId + "." + getExtensions(locationMapFile.getOriginalFilename()));
        Files.createDirectories(locationMapFilePath.getParent());
        Files.deleteIfExists(locationMapFilePath);
        try (
                InputStream is = locationMapFile.getInputStream();
                OutputStream os = Files.newOutputStream(locationMapFilePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }
        shelter.setLocationMapFilePath(locationMapFilePath.toString());
        shelter.setLocationMapFileSize(locationMapFile.getSize());
        shelter.setLocationMapMediaType(locationMapFile.getContentType());
        return shelterRepository.save(shelter);
    }

    private String getExtensions(String fileName) {
        logger.info("Was invoked method ShelterService.getExtensions({})", fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public Shelter setMainVolunteer(Shelter shelter, Long mainVolunteerId) {
        logger.info("Was invoked method ShelterService.setMainVolunteer({}, {})", shelter, mainVolunteerId);
        Volunteer volunteer = volunteerRepository.findById(mainVolunteerId).orElse(null);
        Shelter shelterFromDb = shelterRepository.findById(shelter.getId()).orElse(null);
        if (volunteer == null || shelterFromDb == null) {
            logger.error(volunteer == null ? "Volunteer not found" : "Shelter not found");
            throw new NullPointerException();
        } else {
            shelterFromDb.setMainVolunteer(volunteer);
            return shelterRepository.save(shelterFromDb);
        }
    }

    @Override
    public Volunteer getMainVolunteer(Long shelterId) {
        logger.info("Was invoked method ShelterService.getMainVolunteer({})", shelterId);
        Shelter shelter = find(shelterId);
        if (shelter == null) {
            return null;
        }
        return shelter.getMainVolunteer();
    }

    @Override
    public Collection<Volunteer> getVolunteers(Long shelterId) {
        logger.info("Was invoked method ShelterService.getVolunteers({})", shelterId);
        Shelter shelter = find(shelterId);
        if (shelter == null) {
            return null;
        }
        return shelter.getVolunteerSet();
    }

    @Override
    public void delete(Shelter shelter) {
        logger.info("Was invoked method ShelterService.delete({})", shelter);
        Shelter shelterFromDb = shelterRepository.findById(shelter.getId()).orElse(null);
        if (shelterFromDb == null) {
            return;
        }
        shelterRepository.deleteById(shelter.getId());
    }
}
