package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;

import java.awt.*;
import java.util.List;

public class ShelterServiceImpl implements ShelterService {
    private Shelter shelterModal;
    private ShelterRepository shelterRepository;
    public ShelterServiceImpl(Shelter shelterModal, ShelterRepository shelterRepository) {
        this.shelterModal = shelterModal;
        this.shelterRepository = shelterRepository;
    }
    @Override
    public Shelter findingShelterByName(String name) {
        return shelterRepository.findByName(name);
    }

    @Override
    public Shelter addShelter(String name, String address, String info, String regime, List<Image> locationMaps) {
        Shelter shelter = new Shelter(name, info, regime, address, locationMaps);
        return (Shelter) shelterRepository.save(shelter);
    }

    @Override
    public Shelter addShelter(Shelter shelter) {
        return (Shelter) shelterRepository.save(shelter);
    }

    @Override
    public List<Volunteer> addVolunteer(String name, String address) {
        //Volunteer volunteer = new Volunteer(name, address);
        //shelterModal.getVolunteers().add(volunteer);
        return null;
    }

    @Override
    public String getInfoAboutShelter(String name) {
        return shelterRepository.findByName(name).getInfo();
    }


}
