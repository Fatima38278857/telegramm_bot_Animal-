package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.web.bind.annotation.RestController;
import pro.sky.javacourse.AnimalShelterBot.service.PetService;

@RestController("pet")
public class PetController {
    private PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }
}
