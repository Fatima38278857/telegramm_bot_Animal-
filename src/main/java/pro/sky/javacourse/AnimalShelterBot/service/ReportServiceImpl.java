package pro.sky.javacourse.AnimalShelterBot.service;

import org.springframework.stereotype.Service;
import pro.sky.javacourse.AnimalShelterBot.model.Report;
import pro.sky.javacourse.AnimalShelterBot.model.ReportMessage;
import pro.sky.javacourse.AnimalShelterBot.model.ReportStatus;
import pro.sky.javacourse.AnimalShelterBot.repository.ReportRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final ReportMessageService reportMessageService;

//    New Report is created and edited in TelegramBot class, because it's  temporarily stored in Map.
//    After saving report in database, it will be available by ReportService to operate with.

    public ReportServiceImpl(ReportRepository reportRepository, ReportMessageService reportMessageService) {
        this.reportRepository = reportRepository;
        this.reportMessageService = reportMessageService;
    }

    @Override
    public Report saveReport(Report report) {
        // получаем объект репорт с Id
        report.setStatus(ReportStatus.UNVERIFIED);
        Report reportFromDb = reportRepository.save(report);
        List<ReportMessage> reportMessages = report.getMessages();
        for (ReportMessage message: reportMessages) {
            message.setReport(reportFromDb);
            reportMessageService.save(message);
        }
        return reportFromDb;
    }

    @Override
    public Report findById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Override
    public Collection<Report> findByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    @Override
    public Report approveReport(Long reportId, Long volunteerChatId) {
        Report reportFromDb = reportRepository.findById(reportId).orElse(null);
        if (reportFromDb != null && reportFromDb.getStatus().equals(ReportStatus.UNVERIFIED)) {
            reportFromDb.setStatus(ReportStatus.ACCEPTED);
            reportFromDb.setInspectionTime(LocalDateTime.now());
            return reportRepository.save(reportFromDb);
        } else return null;
    }

    @Override
    public Report declineReport(Long reportId, Long volunteerChatId) {
        Report reportFromDb = reportRepository.findById(reportId).orElse(null);
        if (reportFromDb != null && reportFromDb.getStatus().equals(ReportStatus.UNVERIFIED)) {
            reportFromDb.setStatus(ReportStatus.REJECTED);
            reportFromDb.setInspectionTime(LocalDateTime.now());
            return reportRepository.save(reportFromDb);
        } else return null;
    }
}

// по расписанию из мар должны удаляться старые незаполненные отчеты
// кнопка отчеты должна появляться только если нужно представить отчет (прошло время после предыдущего отчета и у опекуна есть питомец)
