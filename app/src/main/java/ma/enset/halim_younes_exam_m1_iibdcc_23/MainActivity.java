package ma.enset.halim_younes_exam_m1_iibdcc_23;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    EditText username,password;
    ImageButton login;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=findViewById(R.id.usrname);
        password=findViewById(R.id.pswd);
        login=findViewById(R.id.btnLogin);
        sharedPreferences = getSharedPreferences("loginPref",
                MODE_PRIVATE);

    }
    public  void login(View view){
        if(sharedPreferences==null) sharedPreferences=getSharedPreferences("loginPref",MODE_PRIVATE);
        String n = username.getText().toString();
        String e = password.getText().toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emailKey", n);
        editor.putString("pwdKey", e);
        editor.commit();
        username.setText("");
        password.setText("");
        Intent intent = new Intent(MainActivity.this,IpFinderActivity.class);
        startActivity(intent);
    }
}