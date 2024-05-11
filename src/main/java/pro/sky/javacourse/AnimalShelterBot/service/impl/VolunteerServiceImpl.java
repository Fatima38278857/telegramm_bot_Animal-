package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;
import pro.sky.javacourse.AnimalShelterBot.repository.PetRepository;
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
    private final PetRepository petRepository;
    private final CaretakerRepository caretakerRepository;

    public VolunteerServiceImpl(VolunteerRepository volunteerRepository,
                                CaretakerRepository caretakerRepository,
                                PetRepository petRepository) {
        this.volunteerRepository = volunteerRepository;
        this.petRepository = petRepository;
        this.caretakerRepository = caretakerRepository;
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
    public void assignPetToCaretaker(Long volunteerId, Long petId, Long caretakerId) {
        Optional<Volunteer> volunteerOptional = volunteerRepository.findById(volunteerId);
        Optional<Pet> petOptional = petRepository.findById(petId);
        Optional<Caretaker> caretakerOptional = caretakerRepository.findById(caretakerId);

        if (volunteerOptional.isPresent() && petOptional.isPresent() && caretakerOptional.isPresent()) {
            Volunteer volunteer = volunteerOptional.get();
            Pet pet = petOptional.get();
            Caretaker caretaker = caretakerOptional.get();

            // Назначаем усыновителя
            pet.setCaretaker(caretaker);

            // Обновляем запись о животном в базе данных
            petRepository.save(pet);
        } else {
            throw new IllegalArgumentException("Invalid volunteer, pet, or caretaker ID.");
        }
    }

    @Override
    public void setTrialPeriodEnd(Long caretakerId, LocalDate newEndDate) {
        Optional<Caretaker> caretakerOptional = caretakerRepository.findById(caretakerId);

        if (caretakerOptional.isPresent()) {
            Caretaker caretaker = caretakerOptional.get();
            caretaker.setTrialPeriodEnd(newEndDate);

            // Обновляем запись об усыновителе в базе данных
            caretakerRepository.save(caretaker);
        } else {
            throw new IllegalArgumentException("Invalid caretaker ID.");
        }
    }
}
