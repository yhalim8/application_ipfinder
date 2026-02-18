package ma.enset.halim_younes_exam_m1_iibdcc_23;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.usrname);
        password = findViewById(R.id.pswd);
        sharedPreferences = getSharedPreferences("loginPref", MODE_PRIVATE);

        username.setText(sharedPreferences.getString("emailKey", ""));

        findViewById(R.id.btnLogin).setOnClickListener(view -> login());
    }

    private void login() {
        String enteredUsername = username.getText().toString().trim();
        String enteredPassword = password.getText().toString();

        if (enteredUsername.isEmpty()) {
            username.setError("Username is required");
            username.requestFocus();
            return;
        }

        if (enteredPassword.length() < 4) {
            password.setError("Password must contain at least 4 characters");
            password.requestFocus();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emailKey", enteredUsername);
        editor.putString("pwdKey", enteredPassword);
        editor.apply();

        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, IpFinderActivity.class));
    }
}
