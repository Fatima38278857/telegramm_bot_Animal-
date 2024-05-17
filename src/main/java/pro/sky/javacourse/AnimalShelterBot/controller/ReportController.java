package pro.sky.javacourse.AnimalShelterBot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.sky.javacourse.AnimalShelterBot.model.Pet;
import pro.sky.javacourse.AnimalShelterBot.model.PetStatus;
import pro.sky.javacourse.AnimalShelterBot.model.Report;
import pro.sky.javacourse.AnimalShelterBot.model.ReportStatus;
import pro.sky.javacourse.AnimalShelterBot.service.ReportService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("{status}")
    public ResponseEntity<Collection<Report>> findByStatus(@RequestParam ReportStatus status) {
        Collection<Report> reports = reportService.findByStatus(status);
        if (reports.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Collections.unmodifiableCollection(reports));
    }
}
