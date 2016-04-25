package me.varlez.reactive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textview);

        final Observable longOpObservable = Observable.create(subscriber -> {
            subscriber.onNext(longRunningOperation());
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        if (button != null) {
            button.setOnClickListener(view -> {
                view.setEnabled(false);
                textView.setText(R.string.doing_stuff);
                longOpObservable.subscribe(
                        value -> textView.setText(value.toString()),
                        error -> Log.e("ReactiveApp", "Error !"),
                        () -> view.setEnabled(true)
                );
            });
        }
    }

    public String longRunningOperation() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // error
        }
        return "Complete !";
    }
}
