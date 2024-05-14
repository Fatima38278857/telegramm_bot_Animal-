package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface ShelterService {
    public List<Shelter> getAll();

    Shelter findShelterByName(String name);

    Shelter find(Long id);

    Shelter add(Shelter shelter);

    Shelter edit(Long id, Shelter shelter);

    void delete(Shelter shelter);

    void uploadLocationMap(Long shelterId, MultipartFile locationMapFile) throws IOException;

    void setMainVolunteer(Long shelterId, Long mainVolunteerId);

    Volunteer getMainVolunteer(Long shelterId);

    Collection<Volunteer> getVolunteers(Long shelterId);

}
