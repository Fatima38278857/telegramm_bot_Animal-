package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;


import java.util.Set;

@Entity
@Table(name = "shelter")
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "info")
    private String info;
    @Column(name = "regime")
    private String regime;
    @Column(name = "address")
    private String address;

    @ManyToMany()
    @Column(name = "volunteers")
    private Set<Volunteer> volunteerSet;


    public Shelter() {
    }


    public Shelter(String name, String info, String regime, String address) {
        this.name = name;
        this.info = info;
        this.regime = regime;
        this.address = address;
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


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;

    }

    public String getRegime() {
        return regime;
    }

    public void setRegime(String regime) {
        this.regime = regime;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Volunteer> getVolunteerSet() {
        return volunteerSet;
    }

    public void setVolunteerSet(Set<Volunteer> volunteerSet) {
        this.volunteerSet = volunteerSet;
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

}
