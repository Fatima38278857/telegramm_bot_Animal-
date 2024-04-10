package pro.sky.javacourse.AnimalShelterBot.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Pet {
    private Long id;
    private Long shelterId;
    private Long caretakerId;
    private String type; // change animal (doc, cat, etc)
    private String name;
    private String breed;
    private int age;
    private String color;
    private LocalDateTime startProbationary;
    private LocalDateTime endProbationary;

    public Pet(Long shelterId, Long caretakerId, String type, String name, String breed, int age, String color) {
        this.shelterId = shelterId;
        this.caretakerId = caretakerId;
        this.type = type;
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.color = color;
    }

    public Long getId() { return id; }
    public Long getShelterId() { return shelterId; }
    public Long getCaretakerId() { return caretakerId; }
    public void setCaretakerId(Long caretakerId) { this.caretakerId = caretakerId; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getColor() { return color; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Pet pet = (Pet) object;
        return age == pet.age && Objects.equals(id, pet.id) && Objects.equals(shelterId, pet.shelterId) && Objects.equals(caretakerId, pet.caretakerId) && Objects.equals(type, pet.type) && Objects.equals(name, pet.name) && Objects.equals(breed, pet.breed) && Objects.equals(color, pet.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shelterId, caretakerId, type, name, breed, age, color);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", shelterId=" + shelterId +
                ", caretakerId=" + caretakerId +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                ", color='" + color + '\'' +
                '}';
    }
}
