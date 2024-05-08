package pro.sky.javacourse.AnimalShelterBot.service.impl;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;

import java.awt.*;
import java.util.List;
import java.util.Set;

@Service
public class ShelterServiceImpl implements ShelterService {
    private ShelterRepository shelterRepository;
    public ShelterServiceImpl(ShelterRepository shelterRepository) {

        this.shelterRepository = shelterRepository;
    }

    /**
     * finding shelter by name
     * @param name
     * @return Shelter shelter
     */
    @Override
    public Shelter findingShelterByName(String name) {
        return shelterRepository.findByName(name);
    }

    /**
     * method for creating shelter and saving it in repository
     * @param name
     * @param address
     * @param info
     * @param regime
     * @return Shelter shelter
     */
    @Override
    public Shelter addShelter(String name, String address, String info, String regime) {
        Shelter shelter = new Shelter(name, info, regime,address );
        return (Shelter) shelterRepository.save(shelter);
    }

    /**
     * saving shelter in repository
     * @param shelter
     * @return Shelter savedShelter
     */
    @Override
    public Shelter addShelter(Shelter shelter) {
        return (Shelter) shelterRepository.save(shelter);
    }

    /**
     * delete shelter by id
     * @param id
     */
    @Override
    public void delete(Long id) {
        shelterRepository.deleteById(id);
    }

    @Override
    public Shelter update(Long id) {
        return null;
    }

    /**
     * add volunteer ind set of volunteer
     * @param shelterId
     * @param volunteer
     * @return added volunteer
     */
    @Override
    public Volunteer addVolunteer(Long shelterId, Volunteer volunteer) {
        Set<Volunteer> set = shelterRepository.findById(shelterId).map(Shelter::getVolunteerSet).get();
        set.add(volunteer);
        return volunteer;
    }

    /**
     * get info about shelter
     * @param name
     * @return String information
     */
    @Override
    public String getInfoAboutShelter(String name) {
        return shelterRepository.findByName(name).getInfo();
    }

    /**
     * finding shelter by id
     * @param id
     * @return Shelter shelter
     */
    @Override
    public Shelter findingShelterById(Long id) {
        return shelterRepository.findById(id).orElse(null);
    }

    /**
     * get all shelters from repository
     * @return List of shelters
     */
    @Override
    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    /**
     * get all volunteers whose work in a shelter
     * @param id
     * @return set of volunteers
     */
    @Override
    public Set<Volunteer> getSetOfVolunteers(Long id) {
        return shelterRepository.findById(id).map(Shelter::getVolunteerSet).get();
    }


}
