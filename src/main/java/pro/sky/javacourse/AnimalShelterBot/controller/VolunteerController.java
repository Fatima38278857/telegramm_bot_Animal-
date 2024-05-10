package pro.sky.javacourse.AnimalShelterBot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("volunteers")
public class VolunteerController {
    private final VolunteerService volunteerService;


    public VolunteerController(VolunteerService volunteerService, ShelterService shelterService) {
        this.volunteerService = volunteerService;
    }

    //    CREATE
    @PostMapping
    public Volunteer add(@RequestBody Volunteer volunteer) {
        return volunteerService.add(volunteer);
    }

    // READ
    @GetMapping
    public ResponseEntity<Collection<Volunteer>> getVolunteers() {
        Collection<Volunteer> volunteers = volunteerService.getAll();
        if (volunteers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(volunteers));
    }

    @GetMapping("/id")
    public ResponseEntity<Volunteer> find(@RequestParam Long id) {
        Volunteer volunteer = volunteerService.find(id);
        if (volunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(volunteer);
    }

    @GetMapping("/chatId")
    public ResponseEntity<Volunteer> findByChatId(@RequestParam Long chatId) {
        Volunteer volunteer = volunteerService.findByChatId(chatId);
        if (volunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(volunteer);
    }

    @GetMapping("/shelters")
    public ResponseEntity<Collection<Shelter>> getShelters(@RequestParam Long id) {
        Collection<Shelter> shelters = volunteerService.getShelters(id);

        return ResponseEntity.ok(Collections.unmodifiableCollection(shelters));
    }

    // UPDATE
    @PutMapping(value = "{id}/shelter")
    public ResponseEntity<String> addToShelter(@PathVariable("id") Long id, Long shelterId) {
        volunteerService.addToShelter(id, shelterId);
        return ResponseEntity.ok().build();
    }

    // DELETE
    @PutMapping(value = "{id}/removeShelter")
    public ResponseEntity<String> removeFromShelter(@PathVariable("id") Long id, Long shelterId) {
        volunteerService.removeFromShelter(id, shelterId);
        return ResponseEntity.ok().build();
    }


}
