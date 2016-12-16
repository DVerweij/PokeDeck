package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*The activity where a user can sign up, sign in or choose to continue using the app not logged in*/

public class LoginActivity extends AppCompatActivity {
    //The views in the activity
    private EditText email;
    private EditText password;

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase deckDatabase;
    private DatabaseReference deckRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("IN", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("OUT", "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    //The signup function, will fail if you put in a already existing e-mailaddress
    public void signUp(View view) {
        String attemptEmail = email.getText().toString().trim();
        if (attemptEmail.length() == 0) {
            Toast noEmail = Toast.makeText(this, "No email input", Toast.LENGTH_SHORT);
            noEmail.show();
        } else if (!attemptEmail.contains("@")) {
            Toast notAnEmail = Toast.makeText(this, "Not an e-mail address", Toast.LENGTH_SHORT);
            notAnEmail.show();
        }
        String attemptPass = password.getText().toString();
        if (attemptPass.length() < 6) {
            Toast tooShort = Toast.makeText(this, "At least 6 characters, please",
                    Toast.LENGTH_SHORT);
            tooShort.show();
        } else if (!attemptPass.matches("^[a-zA-Z0-9!?]*$")) {
            Toast illegalChars = Toast.makeText(this,
                    "Can only contain letters, numbers or ? and !", Toast.LENGTH_LONG);
            illegalChars.show();
        } else {
            mAuth.createUserWithEmailAndPassword(attemptEmail, attemptPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("GOTEM", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "SignUp Failed",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                if (getIntent().getStringExtra("Activity").equals("Card")) {
                                    Intent goToCard = new Intent(getApplicationContext(), CardActivity.class);
                                    goToCard.putExtra("offline", "OFFLINE");
                                    goToCard.putExtra("card", getIntent().getSerializableExtra("card"));
                                    startActivity(goToCard);
                                    finish();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }
                        }
                    });
        }
    }

    //The signin function, will fail if e-mail address doesn't exist in the system
    public void signIn(View view) {
        String loginEmail = email.getText().toString().trim();
        if (loginEmail.length() == 0) {
            Toast noEmail = Toast.makeText(this, "No email input", Toast.LENGTH_SHORT);
            noEmail.show();
        }
        String loginPass = password.getText().toString();
        if (loginPass.length() == 0) {
            Toast noPass = Toast.makeText(this, "No password input", Toast.LENGTH_SHORT);
            noPass.show();
        }
        mAuth.signInWithEmailAndPassword(loginEmail, loginPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("LOGGEDIN", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("LOGFAILED", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "SignIn Failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (getIntent().getStringExtra("Activity").equals("Card")) {
                                Intent goToCard = new Intent(getApplicationContext(), CardActivity.class);
                                goToCard.putExtra("offline", "OFFLINE");
                                goToCard.putExtra("card", getIntent().getSerializableExtra("card"));
                                startActivity(goToCard);
                                finish();
                            } else {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }
                    }
                });
    }

    //offlineMode is a function which allows a user to forgo loggin in and make use of the app
    // without having logged in
    public void offlineMode(View view) {
        if (getIntent().getStringExtra("Activity").equals("Card")) {
            Intent goToCard = new Intent(this, CardActivity.class);
            goToCard.putExtra("offline", "OFFLINE");
            goToCard.putExtra("card", getIntent().getSerializableExtra("card"));
            startActivity(goToCard);
            finish();
        } else {
            Intent goToMain = new Intent(this, MainActivity.class);
            goToMain.putExtra("offline", "OFFLINE");
            startActivity(goToMain);
            finish();
        }
    }
}
