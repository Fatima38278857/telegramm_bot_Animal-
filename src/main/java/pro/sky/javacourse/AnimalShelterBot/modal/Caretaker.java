package pro.sky.javacourse.AnimalShelterBot.modal;

import jakarta.persistence.Entity;

import java.util.Objects;
@Entity
public class Caretaker {
    private Long id;
    private String name;
    private String address;
    private String comments;
    private Long petId;

    public Caretaker(String name, String address, String comments, Long petId) {
        this.name = name;
        this.address = address;
        this.comments = comments;
        this.petId = petId;
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
}
