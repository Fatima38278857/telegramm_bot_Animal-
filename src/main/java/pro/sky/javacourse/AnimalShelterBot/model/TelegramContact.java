package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class TelegramContact {
    @Id
    private String phoneNumber;
    private Long chatId;
    @ManyToOne()
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;
    private String firstName;
    private String lastName;
    private String status;

    public TelegramContact() {
    }

    public TelegramContact(Long chatId, String firstName, String lastName, String phoneNumber, Shelter shelter) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.chatId = chatId;
        this.shelter = shelter;
        this.status = "unsolved";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getChatId() {
        return chatId;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        if (!(o instanceof TelegramContact that)) return false;
        return Objects.equals(getPhoneNumber(), that.getPhoneNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhoneNumber());
    }

    @Override
    public String toString() {
        return "TelegramContact{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", chatId=" + chatId +
                ", shelter=" + shelter +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
