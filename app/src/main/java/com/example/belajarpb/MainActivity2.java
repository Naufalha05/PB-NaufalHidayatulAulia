package com.example.belajarpb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.belajarpb.Models.UserDetails;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "MainActivity2";

    Button signUpBtn, backBtn;
    TextInputEditText usernameSignUp, emailPengguna, passwordSignUp, nimPengguna;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance(); //yg bawah iki tempel
        databaseReference = FirebaseDatabase.getInstance("https://belajarpb-05-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        signUpBtn = findViewById(R.id.buttonSignUp);
        usernameSignUp = findViewById(R.id.usernames2);
        emailPengguna = findViewById(R.id.email2);
        passwordSignUp = findViewById(R.id.password2);
        nimPengguna = findViewById(R.id.nim2);

        backBtn = findViewById(R.id.buttonBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });
        signUpBtn.setOnClickListener(view -> {
            String username = usernameSignUp.getText().toString().trim();
            String email = emailPengguna.getText().toString().trim();
            String password = passwordSignUp.getText().toString().trim();
            String NIM = nimPengguna.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                usernameSignUp.setError("Masukkan Username!");
                usernameSignUp.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                emailPengguna.setError("Masukkan Email!");
                emailPengguna.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordSignUp.setError("Masukkan Password!");
                passwordSignUp.requestFocus();
                return;
            }
            if (password.length() < 6) {
                passwordSignUp.setError("Password minimal 6 karakter!");
                passwordSignUp.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(NIM)) {
                nimPengguna.setError("Masukkan NIM!");
                nimPengguna.requestFocus();
                return;
            }
            registerUser(username, email, password, NIM);
        });
    }

    private void registerUser(String username, String email, String password, String NIM) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fUser = mAuth.getCurrentUser();
                        if (fUser != null) {
                            String uid = fUser.getUid();
                            UserDetails userDetails = new UserDetails(uid, username, email, password, NIM);
                            databaseReference.child(uid).setValue(userDetails)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            fUser.sendEmailVerification()
                                                    .addOnCompleteListener(emailTask -> {
                                                        if (emailTask.isSuccessful()) {
                                                            Toast.makeText(MainActivity2.this, "Registrasi berhasil! Periksa email Anda untuk verifikasi.", Toast.LENGTH_LONG).show();
                                                            Log.d(TAG, "Registrasi berhasil untuk: " + username);
                                                            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(MainActivity2.this, "Gagal mengirim email verifikasi.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        } else {
                                            Log.e(TAG, "Gagal menyimpan data ke database: " + dbTask.getException().getMessage());
                                            Toast.makeText(MainActivity2.this, "Gagal menyimpan data, coba lagi.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(MainActivity2.this, "Email " + email + " sudah digunakan!", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Registrasi gagal: Email " + email + " sudah digunakan.");
                        } else {
                            Log.e(TAG, "Registrasi gagal: " + task.getException().getMessage());
                            Toast.makeText(MainActivity2.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
