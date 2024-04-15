package pro.sky.javacourse.AnimalShelterBot.model;

import java.util.ArrayList;
import java.util.List;

public class Volunteer {
    private long id;
    private String name;
    private String city;
    private boolean working; // Статус работы: работает или не работает
    private Pet currentPet; // Текущее животное, назначенное усыновителю
    private boolean trialPeriod; // Флаг указывающий на наличие испытательного срока
    private boolean reportApproved; // Флаг подтверждения отчета усыновителя
    private String remarks; // Замечания для усыновителя

    public Volunteer(long id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.working = true; // По умолчанию волонтер работает
        this.currentPet = null;
        this.trialPeriod = false;
        this.reportApproved = false;
        this.remarks = "";
    }


    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public Pet getCurrentPet() {
        return currentPet;
    }

    public void setCurrentPet(Pet currentPet) {
        this.currentPet = currentPet;
    }

    public boolean isTrialPeriod() {
        return trialPeriod;
    }

    public void setTrialPeriod(boolean trialPeriod) {
        this.trialPeriod = trialPeriod;
    }

    public boolean isReportApproved() {
        return reportApproved;
    }

    public void setReportApproved(boolean reportApproved) {
        this.reportApproved = reportApproved;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // Метод для назначения животного усыновителю
    public void assignPetToAdopter(Pet pet) {
        this.currentPet = pet;
        this.trialPeriod = true; // Установка испытательного срока
    }

    // Метод для проверки отчета усыновителя
    public void checkAdopterReport(boolean approved, String remarks) {
        this.reportApproved = approved;
        this.remarks = remarks;
    }

    // Метод для продления испытательного срока
    public void extendTrialPeriod() {
        this.trialPeriod = true;
    }

    // Метод для забора животного у усыновителя
    public void takeBackPet() {
        this.currentPet = null;
        this.trialPeriod = false; // Завершение испытательного срока
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "id=" + id +
                ", firstName='" + name + '\'' +
                ", city='" + city + '\'' +
                ", working=" + working +
                ", currentPet=" + (currentPet != null ? currentPet.getName() : "None") +
                ", trialPeriod=" + trialPeriod +
                ", reportApproved=" + reportApproved +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}