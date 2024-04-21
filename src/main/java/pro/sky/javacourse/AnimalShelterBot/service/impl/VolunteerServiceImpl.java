package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.VolunteerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Override
    public Volunteer addVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    @Override
    public Volunteer updateVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    @Override
    public void deleteVolunteer(Long id) {
        volunteerRepository.deleteById(id);
    }

    @Override
    public Volunteer getVolunteerById(Long id) {
        return volunteerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    @Override
    public void addInitialVolunteers() {
        addVolunteer(new Volunteer("Иван Петров", "+795322115"));
        addVolunteer(new Volunteer("Марина Сидорова", "+79552222"));
    }

    @Override
    public void assignPetToVolunteer(Long volunteerId, Long petId) {
        // Логика назначения питомца волонтеру
    }

    @Override
    public void extendTrialPeriod(Long volunteerId, int days) {
        // Логика продления испытательного срока
    }

    @Override
    public void checkAdoptionReport(Long volunteerId) {
        // Логика проверки отчета усыновителя
    }

    @Override
    public void makeRemarksToAdopter(Long volunteerId, String remarks) {
        // Логика оставления замечаний усыновителю
    }

    @Override
    public void takeBackPetFromVolunteer(Long volunteerId) {
        // Логика возврата питомца обратно в приют
    }

    @Override
    public Volunteer findVolunteerById(Long id) {
        return volunteerRepository.findById(id).orElse(null);
    }

    @Override
    public Volunteer saveVolunteer(Volunteer existingVolunteer) {
        return volunteerRepository.save(existingVolunteer);
    }
}