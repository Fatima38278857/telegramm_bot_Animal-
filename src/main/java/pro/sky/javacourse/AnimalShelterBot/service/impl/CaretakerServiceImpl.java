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


    /**
     * method for create caretaker and add it in a repository
     * @param chatId
     * @param name
     * @param address
     * @param passport
     * @return created caretaker
     */
    @Override
    public Caretaker addCaretaker(Long chatId, String name, String address, String passport) {
        Caretaker caretaker = new Caretaker(chatId, name, address, passport);
        return caretakerRepository.save(caretaker);
    }

    /**
     * save a caretaker in a repository
     * @param caretaker
     * @return
     */
    @Override
    public Caretaker addCaretaker(Caretaker caretaker) {
        return caretakerRepository.save(caretaker);
    }

    /**
     * delete by id
     * @param id
     */
    @Override
    public void delete(Long id) {
        caretakerRepository.deleteById(id);
    }

    /**
     * get a caretakers name by id
     * @param id
     * @return String name
     */
    @Override
    public String getNameById(Long id) {
        String caretakerName = caretakerRepository.findById(id).get().getName();
        return caretakerName;

    }

    /**
     * getting caretaker by id
     * @param id
     * @return Caretaker caretaker
     */
    @Override
    public Optional<Caretaker> getById(Long id) {
        return caretakerRepository.findById(id);
    }

    /**
     * get all caretakers from repository
     * @return List of Caretakers
     */
    @Override
    public List<Caretaker> getAllCaretaker() {
        return caretakerRepository.findAll().stream().toList();
    }

    /**
     * set probation on caretaker
     * @param bol
     * @param name
     */
    @Override
    public void setProbation(boolean bol, String name) {
        caretakerRepository.findByName(name).setOnProbation(bol);
    }


}
