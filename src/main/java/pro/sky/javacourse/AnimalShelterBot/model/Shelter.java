package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity(name = "shelters")
public class Shelter {
    @Id
    @GeneratedValue

    private Long id;
    private String name;
    private String address;
    private String regime;
    @Lob
    private byte[] locationMap;
    @ManyToMany()
    private Set<Volunteer> volunteerSet;
    @OneToOne()
    @JoinColumn(name = "volunteer_id")
    private Volunteer mainVolunteer;

    public Shelter() {
    }

    public Shelter(String name, String address) {
        this.name = name;
        this.address = address;
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

    public Volunteer getMainVolunteer() {
        return mainVolunteer;
    }

    public void setMainVolunteer(Volunteer mainVolunteer) {
        this.mainVolunteer = mainVolunteer;
    }

    public String getRegime() {
        return regime;
    }

    public void setRegime(String regime) {
        this.regime = regime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shelter shelter)) return false;
        return Objects.equals(id, shelter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public boolean isReportApproved() {
        return false;
    }

    public void setComments(String comments) {
    }

    public void setTrialPeriodEnd(LocalDate newEndDate) {
    }

    public void setTrialPeriodStart(LocalDate startDate) {
    }
}
