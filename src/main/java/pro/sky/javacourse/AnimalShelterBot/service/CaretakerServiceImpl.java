package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.repository.CaretakerRepository;

import java.util.Collection;

@Service
public class CaretakerServiceImpl implements CaretakerService {
    private final CaretakerRepository caretakerRepository;
    private final Logger logger = LoggerFactory.getLogger(CaretakerServiceImpl.class);

    public CaretakerServiceImpl(CaretakerRepository caretakerRepository) {
        this.caretakerRepository = caretakerRepository;
    }

    @Override
    public Caretaker add(Caretaker caretaker) {
        logger.info("Was invoked method CaretakerService.add({})", caretaker);
        Caretaker caretakerToAdd = new Caretaker();
        caretakerToAdd.setName(caretaker.getName());
        caretakerToAdd.setAddress(caretaker.getAddress());
        caretakerToAdd.setPassport(caretaker.getPassport());
        caretakerToAdd.setPhoneNumber(caretaker.getPhoneNumber());
        caretakerToAdd.setChatId(caretaker.getChatId());
        return caretakerRepository.save(caretakerToAdd);
    }

    @Override
    public void delete(Caretaker caretaker) {
        logger.info("Was invoked method CaretakerService.delete({})", caretaker);
        Caretaker caretakerFromDb = caretakerRepository.findById(caretaker.getId()).orElse(null);
        if (caretakerFromDb == null) {
            return;
        }
        caretakerRepository.deleteById(caretaker.getId());
    }

    @Override
    public Collection<Caretaker> getAll() {
        logger.info("Was invoked method CaretakerService.getAll()");
        return caretakerRepository.findAll();
    }

    @Override
    public Caretaker find(Long id) {
        logger.info("Was invoked method CaretakerService.find({})", id);
        return caretakerRepository.findById(id).orElse(null);
    }

    @Override
    public Caretaker findByChatId(Long chatId) {
        logger.info("Was invoked method CaretakerService.findByChatId({})", chatId);
        return caretakerRepository.findByChatId(chatId);
    }

    @Override
    public Caretaker edit(Caretaker caretaker) {
        logger.info("Was invoked method CaretakerService.edit({})", caretaker);
        return caretakerRepository.findById(caretaker.getId())
                .map(found -> {
                    found.setName(caretaker.getName());
                    found.setAddress(caretaker.getAddress());
                    found.setPassport(caretaker.getPassport());
                    found.setPhoneNumber(caretaker.getPhoneNumber());
                    found.setChatId(caretaker.getChatId());
                    return caretakerRepository.save(found);
                }).orElse(null);
    }
}
