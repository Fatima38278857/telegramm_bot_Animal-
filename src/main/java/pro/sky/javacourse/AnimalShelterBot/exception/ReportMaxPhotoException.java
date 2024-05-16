package pro.sky.javacourse.AnimalShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
    public class ReportMaxPhotoException extends RuntimeException {
        public ReportMaxPhotoException() {
            super();
        }

    public ReportMaxPhotoException(String message) {
        super(message);
    }
    }