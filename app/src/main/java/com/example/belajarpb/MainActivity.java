package com.example.belajarpb;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TextInputEditText username, password;
    CheckBox checkboxes;
    Button btlogin;
    TextView forgotPass, signUpp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.usernames);
        password = findViewById(R.id.password);
        checkboxes = findViewById(R.id.checkboxes);
        btlogin = findViewById(R.id.buttonlogin);
        forgotPass = findViewById(R.id.forgotPassword);
        signUpp = findViewById(R.id.signUp);
        btlogin.setOnClickListener(view -> loginUser());
        signUpp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            username.setError("Masukkan email!");
            username.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            password.setError("Masukkan password!");
            password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                Toast.makeText(MainActivity.this, "Login berhasil! Selamat datang, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Login berhasil: " + user.getEmail());
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Silakan verifikasi email Anda sebelum login!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Login gagal: " + task.getException().getMessage());
                    }
                });
    }
}