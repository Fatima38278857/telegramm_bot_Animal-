package pro.sky.javacourse.AnimalShelterBot.service.impl;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.modal.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

@Service
public class CaretakerServiceImpl implements CaretakerService {
    private CaretakerRepository caretakerRepository;

    public CaretakerServiceImpl(CaretakerRepository caretakerRepository) {
        this.caretakerRepository = caretakerRepository;
    }


    @Override
    public Caretaker addCaretaker(Long chatId,String name, String address) {
        Caretaker caretaker = new Caretaker(chatId,name, address);
        return caretakerRepository.save(caretaker);
    }

    @Override
    public void getNameById(Long id) {
        caretakerRepository.findById(id);
    }

}
