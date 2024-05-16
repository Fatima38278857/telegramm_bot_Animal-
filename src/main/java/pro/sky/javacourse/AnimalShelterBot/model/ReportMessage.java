package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Entity(name = "report_messages")
public class ReportMessage {
    @Id
    private Integer id;
    Long chatId;
    String firstName;
    String lastName;
    String userName;
    Integer date;
    @Column(name = "text", length = 1024)
    String text;
    int fileSize;
    String filePath;
    String mediaGroupId;
    @Column(name = "caption", length = 1024)
    String caption;
    @ManyToOne
    @JoinColumn(name = "report_id")
    Report report;

    public ReportMessage() {
    }

    public ReportMessage(Integer id) {
        this.id = id;
    }

    public ReportMessage(Integer id, Long chatId, String firstName, String lastName, Integer date, int fileSize) {
        this.id = id;
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.fileSize = fileSize;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMediaGroupId() {
        return mediaGroupId;
    }

    public void setMediaGroupId(String mediaGroupId) {
        this.mediaGroupId = mediaGroupId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportMessage that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    private LocalDateTime toLocalDateTime(Integer date) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(date), ZoneId.systemDefault());
    }

    @Override
    public String toString() {
        return "ReportMessage{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", date=" + date +
                ", dateConverted=" + toLocalDateTime(date) +
                ", text='" + text + '\'' +
                ", fileSize=" + fileSize +
                ", filePath='" + filePath + '\'' +
                ", mediaGroupId=" + mediaGroupId +
                ", caption='" + caption + '\'' +
                '}';
    }
}
