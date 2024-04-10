package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.modal.Report;
import pro.sky.javacourse.AnimalShelterBot.repository.ReportRepository;
import pro.sky.javacourse.AnimalShelterBot.service.ReportService;

public class ReportServiceImpl implements ReportService {
    private ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public void saveAReport(Report report) {
        reportRepository.save(report);
    }
}
