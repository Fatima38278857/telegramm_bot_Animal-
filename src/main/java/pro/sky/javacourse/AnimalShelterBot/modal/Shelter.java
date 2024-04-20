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


    public Shelter(String name, String info, String regime) {
        this.name = name;
        this.info = info;
        this.regime = regime;

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



    //------------------------------Inner class------------------------------//

}
