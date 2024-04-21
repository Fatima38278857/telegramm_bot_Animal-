package pro.sky.javacourse.AnimalShelterBot.intermediary;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Report;
import pro.sky.javacourse.AnimalShelterBot.service.CaretakerService;
import pro.sky.javacourse.AnimalShelterBot.service.ReportService;
import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;

@Service
public class QueryDefinitionClass {
    private ReportService reportService;
    private ShelterService shelterService;
    private CaretakerService caretakerService;

    public QueryDefinitionClass(ShelterService shelterService, CaretakerService caretakerService) {
        this.shelterService = shelterService;
        this.caretakerService = caretakerService;
    }

    public String getInfoAboutShelter(String shelterName) {
        return shelterService.findingShelterByName(shelterName).getName();
    }

    public void takeAReportAboutPet(Report report) {
        reportService.saveAReport(report);
    }



}
