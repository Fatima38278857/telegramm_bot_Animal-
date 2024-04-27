package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;

import java.awt.*;
import java.util.List;

public interface ShelterService {
    Shelter findingShelterByName(String name);

    Shelter addShelter(String name, String address, String info, String regime, List<Image> locationMaps);

    Shelter addShelter(Shelter shelter);

    void delete(Long id);

    Shelter update(Long id);

    List<Volunteer> addVolunteer(String name, String address);

    String getInfoAboutShelter(String name);

    Shelter findingShelterById(Long id);

    List<Shelter> getAllShelters();
}
