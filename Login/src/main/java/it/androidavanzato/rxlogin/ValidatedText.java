package it.androidavanzato.rxlogin;

import rx.android.widget.OnTextChangeEvent;

public class ValidatedText {
    private final CharSequence text;

    private final boolean valid;

    public ValidatedText(CharSequence text, boolean valid) {
        this.text = text;
        this.valid = valid;
    }

    public ValidatedText(OnTextChangeEvent event) {
        text = event.text();
        valid = true;
    }

    public CharSequence getText() {
        return text;
    }

    public boolean isValid() {
        return valid;
    }

    public ValidatedText checkNotEmpty() {
        return new ValidatedText(text, valid && text.length() > 0);
    }
}
