package it.androidavanzato.rxlogin;

public class ValidationException extends RuntimeException {
    public ValidationException(String detailMessage) {
        super(detailMessage);
    }
}
