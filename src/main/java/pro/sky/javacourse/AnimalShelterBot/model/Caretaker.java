package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity(name = "caretakers")
public class Caretaker {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String address;
    private String passport;
    private Long chatId;
    public Caretaker() {
    }

    public Caretaker(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Caretaker caretaker)) return false;
        return Objects.equals(getId(), caretaker.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Caretaker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", passport='" + passport + '\'' +
                ", chatId=" + chatId +
                '}';
    }
}
