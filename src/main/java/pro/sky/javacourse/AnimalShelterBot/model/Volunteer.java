package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.Objects;
import java.util.Set;

@Entity(name = "volunteers")
public class Volunteer {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String address;
    private String passport;
    private Long chatId;
    @ManyToMany(mappedBy = "volunteerSet")
    private Set<Shelter> shelters;

    public Volunteer() {
    }

    public Volunteer(String name) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Shelter> getShelters() {
        return shelters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volunteer volunteer)) return false;
        return Objects.equals(getId(), volunteer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", chatId=" + chatId +
                '}';
    }

}
