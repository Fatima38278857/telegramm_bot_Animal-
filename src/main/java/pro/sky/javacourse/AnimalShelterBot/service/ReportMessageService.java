package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.ReportMessage;
import pro.sky.javacourse.AnimalShelterBot.model.ReportMessage;

import java.util.List;

public interface ReportMessageService {
    String getExtensions(String fileName);

    ReportMessage save(ReportMessage message);
    List<ReportMessage> findReportMessages(Long reportId);
}
