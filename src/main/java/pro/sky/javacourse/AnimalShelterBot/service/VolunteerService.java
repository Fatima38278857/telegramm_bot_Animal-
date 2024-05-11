package pro.sky.javacourse.AnimalShelterBot.service;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VolunteerService {

    Volunteer addVolunteer(Volunteer volunteer);

    void deleteVolunteer(Long id);

    List<Volunteer> getAllVolunteers();

    Optional<Volunteer> getVolunteerById(Long id);

    void changeWorkingStatus(long volunteerId, boolean working);

    void assignPetToCaretaker(Long volunteerId, Long petId, Long caretakerId);

    void setTrialPeriodEnd(Long caretakerId, LocalDate newEndDate);

    void checkCaretakerReport(Long caretakerId, String report);

    void makeComments(Long caretakerId, String comments);

    void extendTrialPeriod(Long caretakerId, LocalDate newEndDate);

    void transferPetToCaretaker(Long volunteerId, Long petId, Long caretakerId);
}