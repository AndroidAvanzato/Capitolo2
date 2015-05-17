package it.androidavanzato.rxlogin;

public class ValidateResponse {
    private boolean valid;

    public ValidateResponse(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }
}
