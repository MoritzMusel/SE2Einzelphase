package at.moritzmusel.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.Buffer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "RXANDROID";
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonabschicken).setOnClickListener(v -> {
            TextInputLayout inputfield_matnr = findViewById(R.id.textinput_matrikel);
            String matnr = String.valueOf(inputfield_matnr.getEditText().getText());
            observeNetCall(matnr);
        });
    }

    public void prime(View view) {
        StringBuilder primes = new StringBuilder();
        TextInputLayout inputfield_matnr = findViewById(R.id.textinput_matrikel);
        String matnr = String.valueOf(inputfield_matnr.getEditText().getText());
        for (int i = 0; i < matnr.length(); i++) {
            if (isPrime(Integer.parseInt(matnr.charAt(i) + "")))
                primes.append(matnr.charAt(i));
        }
        TextView textprime = (TextView)findViewById(R.id.textprimes);
        textprime.setText("Following primes found");
        TextView primeResponse = ((TextView)findViewById(R.id.primeNumbers));
        primeResponse.setText(((!primes.toString().equals("")) ? primes.toString() : "none"));
        primeResponse.setTextColor(getResources().getColor(R.color.purple_500));
        addTextFadeoutAnimation(primeResponse);
        addTextFadeoutAnimation(textprime, 4000);
    }

    private boolean isPrime(int n) {
        if (n <= 1) return true;
        for (int i = 2; i < n; i++)
            if (n % i == 0)
                return false;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    void observeNetCall(String matNr) {
        disposables.add(networkcall(matNr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "finished");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(String string) {
                        TextView textprimes = (TextView) findViewById(R.id.responseServer);
                        textprimes.setText(string);
                        textprimes.setTextColor(getResources().getColor(R.color.purple_500));
                        addTextFadeoutAnimation(textprimes);
                    }
                }));
    }

    static Observable<String> networkcall(String matNr) {
        return Observable.defer(() -> {
            Socket s = new Socket("se2-isys.aau.at", 53212);
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            os.writeBytes(matNr + "\n\r");
            os.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = in.readLine();
            return Observable.just(line);
        });
    }

    private void addTextFadeoutAnimation(TextView text){
        Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setStartOffset(3000);
        out.setDuration(1500);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                text.setText("");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        text.setAnimation(out);
    }
    private void addTextFadeoutAnimation(TextView text, int animationStartOffset){
        Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setStartOffset(animationStartOffset);
        out.setDuration(1500);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                text.setText("");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        text.setAnimation(out);
    }
}