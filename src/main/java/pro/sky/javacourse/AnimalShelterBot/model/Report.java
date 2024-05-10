package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "reports")
public class Report {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "caretaker_id")
    private Caretaker caretaker;
    @ManyToOne()
    @JoinColumn(name = "pet_id")
    private Pet pet;
    private LocalDateTime dateTime;
    private String text;

    public Report(Caretaker caretaker, Pet pet) {
        this.caretaker = caretaker;
        this.pet = pet;
    }

    public Report() {
    }

    public Long getId() {
        return id;
    }

    public Caretaker getCaretaker() {
        return caretaker;
    }

    public Pet getPet() {
        return pet;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report report)) return false;
        return Objects.equals(getId(), report.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", caretaker=" + caretaker.getId() +
                ", pet=" + pet +
                ", petId=" + pet.getId() +
                ", dateTime=" + dateTime +
                ", text='" + text + '\'' +
                '}';
    }
}
