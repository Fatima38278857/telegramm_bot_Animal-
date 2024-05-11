package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
@Entity
@Table(name = "caretaker")
public class Caretaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "day_of_getting_pet")
    private int dayOfGettingPet;
    @Column(name = "on_probation")
    private boolean onProbation;
    @Column(name = "passport")
    private String passport;

    public Caretaker() {
    }
    private LocalDate trialPeriodStart;

    private LocalDate trialPeriodEnd;

    private String comments;

    // Геттеры и сеттеры для испытательного срока и комментариев
    public LocalDate getTrialPeriodStart() {
        return trialPeriodStart;
    }

    public void setTrialPeriodStart(LocalDate trialPeriodStart) {
        this.trialPeriodStart = trialPeriodStart;
    }

    public LocalDate getTrialPeriodEnd() {
        return trialPeriodEnd;
    }

    public void setTrialPeriodEnd(LocalDate trialPeriodEnd) {
        this.trialPeriodEnd = trialPeriodEnd;
    }

    public String getComments() {
        return comments;
    }

    public void addComments(String comments) {
        this.comments = comments;
    }
    public Caretaker(Long chatId, String name, String address, String passport) {
        this.chatId = chatId;
        this.name = name;
        this.address = address;
        this.passport = passport;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Caretaker caretaker = (Caretaker) o;
        return dayOfGettingPet == caretaker.dayOfGettingPet && onProbation == caretaker.onProbation && Objects.equals(id, caretaker.id) && Objects.equals(chatId, caretaker.chatId) && Objects.equals(name, caretaker.name) && Objects.equals(address, caretaker.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, name, address, dayOfGettingPet, onProbation);
    }

    public Long getChatId() {
        return chatId;
    }

    public int getDayOfGettingPet() {
        return dayOfGettingPet;
    }

    public void setDayOfGettingPet(int dayOfGettingPet) {
        this.dayOfGettingPet = dayOfGettingPet;
    }

    public boolean isOnProbation() {
        return onProbation;
    }

    public void setOnProbation(boolean onProbation) {
        this.onProbation = onProbation;
    }


    public String getPassport() {
        return passport;
    }

    @Override
    public String toString() {
        return "Caretaker{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", dayOfGettingPet=" + dayOfGettingPet +
                ", onProbation=" + onProbation +
                ", passport='" + passport + '\'' +
                '}';
    }

    public void setPassport(String passport) {
        this.passport = passport;

    }
}