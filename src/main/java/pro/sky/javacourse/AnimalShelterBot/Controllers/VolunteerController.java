package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.javacourse.AnimalShelterBot.model.Volunteer;
import pro.sky.javacourse.AnimalShelterBot.service.VolunteerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @PostMapping
    public ResponseEntity<Volunteer> addVolunteer(@RequestBody Volunteer volunteer) {
        Volunteer addedVolunteer = volunteerService.addVolunteer(volunteer);
        return new ResponseEntity<>(addedVolunteer, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteer(@PathVariable Long id) {
        volunteerService.deleteVolunteer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {
        List<Volunteer> volunteers = volunteerService.getAllVolunteers();
        return new ResponseEntity<>(volunteers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getVolunteerById(@PathVariable Long id) {
        Optional<Volunteer> volunteer = volunteerService.getVolunteerById(id);
        return volunteer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{volunteerId}/working-status")
    public ResponseEntity<Void> changeWorkingStatus(@PathVariable long volunteerId, @RequestParam boolean working) {
        volunteerService.changeWorkingStatus(volunteerId, working);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    // Endpoint для назначения животного усыновителю
    @PostMapping("/assign-pet-to-shelter")
    public ResponseEntity<?> assignPetToAdopter(@RequestParam int petId, @RequestParam int adopterId) {
        volunteerService.assignPetToAdopter(petId, adopterId);
        return ResponseEntity.ok("Pet assigned to adopter successfully");
    }

    // Endpoint для назначения испытательного срока
    @PostMapping("/set-trial-period")
    public ResponseEntity<?> setTrialPeriod(@RequestParam int shelterId, @RequestParam String startDate, @RequestParam String endDate) {
        volunteerService.setTrialPeriod(shelterId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        return ResponseEntity.ok("Trial period set successfully");
    }

    // Endpoint для проверки отчета усыновителя
    @GetMapping("/check-adopter-report")
    public ResponseEntity<?> checkAdopterReport(@RequestParam int shelterId) {
        boolean reportApproved = volunteerService.checkAdopterReport(shelterId);
        if (reportApproved) {
            return ResponseEntity.ok("Adopter's report approved");
        } else {
            return ResponseEntity.ok("Adopter's report not approved");
        }
    }

    // Endpoint для делания замечаний усыновителю
    @PostMapping("/make-comments-to-shelter")
    public ResponseEntity<?> makeCommentsToAdopter(@RequestParam int shelterId, @RequestParam String comments) {
        volunteerService.makeCommentsToAdopter(shelterId, comments);
        return ResponseEntity.ok("Comments added successfully");
    }

    // Endpoint для продления испытательного срока
    @PostMapping("/extend-trial-period")
    public ResponseEntity<?> extendTrialPeriod(@RequestParam int shelterId, @RequestParam String newEndDate) {
        volunteerService.extendTrialPeriod(shelterId, LocalDate.parse(newEndDate));
        return ResponseEntity.ok("Trial period extended successfully");
    }

    // Endpoint для забирания или отдачи животного у/от усыновителя
    @PostMapping("/take-or-give-pet")
    public ResponseEntity<?> takeOrGivePet(@RequestParam int petId, @RequestParam boolean takeBack) {
        volunteerService.takeOrGivePet(petId, takeBack);
        if (takeBack) {
            return ResponseEntity.ok("Pet taken back from adopter successfully");
        } else {
            return ResponseEntity.ok("Pet given to adopter successfully");
        }
    }
}