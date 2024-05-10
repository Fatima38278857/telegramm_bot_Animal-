package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.VolunteerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private VolunteerRepository volunteerRepository;

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
}