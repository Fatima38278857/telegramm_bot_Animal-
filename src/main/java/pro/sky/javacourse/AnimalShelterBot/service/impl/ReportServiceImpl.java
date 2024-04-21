package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.model.Report;
import pro.sky.javacourse.AnimalShelterBot.repository.ReportRepository;
import pro.sky.javacourse.AnimalShelterBot.service.ReportService;

public class ReportServiceImpl implements ReportService {
    private ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void addReport(Report report) {
        reportRepository.save(report);
    }

    @Override
    public void saveAReport(Report report) {

    }
}
