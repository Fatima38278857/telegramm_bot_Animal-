package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.VolunteerRepository;

import java.util.Collection;
import java.util.List;

@Service
public class VolunteerServiceImpl implements VolunteerService{
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final Logger logger = LoggerFactory.getLogger(VolunteerRepository.class);

    public VolunteerServiceImpl(VolunteerRepository volunteerRepository, ShelterRepository shelterRepository) {
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = shelterRepository;
    }

    @Override
    public List<Volunteer> getAll() {
        logger.info("Was invoked method VolunteerService.getAll()");
        return volunteerRepository.findAll();
    }

    @Override
    public Volunteer findByChatId(Long chatId) {
        logger.info("Was invoked method VolunteerService.getVolunteerByChatId()({})", chatId);
        return volunteerRepository.findByChatId(chatId);
    }

    @Override
    public Collection<Shelter> getShelters(Long id) {
        logger.info("Was invoked method VolunteerService.getShelters({})", id);
        Volunteer volunteer = find(id);
        return volunteer.getShelters();
    }

    @Override
    public Volunteer add(Volunteer volunteer) {
        logger.info("Was invoked method VolunteerService.add({})", volunteer);
        return volunteerRepository.save(volunteer);
    }

    @Override
    public Volunteer find(Long id) {
        logger.info("Was invoked method VolunteerService.find({})", id);
        return volunteerRepository.findById(id).orElse(null);
    }

    @Override
    public Volunteer edit(Long id, Volunteer volunteer) {
        logger.info("Was invoked method VolunteerService.edit({}, {})", id, volunteer);

        return volunteerRepository.findById(id)
                .map(found -> {
                    found.setName(volunteer.getName());
                    found.setAddress(volunteer.getAddress());
                    found.setPassport(volunteer.getPassport());
                    found.setPhoneNumber(volunteer.getPhoneNumber());
                    found.setChatId(volunteer.getChatId());
                    return volunteerRepository.save(found);
                }).orElse(null);
    }

    @Override
    @Transactional
    public void addToShelter(Long id, Long shelterId) {
        logger.info("Was invoked method VolunteerService.addToShelter(id{}, shelterId{})", id, shelterId);
        Volunteer volunteer = find(id);
        Shelter shelter = shelterRepository.findById(shelterId).orElse(null);
        if (volunteer == null || shelter == null) {
            logger.error(volunteer == null ? "volunteer" : "shelter" + " not found");
        } else{
            volunteer.getShelters().add(shelter);
            shelter.getVolunteerSet().add(volunteer);
            volunteerRepository.save(volunteer);
            shelterRepository.save(shelter);
        }
    }

    @Override
    public void removeFromShelter(Long id, Long shelterId) {
        logger.info("Was invoked method VolunteerService.removeFromShelter(id{}, shelterId{})", id, shelterId);
        Volunteer volunteer = find(id);
        Shelter shelter = shelterRepository.findById(shelterId).orElse(null);
        if (volunteer == null || shelter == null) {
            logger.error(volunteer == null ? "volunteer" : "shelter" + " not found");
        } else{
            volunteer.getShelters().remove(shelter);
            shelter.getVolunteerSet().remove(volunteer);
            volunteerRepository.save(volunteer);
            shelterRepository.save(shelter);
        }
    }

    @Override
    public void delete(Volunteer volunteer) {
        logger.info("Was invoked method VolunteerService.delete({})", volunteer);
        Volunteer volunteerFromDb = volunteerRepository.findById(volunteer.getId()).orElse(null);
        if (volunteerFromDb == null) {
            return;
        }
        volunteerRepository.deleteById(volunteer.getId());
    }

}
