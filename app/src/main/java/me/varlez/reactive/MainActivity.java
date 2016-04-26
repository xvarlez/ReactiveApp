package me.varlez.reactive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private Subscription longOpSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textview);

        final Observable longOpObservable = Observable.create(subscriber -> {
            try {
                subscriber.onNext(longRunningOperation());
            } catch (InterruptedException e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        if (button != null) {
            button.setOnClickListener(view -> {
                view.setEnabled(false);
                textView.setText(R.string.doing_stuff);
                longOpSubscription = longOpObservable.subscribe(
                        value -> textView.setText(value.toString()),
                        error -> Log.e("ReactiveApp", "Error !"),
                        () -> view.setEnabled(true)
                );
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (longOpSubscription != null && !longOpSubscription.isUnsubscribed()) {
            longOpSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    public String longRunningOperation() throws InterruptedException {
        Thread.sleep(2000);
        return "Complete !";
    }
}
