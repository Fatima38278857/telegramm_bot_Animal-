package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;

import java.awt.*;
import java.util.List;
import java.util.Set;

public interface ShelterService {
    Shelter findingShelterByName(String name);

    Shelter addShelter(String name, String address, String info, String regime);

    Shelter addShelter(Shelter shelter);

    void delete(Long id);

    Shelter update(Long id);

    Volunteer addVolunteer(Long shelterId ,Volunteer volunteer);

    String getInfoAboutShelter(String name);

    Shelter findingShelterById(Long id);

    List<Shelter> getAllShelters();

    Set<Volunteer> getSetOfVolunteers(Long id);
}
