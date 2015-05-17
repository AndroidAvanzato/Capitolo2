package it.androidavanzato.rxlogin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;


public class MainActivity extends AppCompatActivity {

    private EditText userNameText;
    private EditText passwordText;
    private Button registerButton;

    private Subscription subscription = Subscriptions.empty();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameText = (EditText) findViewById(R.id.user_name);
        passwordText = (EditText) findViewById(R.id.pwd);
        registerButton = (Button) findViewById(R.id.register);
    }

    @Override protected void onResume() {
        super.onResume();

        Subscription s1 = Observable.combineLatest(
                WidgetObservable.text(userNameText, true).map(OnTextChangeEvent::text).map(TextUtils::isEmpty),
                WidgetObservable.text(passwordText, true).map(OnTextChangeEvent::text).map(TextUtils::isEmpty),
                (userNameEmpty, passwordEmpty) -> !userNameEmpty && !passwordEmpty
        ).subscribe(registerButton::setEnabled);

        Subscription s2 = ViewObservable
                .clicks(registerButton)
                .flatMap(e -> callServer().subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUi, this::updateAfterError);

        subscription = new CompositeSubscription(s1, s2);
    }

    @Override protected void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }

    private void updateAfterError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(this, "Server error " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void updateUi(Integer r) {
        Toast.makeText(this, "Server result " + r, Toast.LENGTH_SHORT).show();
    }

    private Observable<Integer> callServer() {
        return Observable.just(1).delay(1, TimeUnit.SECONDS);
    }
}
