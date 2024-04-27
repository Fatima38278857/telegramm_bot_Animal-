package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

import java.util.List;

@RestController
@RequestMapping("caretaker")
public class CaretakerController {

    @Autowired
    private final CaretakerService caretakerService;

    public CaretakerController(CaretakerService caretakerService) {
        this.caretakerService = caretakerService;
    }

    @GetMapping("delete_{id}")
    public void deleteCaretaker(@PathVariable Long id) {
        caretakerService.delete(id);
    }

    @GetMapping("get_{id}")
    public void getCaretaker(@PathVariable Long id) {
        caretakerService.getById(id);
    }

    @GetMapping("get_all")
    public List<Caretaker> getAll() {
        return caretakerService.getAllCaretaker();
    }

    @PostMapping
    public Caretaker addShelter(@RequestBody Caretaker caretaker) {
        return caretakerService.addCaretaker(caretaker);
    }



}
