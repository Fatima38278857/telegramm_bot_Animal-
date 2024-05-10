package pro.sky.javacourse.AnimalShelterBot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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
    private String phoneNumber;
    private Long chatId;
    @JsonIgnore
    @ManyToMany(mappedBy = "volunteerSet", fetch = FetchType.LAZY)
    private Set<Shelter> shelters;

    public Volunteer() {
    }
    public Volunteer(String name, Long chatId) {
        this.name = name;
        this.chatId = chatId;
    }

    public Volunteer(String name, String address, String passport, String phoneNumber, Long chatId) {
        this.name = name;
        this.address = address;
        this.passport = passport;
        this.phoneNumber = phoneNumber;
        this.chatId = chatId;
    }

    public Volunteer(String name, String address, String passport, String phoneNumber, Long chatId, Set<Shelter> shelters) {
        this(name, address, passport, phoneNumber, chatId);
        this.shelters = shelters;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
                ", address='" + address + '\'' +
                ", passport='" + passport + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", chatId=" + chatId +
                '}';
    }
}
