package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "pets")
public class Pet {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
    private String type; // Собака, кот, другое
    private String abilities;
    private String restrictions;
    private String conditions;
    @Lob
    private byte[] avatar;
    private LocalDateTime trialStart;
    private LocalDateTime trialEnd;
    private String status; // передан хозяину навсегда, на испытательном сроке, болеет, умер
    @ManyToOne()
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;


    public Pet() {
    }

    public Pet(String name, Integer age, String type, Shelter shelter) {
        this.name = name;
        this.age = age;
        this.type = type;
        this.shelter = shelter;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public String getAbilities() {
        return abilities;
    }

    public void setAbilities(String abilities) {
        this.abilities = abilities;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public LocalDateTime getTrialStart() {
        return trialStart;
    }

    public void setTrialStart(LocalDateTime trialStart) {
        this.trialStart = trialStart;
    }

    public LocalDateTime getTrialEnd() {
        return trialEnd;
    }

    public void setTrialEnd(LocalDateTime trialEnd) {
        this.trialEnd = trialEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet pet)) return false;
        return Objects.equals(id, pet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", type='" + type + '\'' +
                ", restrictions='" + restrictions + '\'' +
                ", status='" + status + '\'' +
                ", shelter=" + shelter.getName() +
                '}';
    }
}
