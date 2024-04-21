package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;

import java.awt.*;
import java.util.List;

@Service
public interface ShelterService {
    Shelter findingShelterByName(String name);

    Shelter addShelter(String name, String address, String info, String regime, List<Image> locationMaps);

    Shelter addShelter(Shelter shelter);

    List<Volunteer> addVolunteer(String name, String address);

    String getInfoAboutShelter(String name);
}
