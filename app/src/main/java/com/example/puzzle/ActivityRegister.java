package com.example.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ActivityRegister extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText registerETFirstName, registerETLastName, registerETEmail, registerETPassword;
    private Button registerBTNRegister;
    private TextView registerLBLLogin;
    private FirebaseAuth fAuth;
    private ProgressBar registerProgressBar;
    private String userID;
    private DatabaseReference db;
    private DatabaseReference createPuzzles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        initViews();
        FacebookSdk.sdkInitialize(getApplicationContext());


        fAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference().child(getString(R.string.members));
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeScreenTabbed.class));
            finish();
        }


    }

    private void createUser(final String firstName, final String lastName, final String email, String password) {
        registerProgressBar.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // send verification link

                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ActivityRegister.this, getString(R.string.emailSent), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, getString(R.string.emailError) + e.getMessage());
                        }
                    });

                    Toast.makeText(ActivityRegister.this, R.string.userCreated, Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    createPuzzles = FirebaseDatabase.getInstance().getReference().child(getString(R.string.puzzlesDataBase)).child(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put(getString(R.string.firstNameKey), firstName);
                    user.put(getString(R.string.lastNamekey), lastName);
                    user.put(getString(R.string.emailKey), email);
                    user.put(getString(R.string.scoreKey), 0);
                    user.put(getString(R.string.currentUserID), userID);
                    db.child(userID).setValue(user);
                    user.clear();
                    user.put(getString(R.string.firstPuzzleText), "");
                    user.put(getString(R.string.secondPuzzleText), "");
                    user.put(getString(R.string.thirdPuzzleText), "");
                    user.put(getString(R.string.currentUserID), userID);
                    user.put(getString(R.string.firstPuzzleAnswer), "");
                    user.put(getString(R.string.secondPuzzleAnswer), "");
                    user.put(getString(R.string.thirdPuzzleAnswer), "");
                    createPuzzles.setValue(user);
                    startActivity(new Intent(getApplicationContext(), HomeScreenTabbed.class));

                } else {
                    Toast.makeText(ActivityRegister.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    registerProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {
        registerLBLLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                finish();
            }
        });

        registerBTNRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstName = registerETFirstName.getText().toString().trim();
                final String lastName = registerETLastName.getText().toString().trim();
                final String email = registerETEmail.getText().toString().trim();
                String password = registerETPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    registerETEmail.setError(getString(R.string.emailEmpty));
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    registerETPassword.setError(getString(R.string.passEmpty));
                    return;
                } else if (password.length() < 6) {
                    registerETPassword.setError(getString(R.string.passLength));
                    return;
                } else {
                    createUser(firstName, lastName, email, password);

                }
            }
        });
    }

    private void findViews() {
        registerETFirstName = findViewById(R.id.registerETFirstName);
        registerETLastName = findViewById(R.id.registerETLastName);
        registerETEmail = findViewById(R.id.registerETEmail);
        registerETPassword = findViewById(R.id.registerETPassword);
        registerBTNRegister = findViewById(R.id.registerBTNRegister);
        registerLBLLogin = findViewById(R.id.registerLBLLogin);
        registerProgressBar = findViewById(R.id.registerProgressBar);
    }
}