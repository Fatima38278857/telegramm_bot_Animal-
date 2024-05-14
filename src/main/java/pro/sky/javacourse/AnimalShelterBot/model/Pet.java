package pro.sky.javacourse.AnimalShelterBot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "pets")
public class Pet {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PetType type; // СОБАКА, КОТ, ОСТАЛЬНЫЕ
    private String abilities;
    private String restrictions;
    private String conditions;
    private String avatarFilePath;
    @JsonIgnore
    private Long avatarFileSize;
    @JsonIgnore
    private String avatarMediaType;
    @JsonIgnore
    @Lob
    private byte[] avatar;
    private LocalDateTime trialStart;
    private LocalDateTime trialEnd;
    @ManyToOne()
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PetStatus status;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "caretaker_id")
    private Caretaker caretaker;

    public Pet() {
    }

    public Pet(String name, Integer age, PetType type, String abilities, String restrictions, String conditions,  Shelter shelter) {
        this.name = name;
        this.age = age;
        this.type = type;
        this.abilities = abilities;
        this.restrictions = restrictions;
        this.conditions = conditions;
        this.shelter = shelter;
        this.status = PetStatus.ОФОРМЛЯЕТСЯ;
    }

    public Pet(Long id, String name, Integer age, PetType type, String abilities, String restrictions, String conditions, String avatarFilePath, Shelter shelter, PetStatus status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.type = type;
        this.abilities = abilities;
        this.restrictions = restrictions;
        this.conditions = conditions;
        this.avatarFilePath = avatarFilePath;
        this.shelter = shelter;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public String getAbilities() {
        return abilities;
    }

    public void setAbilities(String abilities) {
        this.abilities = abilities;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public String getAvatarFilePath() {
        return avatarFilePath;
    }

//    public String getLocationMapFilePath() {
//        return locationMapFilePath;
//    }
//
//    public void setLocationMapFilePath(String locationMapFilePath) {
//        this.locationMapFilePath = locationMapFilePath;
//    }

    public void setAvatarFilePath(String avatarFilePath) {
        this.avatarFilePath = avatarFilePath;
    }

    public Long getAvatarFileSize() {
        return avatarFileSize;
    }

    public void setAvatarFileSize(Long avatarFileSize) {
        this.avatarFileSize = avatarFileSize;
    }

    public String getAvatarMediaType() {
        return avatarMediaType;
    }

    public void setAvatarMediaType(String avatarMediaType) {
        this.avatarMediaType = avatarMediaType;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getTrialStart() {
        return trialStart;
    }

    public void setTrialStart(LocalDateTime trialStart) {
        this.trialStart = trialStart;
    }

    public LocalDateTime getTrialEnd() {
        return trialEnd;
    }

    public void setTrialEnd(LocalDateTime trialEnd) {
        this.trialEnd = trialEnd;
    }

    public PetStatus getStatus() {
        return status;
    }

    public void setStatus(PetStatus status) {
        this.status = status;
    }

    public Caretaker getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(Caretaker caretaker) {
        this.caretaker = caretaker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet pet)) return false;
        return Objects.equals(id, pet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", type='" + type + '\'' +
                ", restrictions='" + restrictions + '\'' +
                ", status='" + status.name() + '\'' +
                '}';
    }
}
