package pro.sky.javacourse.AnimalShelterBot.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Map;
import java.util.Objects;
@Entity
public class Caretaker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private final Long chatId;
    private String name;
    private String address;
    private String comments;
    private Long petId;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Caretaker caretaker = (Caretaker) o;
        return Objects.equals(id, caretaker.id) && Objects.equals(name, caretaker.name) && Objects.equals(address, caretaker.address) && Objects.equals(comments, caretaker.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, comments);
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }



    public Long getChatId() {
        return chatId;
    }
}
