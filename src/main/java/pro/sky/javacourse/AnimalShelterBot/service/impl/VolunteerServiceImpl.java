package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.PetRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.VolunteerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;

    // Инъекция зависимостей через конструктор
    public VolunteerServiceImpl(VolunteerRepository volunteerRepository,
                                ShelterRepository adopterRepository,
                                PetRepository petRepository) {
        this.volunteerRepository = volunteerRepository;
        this.shelterRepository = adopterRepository;
        this.petRepository = petRepository;
    }

    @Override
    public Volunteer addVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    @Override
    public void deleteVolunteer(Long id) {
        volunteerRepository.deleteById(id);
    }

    @Override
    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    @Override
    public Optional<Volunteer> getVolunteerById(Long id) {
        return volunteerRepository.findById(id);
    }

    @Override
    public void changeWorkingStatus(long volunteerId, boolean working) {
        Optional<Volunteer> optionalVolunteer = volunteerRepository.findById(volunteerId);
        if (optionalVolunteer.isPresent()) {
            Volunteer volunteer = optionalVolunteer.get();
            volunteer.setWorking(working);
            volunteerRepository.save(volunteer);
        } else {
            throw new IllegalArgumentException("Volunteer with id " + volunteerId + " not found");
        }
    }

    @Override
    public void assignPetToAdopter(long petId, long shelterId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));
        Shelter shelter = shelterRepository.findById(shelterId).orElseThrow(() -> new RuntimeException("Adopter not found"));
        pet.setShelter(shelter);
        petRepository.save(pet);
    }

    // Метод для назначения испытательного срока
    @Override
    public void setTrialPeriod(long shelterId, LocalDate startDate, LocalDate endDate) {
        Shelter shelter = shelterRepository.findById(shelterId).orElseThrow(() -> new RuntimeException("Adopter not found"));
        shelter.setTrialPeriodStart(startDate);
        shelter.setTrialPeriodEnd(endDate);
        shelterRepository.save(shelter);
    }

    // Метод для проверки отчета усыновителя
    @Override
    public boolean checkAdopterReport(long shelterId) {
        Shelter shelter = shelterRepository.findById(shelterId).orElseThrow(() -> new RuntimeException("Adopter not found"));
        return shelter.isReportApproved();
    }

    // Метод для добавления замечаний усыновителю
    @Override
    public void makeCommentsToAdopter(long shelterId, String comments) {
        Shelter shelter = shelterRepository.findById(shelterId).orElseThrow(() -> new RuntimeException("Adopter not found"));

        shelter.setComments(comments);

        shelterRepository.save(shelter);
    }

    // Метод для продления испытательного срока
    @Override
    public void extendTrialPeriod(long shelterId, LocalDate newEndDate) {
        Shelter shelter = shelterRepository.findById(shelterId).orElseThrow(() -> new RuntimeException("Adopter not found"));

        shelter.setTrialPeriodEnd(newEndDate);

        shelterRepository.save(shelter);
    }

    // Метод для забирания или отдачи животного у/от усыновителя
    @Override
    public void takeOrGivePet(long petId, boolean takeBack) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));

        if (takeBack) {
            pet.setShelter(null);
        } else {

            Shelter newShelter = new Shelter();
            pet.setShelter(newShelter);
        }

        petRepository.save(pet);
    }
}
