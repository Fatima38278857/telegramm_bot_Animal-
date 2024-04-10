package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.modal.Shelter;
import pro.sky.javacourse.AnimalShelterBot.repository.ShelterRepository;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;

public class ShelterServiceImpl implements ShelterService {
    private ShelterRepository shelterRepository;
    public ShelterServiceImpl(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }
    @Override
    public Shelter findingShelterByName(String name) {
        return shelterRepository.findByName(name);
    }
}
