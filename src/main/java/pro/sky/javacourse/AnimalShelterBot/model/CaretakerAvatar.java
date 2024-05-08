package pro.sky.javacourse.AnimalShelterBot.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Objects;
@Entity
@Table(name = "caretaker_avatar")
public class CaretakerAvatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_path")
    private String filePath ;
    @Column(name = "media_type")
    private String mediaType;
    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "data")
    @Lob
    private byte[] data;

    @OneToOne
    private Caretaker caretaker;

    public CaretakerAvatar() {
    }

    public CaretakerAvatar(Long id, String filePath, String mediaType, long fileSize, Caretaker caretaker) {
        this.id = id;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.fileSize = fileSize;
        this.caretaker = caretaker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CaretakerAvatar avatar = (CaretakerAvatar) o;
        return fileSize == avatar.fileSize && Objects.equals(id, avatar.id) && Objects.equals(filePath, avatar.filePath) && Objects.equals(mediaType, avatar.mediaType) && Arrays.equals(data, avatar.data) && Objects.equals(caretaker, avatar.caretaker);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filePath, mediaType, fileSize, caretaker);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", fileSize=" + fileSize +
                ", data=" + Arrays.toString(data) +
                ", student=" + caretaker +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public Caretaker getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(Caretaker caretaker) {
        this.caretaker = caretaker;
    }
}
