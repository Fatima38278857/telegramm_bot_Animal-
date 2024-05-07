package pro.sky.javacourse.AnimalShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.repository.PetRepository;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetServiceImpl.class);

    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }
    private final PetRepository petRepository;

    @Override
    public Pet add(Pet pet) {
        logger.info("add method was invoked");
        return petRepository.save(pet);
    }

    @Override
    public Pet get(Long id) {
        logger.info("get method was invoked");
        return petRepository.findById(id).orElse(null);
    }

    @Override
    public Pet update(Long id, Pet pet) {
        logger.info("update method was invoked");
        return petRepository.findById(id).map(petFromDb -> {
//            petFromDb.setCaretakerId(pet.getCaretakerId());
            petFromDb.setShelter(pet.getShelter());
            return petRepository.save(petFromDb);
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        logger.info("delete method was invoked");
        petRepository.deleteById(id);
    }

    @Override
    public List<Pet> getByType(String type) {
        return null;
    }

/*    @Override
    public Shelter getShelter(Long id) {
        return null;
    }
*/
    @Override
    public int getPetsCount() {
        return 0;
    }
}