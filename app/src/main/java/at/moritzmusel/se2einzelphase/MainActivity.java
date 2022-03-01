package at.moritzmusel.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void prime(View view) {
        String primes = "";
        TextInputLayout inputfield_matnr = findViewById(R.id.textinput_matrikel);
        String matnr = String.valueOf(inputfield_matnr.getEditText().getText());
        for (int i = 0; i < matnr.length(); i++) {
            if(isPrime(Integer.parseInt(matnr.charAt(i)+"")))primes+=matnr.charAt(i)+" ";
        }
        TextView textprimes = (TextView) findViewById(R.id.textprimes);
        textprimes.setText("Following primes found\n"+((!primes.equals(""))?primes:"none"));
    }

    private boolean isPrime(int n) {
        if (n <= 1) return true;
        for (int i = 2; i < n; i++)
            if (n % i == 0)
                return false;
        return true;
    }
}