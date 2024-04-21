package pro.sky.javacourse.AnimalShelterBot.model;
import jakarta.persistence.*;

@Entity
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String number;

    private boolean isAvailable;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    private Pet adoptedPet;

    // Конструкторы
    public Volunteer() {
    }

    public Volunteer(String name, String number) {
        this.name = name;
        this.number = number;
        this.isAvailable = true; // Новый волонтер по умолчанию доступен для общения
    }

    // Геттеры и сеттеры
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Pet getAdoptedPet() {
        return adoptedPet;
    }

    public void setAdoptedPet(Pet adoptedPet) {
        this.adoptedPet = adoptedPet;
    }

    // Дополнительные методы
    public void assignPet(Pet pet) {
        this.adoptedPet = pet;
    }

    public void extendTrialPeriod(int days) {
        // Логика продления испытательного срока
        // Например, увеличение срока на указанное количество дней
    }

    public void checkAdoptionReport() {
        // Логика проверки отчета усыновителя
    }

    public void makeRemarksToAdopter(String remarks) {
        // Логика оставления замечаний усыновителю
    }

    public void takeBackPet() {
        this.adoptedPet = null;
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", isAvailable=" + isAvailable +
                ", adoptedPet=" + adoptedPet +
                '}';
    }
}