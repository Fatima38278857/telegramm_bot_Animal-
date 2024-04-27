package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

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
    public Caretaker() {
    }

    public Caretaker(Long chatId, String name, String address) {
        this.chatId = chatId;
        this.name = name;
        this.address = address;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


}
