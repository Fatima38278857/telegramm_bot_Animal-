package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.modal.Shelter;

import java.util.List;
@Service
public interface ShelterService {
    Shelter findingShelterByName(String name);
}
