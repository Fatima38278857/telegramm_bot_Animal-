package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.*;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.PetRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PetServiceImpl implements PetService {
    private final PetRepository petRepository;
    private final CaretakerRepository caretakerRepository;
    private final Logger logger = LoggerFactory.getLogger(PetServiceImpl.class);

    public PetServiceImpl(PetRepository petRepository, CaretakerRepository caretakerRepository) {
        this.petRepository = petRepository;
        this.caretakerRepository = caretakerRepository;
    }

    @Override
    public Pet add(Pet pet) {
        return petRepository.save(pet);
    }

    @Override
    public void uploadAvatar(Long petId, MultipartFile avatarFile) throws IOException {
        logger.info("Was invoked method PetService.uploadAvatar({}, avatarFile)", petId);
        Pet pet = find(petId);
        String fileName = "pet" + petId + "." + getExtensions(avatarFile.getOriginalFilename());
        pet.setAvatarFileName(fileName);
        pet.setAvatarFileSize(avatarFile.getSize());
        pet.setAvatarMediaType(avatarFile.getContentType());
        try (
                InputStream is = avatarFile.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            ImageIO.write(image, getExtensions(fileName), baos);
            pet.setAvatar(baos.toByteArray());
            petRepository.save(pet);
        }
    }

    private String getExtensions(String fileName) {
        logger.info("Was invoked method AvatarService.getExtensions({})", fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public Pet find(Long id) {
        logger.info("Was invoked method PetService.find({})", id);
        return petRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Pet find(String name) {
        logger.info("Was invoked method PetService.find({})", name);
        return petRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional
    public Collection<Pet> getAll() {
        logger.info("Was invoked method PetService.getAll()");
        return petRepository.findAll();
    }

    @Override
    @Transactional
    public Collection<Pet> findByStatus(PetStatus status) {
        logger.info("Was invoked method PetService.findByStatus({})", status);
        return petRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public List<Pet> findAvailableByShelterId(Long shelterId) {
        logger.info("Was invoked method PetService.findAvailableByShelterId({})", shelterId);
        return petRepository.findAvailableByShelterId(shelterId);
    }

    @Override
    @Transactional
    public Collection<Pet> findByCaretakerId(Long caretakerId) {
        return petRepository.findByCaretakerId(caretakerId);
    }
    @Override
    @Transactional
    public Collection<Pet> findByCaretakerIdAndShelterId(Long caretakerId, Long shelterId) {
        return petRepository.findByCaretakerIdAndShelterId(caretakerId, shelterId);
    }

    @Override
    public Pet edit(Pet pet) {
        logger.info("Was invoked method PetService.edit({})", pet);
        return petRepository.findById(pet.getId())
                .map(found -> {
                    found.setName(pet.getName());
                    found.setAge(pet.getAge());
                    found.setType(pet.getType());
                    found.setAbilities(pet.getAbilities());
                    found.setRestrictions(pet.getRestrictions());
                    found.setConditions(pet.getConditions());
                    found.setStatus(pet.getStatus());
                    return petRepository.save(found);
                }).orElse(null);
    }

    @Override
    @Transactional
    public Pet startTrial(Pet pet, Long caretakerId) {
        logger.info("Was invoked method PetService.startTrial({})", pet);
        Caretaker caretakerFound = caretakerRepository.findById(caretakerId).orElse(null);
        Pet found = petRepository.findById(pet.getId()).orElse(null);
        if (caretakerFound == null || found == null) return null;
        found.setStatus(PetStatus.ОПЕКА);
        found.setTrialStart(LocalDateTime.now());
        found.setTrialEnd(LocalDateTime.now().plusDays(30));
        found.setCaretaker(caretakerFound);
        petRepository.save(found);
        caretakerFound.getPets().add(pet);
        caretakerRepository.save(caretakerFound);
        return found;
    }

    @Override
    public Pet trialAdd15(Pet pet) {
        logger.info("Was invoked method PetService.trialAdd15({})", pet);
        return trialAdd(pet, 15);
    }

    @Override
    public Pet trialAdd30(Pet pet) {
        logger.info("Was invoked method PetService.trialAdd30({})", pet);
        return trialAdd(pet, 30);
    }

    @Override
    public Pet adopt(Pet pet) {
        logger.info("Was invoked method PetService.adopt({})", pet);
        return petRepository.findById(pet.getId())
                .map(found -> {
                    if (pet.getStatus() != PetStatus.ОПЕКА
                            && LocalDateTime.now().isBefore(LocalDateTime.now().minusDays(1))) return null;
                    found.setStatus(PetStatus.УСЫНОВЛЕН);
                    return petRepository.save(found);
                }).orElse(null);
    }

    @Override
    public Pet returned(Pet pet) {
        logger.info("Was invoked method PetService.returned({})", pet);
        return petRepository.findById(pet.getId())
                .map(found -> {
                    found.setStatus(PetStatus.ОФОРМЛЯЕТСЯ);
                    Long caretakerId = found.getCaretaker().getId();
                    Caretaker caretaker = caretakerRepository.findById(caretakerId).orElse(null);
                    logger.info("Method PetService.returned({}) tries to remove pet from caretaker pet set", pet);
                    try {
                        caretaker.getPets().remove(found);
                        caretakerRepository.save(caretaker);
                    } catch (NullPointerException e) {
                        logger.info("NullPointerException occurred while removing ({}) from ({}) Set pets", pet, caretaker);
                        return null;
                    }
                    found.setCaretaker(null);
                    return petRepository.save(found);
                }).orElse(null);
    }

    @Override
    public Pet suspended(Pet pet) {
        logger.info("Was invoked method PetService.suspended({})", pet);
        return petRepository.findById(pet.getId())
                .map(found -> {
                    if (pet.getStatus() != PetStatus.ОПЕКА || pet.getStatus() != PetStatus.ВОЗВРАТ) return null;
                    found.setStatus(PetStatus.ВОЗВРАТ);
                    return petRepository.save(found);
                }).orElse(null);
    }

    @Override
    public Pet available(Pet pet) {
        logger.info("Was invoked method PetService.available({})", pet);
        return petRepository.findById(pet.getId())
                .map(found -> {
                    if (pet.getStatus() == PetStatus.ОПЕКА
                        || pet.getStatus() == PetStatus.ВОЗВРАТ
                        || pet.getStatus() == PetStatus.УМЕР
                        || pet.getStatus() == PetStatus.УСЫНОВЛЕН) return null;
                    found.setStatus(PetStatus.ДОСТУПЕН);
                    return petRepository.save(found);
                }).orElse(null);
    }

    @Override
    public Pet ill(Pet pet) {
        logger.info("Was invoked method PetService.ill({})", pet);
        return petRepository.findById(pet.getId())
                .map(found -> {
                    if (pet.getStatus() == PetStatus.ОПЕКА
                            || pet.getStatus() == PetStatus.ВОЗВРАТ
                            || pet.getStatus() == PetStatus.УМЕР
                            || pet.getStatus() == PetStatus.УСЫНОВЛЕН) return null;
                    found.setStatus(PetStatus.БОЛЕН);
                    return petRepository.save(found);
                }).orElse(null);
    }

    @Override
    public void delete(Pet pet) {
        logger.info("Was invoked method PetService.delete({})", pet);
        Pet petFromDb = petRepository.findById(pet.getId()).orElse(null);
        if (petFromDb == null) {
            return;
        }
        petRepository.deleteById(pet.getId());
    }

    @Override
    public List<PetType> getAvailablePetTypes(Long shelterId) {
        logger.info("Was invoked method PetService.getAvailablePetTypes({})", shelterId);
        List<PetType> petTypes = petRepository.getAvailablePetsTypesByShelterId(shelterId);
        petTypes.sort(Comparator.comparingInt(Enum::ordinal));
        return Collections.unmodifiableList(petTypes);
    }

    @Override
    public Long findChatIdByPetId(Long petId) {
        return petRepository.findChatIdById(petId);
    }

    public Pet trialAdd(Pet pet, int days) {
        logger.info("Was invoked method PetService.trialAdd({},{})", pet, days);
        return petRepository.findById(pet.getId())
                .map(found -> {
                    if (pet.getStatus() != PetStatus.ОПЕКА) return null;
                    found.setTrialEnd(found.getTrialEnd().plusDays(days));
                    petRepository.save(found);
                    return petRepository.save(found);
                }).orElse(null);
    }

}
