package pro.sky.javacourse.AnimalShelterBot.service;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import java.util.List;
import java.util.Optional;

public interface VolunteerService {

    Volunteer addVolunteer(Volunteer volunteer);

    void deleteVolunteer(Long id);

    List<Volunteer> getAllVolunteers();

    Optional<Volunteer> getVolunteerById(Long id);

    void changeWorkingStatus(long volunteerId, boolean working);

}