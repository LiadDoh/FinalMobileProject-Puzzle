package com.example.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String TAG = "Auth";
    private LoginButton loginBTNFacebookLogin;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText loginETEmail, loginETPassword;
    private TextView loginLBLRegister;
    private Button loginBTNLogin;
    private ProgressBar loginProgressBar;
    private String userID;
    private AccessTokenTracker accessTokenTracker;
    public DatabaseReference db;
    private DatabaseReference createPuzzles;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CheckBox loginCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        findViews();
        loginBTNFacebookLogin.setPermissions(Collections.singletonList(EMAIL));
        db = FirebaseDatabase.getInstance().getReference().child(getString(R.string.members));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginBTNFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "OnError: " + exception.getMessage());
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    openProfile(user);
            }
        };

        loginBTNLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = loginETEmail.getText().toString().trim();
                String password = loginETPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    loginETEmail.setError(getString(R.string.emailEmpty));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    loginETPassword.setError(getString(R.string.passEmpty));
                    return;
                }

                if (password.length() < 6) {
                    loginETPassword.setError(getString(R.string.passLength));
                    return;
                }

                loginProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ActivityLogin.this, getString(R.string.userLoggedIn), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeScreenTabbed.class));
                            finish();
                        } else {
                            Toast.makeText(ActivityLogin.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            loginProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginLBLRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityRegister.class));
                finish();
            }
        });

        loginCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginCheckBox.isChecked())
                    loginETPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    loginETPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                token,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray array, GraphResponse response) {

                    }
                });

        request.executeAsync();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew) {
                                userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                createPuzzles = FirebaseDatabase.getInstance().getReference().child(getString(R.string.puzzlesDataBase)).child(userID);
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                Map<String, Object> user1 = new HashMap<>();
                                String faceBook_name = currentUser.getDisplayName();
                                assert faceBook_name != null;
                                String[] full_name = faceBook_name.split(" ");
                                user1.put(getString(R.string.firstNameKey), full_name[0]);
                                user1.put(getString(R.string.lastNamekey), full_name[1]);
                                user1.put(getString(R.string.emailKey), currentUser.getEmail());
                                user1.put(getString(R.string.currentUserID), userID);
                                user1.put(getString(R.string.facebookIDKey), token.getUserId());
                                user1.put(getString(R.string.scoreKey), 0);
                                db.child(userID).setValue(user1);
                                user1.clear();
                                user1.put(getString(R.string.firstPuzzleText), "");
                                user1.put(getString(R.string.secondPuzzleText), "");
                                user1.put(getString(R.string.thirdPuzzleText), "");
                                user1.put(getString(R.string.currentUserID), userID);
                                user1.put(getString(R.string.firstPuzzleAnswer), "");
                                user1.put(getString(R.string.secondPuzzleAnswer), "");
                                user1.put(getString(R.string.thirdPuzzleAnswer), "");
                                createPuzzles.setValue(user);
                            }
                            FirebaseUser fUser = mAuth.getCurrentUser();
                            openProfile(fUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(ActivityLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void openProfile(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(this, HomeScreenTabbed.class));
            finish();
        } else
            Toast.makeText(this, getString(R.string.pleaseSignIn), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    private void findViews() {
        loginBTNFacebookLogin = findViewById(R.id.loginBTNFacebookLogin);
        loginETEmail = findViewById(R.id.loginETEmail);
        loginETPassword = findViewById(R.id.loginETPassword);
        loginLBLRegister = findViewById(R.id.loginLBLRegister);
        loginBTNLogin = findViewById(R.id.loginBTNLogin);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        loginCheckBox = findViewById(R.id.loginCheckBox);
    }


}