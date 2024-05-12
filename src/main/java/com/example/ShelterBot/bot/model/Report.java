package com.example.ShelterBot.bot.model;

import com.example.ShelterBot.bot.enumm.StatusReport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "\"report\"")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "photo")
    @Lob
    private byte[] photo;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusReport statusReport;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    public Report(String text, LocalDateTime dateTime, byte[] photo, StatusReport statusReport, User user) {
        this.text = text;
        this.dateTime = dateTime;
        this.photo = photo;
        this.statusReport = statusReport;
        this.user = user;
    }

    public Report() {
    }

    public StatusReport getStatusReport() {
        return statusReport;
    }

    public void setStatusReport(StatusReport statusReport) {
        this.statusReport = statusReport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    @JsonIgnore
    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Objects.equals(text, report.text) && Objects.equals(dateTime, report.dateTime) && Arrays.equals(photo, report.photo) && statusReport == report.statusReport && Objects.equals(user, report.user);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, text, dateTime, statusReport, user);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", dateTime=" + dateTime +
                ", photo=" + Arrays.toString(photo) +
                ", statusReport=" + statusReport +
                ", user=" + user +
                '}';
    }
}

