package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.modal.Report;

@Service
public interface ReportService {
    void addReport(Report report);
}
