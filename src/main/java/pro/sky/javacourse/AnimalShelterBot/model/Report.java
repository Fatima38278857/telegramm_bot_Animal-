package pro.sky.javacourse.AnimalShelterBot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "reports")
public class Report {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "caretaker_id")
    private Caretaker caretaker;
    @ManyToOne()
    @JoinColumn(name = "pet_id")
    private Pet pet;
    @OneToMany (mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = false)
    private final List<ReportMessage> messages = new ArrayList<>();
    private LocalDateTime creationTime;
    private LocalDateTime inspectionTime;
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ReportStatus status;
    private int photosSize;
    private int textLength;
    private final int MAXMESSAGES = 20;
    private final int MINTEXTLENGTH = 25;
    private final int MAXTEXTLENGTH = 1000;
    private final int MAXPHOTOSIZE = 50 * 1024 * 1024; // 50 Mb


    public Report() {
    }

    public Report(Pet pet, Caretaker caretaker) {
        this.pet = pet;
        this.caretaker = caretaker;
        this.creationTime = LocalDateTime.now();
        this.status = ReportStatus.INCOMPLETE;
        this.photosSize = 0;
        this.textLength = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Caretaker getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(Caretaker caretaker) {
        this.caretaker = caretaker;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public List<ReportMessage> getMessages() {
        return messages;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(LocalDateTime inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public int getPhotosSize() {
        return photosSize;
    }

    public int getMAXMESSAGES() {
        return MAXMESSAGES;
    }

    public int getMINTEXTLENGTH() {
        return MINTEXTLENGTH;
    }

    public int getMAXTEXTLENGTH() {
        return MAXTEXTLENGTH;
    }

    public int getMAXPHOTOSIZE() {
        return MAXPHOTOSIZE;
    }

    public void setPhotosSize(int photosSize) {
        this.photosSize = photosSize;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report report)) return false;
        return Objects.equals(getId(), report.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", caretakerId=" + caretaker.getId() +
                ", caretakerName=" + caretaker.getName() +
                ", petId=" + pet.getId() +
                ", petName=" + pet.getName() +
                ", messagesSize=" + messages.size() +
                ", creationTime=" + creationTime +
                ", inspectionTime=" + inspectionTime +
                ", status=" + status +
                ", photosSize=" + photosSize +
                '}';
    }
}
