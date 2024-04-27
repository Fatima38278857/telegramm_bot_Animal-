package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

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

    public Shelter() {
    }

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
