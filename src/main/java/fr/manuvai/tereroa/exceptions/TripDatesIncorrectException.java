package fr.manuvai.tereroa.exceptions;

public class TripDatesIncorrectException extends RuntimeException {
    public TripDatesIncorrectException() {
        super("Les dates de votre voyage ne sont pas correctes");
    }
}
