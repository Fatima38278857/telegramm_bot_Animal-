package pro.sky.javacourse.AnimalShelterBot.service.impl;

import pro.sky.javacourse.AnimalShelterBot.service.ShelterService;

import java.time.LocalDate;

public class ShelterServiceImpl implements ShelterService {
    @Override
    public boolean isReportApproved() {
        return false;
    }

    @Override
    public void setComments(String comments) {

    }

    @Override
    public void setTrialPeriodEnd(LocalDate newEndDate) {

    }

    @Override
    public void setTrialPeriodStart(LocalDate startDate) {

    }
}
