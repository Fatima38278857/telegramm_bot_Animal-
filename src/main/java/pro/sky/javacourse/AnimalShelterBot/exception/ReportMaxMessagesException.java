package pro.sky.javacourse.AnimalShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
public class ReportMaxMessagesException extends RuntimeException {
    public ReportMaxMessagesException() {
        super();
    }

    public ReportMaxMessagesException(String message) {
        super(message);
    }
}
