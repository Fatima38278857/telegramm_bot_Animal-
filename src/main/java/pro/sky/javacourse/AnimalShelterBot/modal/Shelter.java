package pro.sky.javacourse.AnimalShelterBot.modal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.awt.*;
import java.util.List;
@Entity
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String info;
    private String regime;
    private final Location location;
    private List<Volunteer> volunteers;

    public Shelter(String name, String info, String regime,
                   String address, List<Image> locationMaps) {
        this.name = name;
        this.info = info;
        this.regime = regime;
        location = new Location(address, locationMaps);
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

    public List<Volunteer> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<Volunteer> volunteers) {
        this.volunteers = volunteers;
    }


    //------------------------------Inner class------------------------------//
    @Entity
    private static class Location {
        private String address;
        private List<Image> locationMaps;

        public Location(String address, List<Image> locationMaps) {
            this.address = address;
            this.locationMaps = locationMaps;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<Image> getLocationMaps() {
            return locationMaps;
        }

        public void setLocationMaps(List<Image> locationMaps) {
            this.locationMaps = locationMaps;
        }
    }
}
