package pro.sky.javacourse.AnimalShelterBot.service;

import pro.sky.javacourse.AnimalShelterBot.model.ReportMessage;
import pro.sky.javacourse.AnimalShelterBot.model.ReportMessage;

public interface ReportMessageService {
    String getExtensions(String fileName);

    ReportMessage save(ReportMessage message);
}
