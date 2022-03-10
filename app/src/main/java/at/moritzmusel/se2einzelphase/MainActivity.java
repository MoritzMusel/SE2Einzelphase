package at.moritzmusel.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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
                primes.append(matnr.charAt(i)).append(" ");
        }
        TextView textprimes = (TextView) findViewById(R.id.textprimes);
        textprimes.setText("Following primes found\n" + ((!primes.toString().equals("")) ? primes.toString() : "none"));
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
                    }
                }));
    }

    static Observable<String> networkcall(String matNr) {
        return Observable.defer(() -> {
            Socket s = new Socket("se2-isys.aau.at", 53212);
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            os.writeBytes(matNr + "\n");
            os.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = in.readLine();
            return Observable.just(line);
        });
    }
}