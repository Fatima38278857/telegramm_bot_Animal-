package pro.sky.javacourse.AnimalShelterBot.model;
import jakarta.persistence.*;

@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private int age;
    private boolean adopted;
    private Long caretakerId;

    // Constructors, getters, and setters

    public Pet() {
    }
    @ManyToOne
    @JoinColumn(name = "caretaker_id")
    private Caretaker caretaker;

    // Геттер и сеттер для усыновителя
    public Caretaker getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(Caretaker caretaker) {
        this.caretaker = caretaker;
    }
    public Pet(String name, String species, int age) {
        this.name = name;
        this.species = species;
        this.age = age;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // toString method

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", age=" + age +
                '}';
    }

    public boolean isAdopted() {
    return false;
    }
    public void setAdopted(boolean adopted) {
        this.adopted = adopted;
    }

    public void setCaretakerId(Long caretakerId) {
        this.caretakerId = caretakerId;
    }
}