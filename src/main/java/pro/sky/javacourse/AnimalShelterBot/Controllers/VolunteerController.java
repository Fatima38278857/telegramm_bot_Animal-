package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.repository.VolunteerRepository;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;

import java.util.List;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final VolunteerRepository volunteerRepository;


    public VolunteerController(VolunteerService volunteerService, VolunteerRepository volunteerRepository) {
        this.volunteerService = volunteerService;
        this.volunteerRepository = volunteerRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Volunteer> addVolunteer(@RequestBody Volunteer volunteer) {
        Volunteer savedVolunteer = volunteerService.saveVolunteer(volunteer);
        return new ResponseEntity<>(savedVolunteer, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteVolunteer(@PathVariable Long id) {
        volunteerRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> findVolunteerById(@PathVariable Long id) {
        Volunteer volunteer = volunteerService.findVolunteerById(id);
        if (volunteer != null) {
            return new ResponseEntity<>(volunteer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

}