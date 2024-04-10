package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.modal.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

public class CaretakerServiceImpl implements CaretakerService {
    private CaretakerRepository caretakerRepository;

    public CaretakerServiceImpl(CaretakerRepository caretakerRepository) {
        this.caretakerRepository = caretakerRepository;
    }



}
