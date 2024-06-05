package fr.manuvai.tereroa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TripDatesIncorrectException extends RuntimeException {
    public TripDatesIncorrectException() {
        super("Les dates de votre voyage ne sont pas correctes");
    }
}
