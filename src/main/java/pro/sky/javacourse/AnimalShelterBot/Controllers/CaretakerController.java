package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.web.bind.annotation.RestController;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

@RestController("caretaker")
public class CaretakerController {

    private CaretakerService caretakerService;

    public CaretakerController(CaretakerService caretakerService) {
        this.caretakerService = caretakerService;
    }


}
