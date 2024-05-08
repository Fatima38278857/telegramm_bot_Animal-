package pro.sky.javacourse.AnimalShelterBot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Shelter;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;

import java.util.List;

@RestController
@RequestMapping("shelter")
public class ShelterController {
    @Autowired
    private ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping("get_{id}")
    public Shelter takeShelterById(@PathVariable Long id) {
        return shelterService.findingShelterById(id);
    }

    @GetMapping("info_about_{name}")
    public String giveInfoAboutShelter(@PathVariable String name) {
        return shelterService.getInfoAboutShelter(name);
    }
    @GetMapping("delete_{id}")
    public void deleteShelter(@PathVariable Long id) {
         shelterService.delete(id);
    }

    @GetMapping("get_all")
    public List<Shelter> getAllShelters() {
        return shelterService.getAllShelters();
    }
    @PostMapping
    public Shelter addShelter(@RequestBody Shelter shelter) {
        Shelter savedShelter = shelterService.addShelter(shelter);
        return savedShelter;
    }
}
