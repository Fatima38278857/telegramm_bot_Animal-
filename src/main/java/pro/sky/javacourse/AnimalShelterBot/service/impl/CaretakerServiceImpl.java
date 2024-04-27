package pro.sky.javacourse.AnimalShelterBot.service.impl;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

import java.util.List;
import java.util.Optional;

@Service
public class CaretakerServiceImpl implements CaretakerService {
    private CaretakerRepository caretakerRepository;

    public CaretakerServiceImpl(CaretakerRepository caretakerRepository) {
        this.caretakerRepository = caretakerRepository;
    }


    @Override
    public Caretaker addCaretaker(Long chatId, String name, String address) {
        Caretaker caretaker = new Caretaker(chatId, name, address);
        return caretakerRepository.save(caretaker);
    }

    @Override
    public Caretaker addCaretaker(Caretaker caretaker) {
        return caretakerRepository.save(caretaker);
    }

    @Override
    public void delete(Long id) {
        caretakerRepository.deleteById(id);
    }

    @Override
    public String getNameById(Long id) {
        String caretakerName = caretakerRepository.findById(id).get().getName();
        return caretakerName;

    }

    @Override
    public Optional<Caretaker> getById(Long id) {
        return caretakerRepository.findById(id);
    }

    @Override
    public List<Caretaker> getAllCaretaker() {
        return caretakerRepository.findAll().stream().toList();
    }

    @Override
    public void setProbation(boolean bol, String name) {
        caretakerRepository.findByName(name).setOnProbation(bol);
    }


}
