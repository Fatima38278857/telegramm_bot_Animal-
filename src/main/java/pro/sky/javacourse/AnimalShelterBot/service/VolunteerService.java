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

    void assignPetToAdopter(long petId, long shelterId);

    // Метод для назначения испытательного срока
    void setTrialPeriod(long shelterId, LocalDate startDate, LocalDate endDate);

    // Метод для проверки отчета усыновителя
    boolean checkAdopterReport(long shelterId);

    // Метод для добавления замечаний усыновителю
    void makeCommentsToAdopter(long shelterId, String comments);

    // Метод для продления испытательного срока
    void extendTrialPeriod(long shelterId, LocalDate newEndDate);

    // Метод для забирания или отдачи животного у/от усыновителя
    void takeOrGivePet(long petId, boolean takeBack);
}