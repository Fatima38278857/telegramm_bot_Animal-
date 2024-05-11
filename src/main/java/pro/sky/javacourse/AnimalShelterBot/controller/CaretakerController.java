package pro.sky.javacourse.AnimalShelterBot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Caretaker;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("caretakers")
public class CaretakerController {
    private final CaretakerService caretakerService;

    public CaretakerController(CaretakerService caretakerService) {
        this.caretakerService = caretakerService;
    }

    //    CREATE
    @PostMapping
    public Caretaker add(@RequestBody Caretaker caretaker) {
        return caretakerService.add(caretaker);
    }

    //    READ
    @GetMapping
    public ResponseEntity<Collection<Caretaker>> getCaretakers() {
        Collection<Caretaker> caretakers = caretakerService.getAll();
        if (caretakers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(caretakers));
    }

    @GetMapping("/id")
    public ResponseEntity<Caretaker> find(@RequestParam Long id) {
        Caretaker caretaker = caretakerService.find(id);
        if (caretaker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(caretaker);
    }

    @GetMapping("/chatId")
    public ResponseEntity<Caretaker> findByChatId(@RequestParam Long chatId) {
        Caretaker caretaker = caretakerService.findByChatId(chatId);
        if (caretaker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(caretaker);
    }

    //    UPDATE
    @PutMapping
    public ResponseEntity<Caretaker> edit(@RequestBody Caretaker caretaker) {
        Caretaker updatedCaretaker = caretakerService.edit(caretaker);
        if (caretaker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCaretaker);
    }

    //    DELETE
    @DeleteMapping
    public void delete(@RequestBody Caretaker caretaker) {
        caretakerService.delete(caretaker);
    }


}
