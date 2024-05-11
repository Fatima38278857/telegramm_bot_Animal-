package pro.sky.javacourse.AnimalShelterBot.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("shelters")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService, VolunteerService volunteerService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    public ResponseEntity<Collection<Shelter>> getShelters() {
        Collection<Shelter> shelters = shelterService.getAll();
        if (shelters.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(shelters));
    }

    @GetMapping("/id")
    public ResponseEntity<Shelter> find(@RequestParam Long id) {
        Shelter shelter = shelterService.find(id);
        if (shelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shelter);
    }

    @GetMapping("/name")
    public ResponseEntity<Shelter> find(@RequestParam String name) {
        Shelter shelter = shelterService.findShelterByName(name);
        if (shelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shelter);
    }

    @GetMapping("/mainVolunteer")
    public ResponseEntity<Volunteer> getMainVolunteer(@RequestParam Long shelterId) {
        Volunteer mainVolunteer = shelterService.getMainVolunteer(shelterId);
        if (mainVolunteer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mainVolunteer);
    }

    @GetMapping("/volunteers")
    public ResponseEntity<Collection<Volunteer>> getVolunteers(@RequestParam Long shelterId) {
        Collection<Volunteer> volunteers = shelterService.getVolunteers(shelterId);
        if (volunteers == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(volunteers));
    }

    @PostMapping
    public Shelter add(@RequestBody Shelter shelter) {
        return shelterService.add(shelter);
    }

    @PutMapping("{id}")
    public ResponseEntity<Shelter> edit(@PathVariable("id") Long id, @RequestBody Shelter shelter) {
        Shelter updatedshelter = shelterService.edit(id, shelter);
        if (updatedshelter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedshelter);
    }

    @PutMapping(value = "{id}/location", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadLocationMap(@PathVariable("id") Long shelterId, @RequestParam MultipartFile locationMapFile) throws IOException {
        if (locationMapFile.getSize() > 1000 * 1000) {
            return ResponseEntity.badRequest().body("File must not be larger then 1000x1080 pixels.");
        }
        shelterService.uploadLocationMap(shelterId, locationMapFile);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "{id}/mainVolunteer")
    public ResponseEntity<String> setMainVolunteer(@PathVariable("id") Long shelterId, Long mainVolunteerId) {
        shelterService.setMainVolunteer(shelterId, mainVolunteerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public void delete(@RequestBody Shelter shelter) {
        shelterService.delete(shelter);
    }

}
