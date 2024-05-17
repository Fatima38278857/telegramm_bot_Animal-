package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.Report;
import pro.sky.javacourse.AnimalShelterBot.model.ReportStatus;

import java.util.Collection;
import java.util.List;

public interface ReportService {
    Report saveReport(Report report);
    Report findById(Long id);
    Collection<Report> findByStatus(ReportStatus status);

    Report approveReport(Long reportId, Long volunteerChatId);

    Report declineReport(Long reportId, Long volunteerChatId);
}
