package pro.sky.javacourse.AnimalShelterBot.service;
import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;


import java.util.List;

@Service
public interface VolunteerService {


    Volunteer addVolunteer(Volunteer volunteer);

    Volunteer updateVolunteer(Volunteer volunteer);

    void deleteVolunteer(Long id);

    Volunteer getVolunteerById(Long id);

    List<Volunteer> getAllVolunteers();

    void addInitialVolunteers();

    void assignPetToVolunteer(Long volunteerId, Long petId);

    void extendTrialPeriod(Long volunteerId, int days);

    void checkAdoptionReport(Long volunteerId);

    void makeRemarksToAdopter(Long volunteerId, String remarks);

    void takeBackPetFromVolunteer(Long volunteerId);

    Volunteer findVolunteerById(Long id);

    Volunteer saveVolunteer(Volunteer existingVolunteer);
}