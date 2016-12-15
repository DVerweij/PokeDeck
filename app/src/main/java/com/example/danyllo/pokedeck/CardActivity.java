package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.R.layout.simple_list_item_1;

public class CardActivity extends AppCompatActivity {
    private Deck deck;
    private Card card;
    private TextView usernameView;
    private Button logButton;
    private ListView details;
    private ArrayAdapter<String> detailAdapter;
    private TextView nameView;
    private ImageView cardImage;

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase deckDatabase;
    private DatabaseReference deckRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        if (savedInstanceState == null) {
            card = (Card) getIntent().getSerializableExtra("card");
            setUpCardProfile();
        } else {
            Log.d("GOT HERE", savedInstanceState.toString());
            card = (Card) savedInstanceState.getSerializable("card");
            Log.d("CARD", card.toString());
            setUpCardProfile();
        }
        setUpDatabase();
        checkLogin();

    }

    private void setUpDatabase() {
        deckDatabase = FirebaseDatabase.getInstance();
        deckRef = deckDatabase.getReference("deck");
        // Read from the database
        deckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    Log.d("BOI", "BOI");
                    deck = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(Deck.class);
                } catch (NullPointerException n) {
                    n.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                error.toException().printStackTrace();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("card", card);
        super.onSaveInstanceState(savedInstanceState);
    }

    //Authentication function
    private void checkLogin() {
        //Courtesy of: http://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        usernameView = (TextView) findViewById(R.id.username);
        logButton = (Button) findViewById(R.id.signout);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //when not triggered by the continue offline button
                    if (getIntent().getStringExtra("offline") == null) {
                        // launch login activity
                        startActivity(new Intent(CardActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        };
        if (user != null) {
            String viewString = "Logged in as: " + user.getEmail();
            usernameView.setText(viewString);
            logButton.setText("Sign Out");
        } else {
            usernameView.setText("Not logged in");
            logButton.setText("Sign In");
        }
        //click listeners for the button
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logButton.getText().toString().equals("Sign In")) {
                    Intent goToLogin = new Intent(CardActivity.this, LoginActivity.class);
                    goToLogin.putExtra("Activity", "Card");
                    //To hold the card and send it back later
                    goToLogin.putExtra("card", card);
                    startActivity(goToLogin);
                    finish();
                } else {
                    mAuth.signOut();
                }
            }
        });
    }


    private void setUpCardProfile() {
        cardImage = (ImageView) findViewById(R.id.imageView);
        CardSyncTask task = new CardSyncTask(this);
        task.execute(card);
        setUpDetails();
    }

    private void setUpDetails() {
        details = (ListView) findViewById(R.id.detailList);
        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(card.getName());
        detailAdapter = new ArrayAdapter<String>(this, simple_list_item_1, card.getDetails());
        details.setAdapter(detailAdapter);

    }

    public void setBitmap(Bitmap picture) {
        Log.d("BITMAP", "HERE");
        cardImage.setImageBitmap(picture);
    }

    public void goToDeckList(View view) {
        if (mAuth.getCurrentUser() != null) {
            Intent deckList = new Intent(this, DeckActivity.class);
            startActivity(deckList);
        } else {
            Toast notAUser = Toast.makeText(this, "Have to be logged in", Toast.LENGTH_SHORT);
            notAUser.show();
        }
    }

    public void addToDeckList(View view) {
        if (deck != null) {
            boolean wasAdded = deck.addCard(card);
            Log.d("ADDED", Boolean.toString(wasAdded));
            if (!wasAdded) {
                if (deck.deckSize == 60) {
                    Toast deckFull = Toast.makeText(this, "Deck is full", Toast.LENGTH_SHORT);
                    deckFull.show();
                } else {
                    Toast tooManyOfCard = Toast.makeText(this, "Cannot add any more of this card", Toast.LENGTH_SHORT);
                    tooManyOfCard.show();
                }
            } else {
                Log.d("GOT HERE", "GOT HERE");
                putInDatabase();
            }
        } else {
            deck = new Deck(card);
            putInDatabase();
        }
    }

    private void putInDatabase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            deckRef.child(user.getUid()).setValue(deck);
        } else {
            Toast notAUser = Toast.makeText(this, "Have to be logged in", Toast.LENGTH_SHORT);
            notAUser.show();
        }
    }
}
