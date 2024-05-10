package pro.sky.javacourse.AnimalShelterBot.service;

import java.time.LocalDate;

public interface ShelterService {
     boolean isReportApproved();

     void setComments(String comments);

     void setTrialPeriodEnd(LocalDate newEndDate);

     void setTrialPeriodStart(LocalDate startDate);
}
