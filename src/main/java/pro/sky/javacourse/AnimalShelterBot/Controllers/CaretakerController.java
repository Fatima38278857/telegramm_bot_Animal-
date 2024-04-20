package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.javacourse.AnimalShelterBot.modal.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

@RestController("caretaker")
public class CaretakerController {

    @Autowired
    private final CaretakerService caretakerService;

    public CaretakerController(CaretakerService caretakerService) {
        this.caretakerService = caretakerService;
    }

    @GetMapping("get_name")
    public boolean getNameOfCaretaker(Long id) {
        return true;
    }

}
