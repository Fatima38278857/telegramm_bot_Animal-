package pro.sky.javacourse.AnimalShelterBot.dto;

import java.util.Objects;

public class CaretakerAvatarDto {
    private Long id;

    private long fileSize;

    private String mediaType;

    private Long caretakerID;

    public CaretakerAvatarDto(Long id, long fileSize, String mediaType, Long caretakerID) {
        this.id = id;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.caretakerID = caretakerID;
    }

    public CaretakerAvatarDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public Long getCaretakerID() {
        return caretakerID;
    }

    public void setCaretakerID(Long caretakerID) {
        this.caretakerID = caretakerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaretakerAvatarDto avatarDTO = (CaretakerAvatarDto) o;
        return fileSize == avatarDTO.fileSize && Objects.equals(id, avatarDTO.id) && Objects.equals(mediaType, avatarDTO.mediaType) && Objects.equals(caretakerID, avatarDTO.caretakerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileSize, mediaType, caretakerID);
    }
}
