package pro.sky.javacourse.AnimalShelterBot.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    @PutMapping("/{volunteerId}/assign-pet/{petId}/to-caretaker/{caretakerId}")
    public ResponseEntity<String> assignPetToCaretaker(@PathVariable Long volunteerId, @PathVariable Long petId, @PathVariable Long caretakerId) {
        volunteerService.assignPetToCaretaker(volunteerId, petId, caretakerId);
        return ResponseEntity.ok("Pet assigned to caretaker successfully.");
    }

    @PutMapping("/{caretakerId}/trial-period-end")
    public ResponseEntity<String> setTrialPeriodEnd(@PathVariable Long caretakerId, @RequestParam("newEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
        volunteerService.setTrialPeriodEnd(caretakerId, newEndDate);
        return ResponseEntity.ok("Trial period end date set successfully.");
    }}