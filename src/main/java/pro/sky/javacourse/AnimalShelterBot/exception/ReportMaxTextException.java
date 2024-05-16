package pro.sky.javacourse.AnimalShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
public class ReportMaxTextException extends RuntimeException {
    public ReportMaxTextException() {
        super();
    }

    public ReportMaxTextException(String message) {
        super(message);
    }
}
