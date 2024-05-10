package pro.sky.javacourse.AnimalShelterBot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

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
    @JsonIgnore
    @Column(name = "how_to", length = 2048)
    private String howTo;
    @JsonIgnore
    private String locationMapFileName;
    @JsonIgnore
    private Long locationMapFileSize;
    @JsonIgnore
    private String locationMapMediaType;
    @JsonIgnore
    @Lob
    private byte[] locationMap;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "shelters_volunteers",
            joinColumns = {
                    @JoinColumn(name = "shelter_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "volunteer_id", referencedColumnName = "id")
            }
    )
    @JsonIgnore
    private Set<Volunteer> volunteerSet;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "volunteer_id")
    private Volunteer mainVolunteer;

    public Shelter() {
    }

    public Shelter(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Shelter(String name, String address, String regime) {
        this.name = name;
        this.address = address;
        this.regime = regime;
    }

    public Shelter(String name, String address, String regime, String howTo, Volunteer mainVolunteer) {
        this.name = name;
        this.address = address;
        this.regime = regime;
        this.howTo = howTo;
        this.mainVolunteer = mainVolunteer;
    }

    public Long getId() {
        return id;
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

    public String getHowTo() {
        return howTo;
    }

    public void setHowTo(String howTo) {
        this.howTo = howTo;
    }

    public byte[] getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(byte[] locationMap) {
        this.locationMap = locationMap;
    }

    public Set<Volunteer> getVolunteerSet() {
        return volunteerSet;
    }

    public void setVolunteerSet(Set<Volunteer> volunteerSet) {
        this.volunteerSet = volunteerSet;
    }

    public String getLocationMapFileName() {
        return locationMapFileName;
    }

    public void setLocationMapFileName(String locationMapFileName) {
        this.locationMapFileName = locationMapFileName;
    }

    public Long getLocationMapFileSize() {
        return locationMapFileSize;
    }

    public void setLocationMapFileSize(Long locationMapFileSize) {
        this.locationMapFileSize = locationMapFileSize;
    }

    public String getLocationMapMediaType() {
        return locationMapMediaType;
    }

    public void setLocationMapMediaType(String locationMapMediaType) {
        this.locationMapMediaType = locationMapMediaType;
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
                ", mainVolunteer=" + mainVolunteer +
                '}';
    }
}
