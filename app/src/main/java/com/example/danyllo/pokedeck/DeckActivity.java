package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DeckActivity extends AppCompatActivity {
    //Globals
    public Deck deck;

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //private StorageReference myDeckStorage;
    private FirebaseDatabase deckDatabase;
    private DatabaseReference deckRef;


    //Views
    private TextView usernameView;
    private TextView ratingView;
    private Button logButton;
    private ListView deckList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);
        checkLogin();
        setUpDatabase();
    }

    private void setRating() {
        ratingView = (TextView) findViewById(R.id.deckRating);
        ratingView.setText("This deck scores a " + String.valueOf(deck.getAvgRating()));
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
                    deck = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(Deck.class);
                    showDeck();
                    setRating();
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
        deckRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    deck = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(Deck.class);
                    if (getIntent().getStringExtra("Activity").equals("Hand")) {
                        addRating(getIntent().getIntExtra("rating", 0));
                    }
                    showDeck();
                } catch (NullPointerException n) {
                    n.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    private void addRating(int rating) {
        deck.rating += rating;
        deck.ratingCount++;
        deck.avgRating = (double) deck.rating / (double) deck.ratingCount;
        FirebaseUser user = mAuth.getCurrentUser();
        deckRef.child(user.getUid()).setValue(deck);
    }

    private void checkLogin() {
        //Courtesy of: http://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        usernameView = (TextView) findViewById(R.id.username);
        logButton = (Button) findViewById(R.id.signout);
        String viewString = "Logged in as: " + user.getEmail();
        usernameView.setText(viewString);
        logButton.setText("Sign Out");
        //click listeners for the button
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void showDeck() {
        deckList = (ListView) findViewById(R.id.deckList);
        //Log.d("ISFILLED", deck.getCardList().get(0).getName());
        deckList.setAdapter(new DeckAdapter(this, deck.getCardList()));
        deckList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deck.deleteCard(deck.getCard(position));
                //saveDeckToStorage();
                //deckRef.setValue(deck);
                deckList.setAdapter(new DeckAdapter(getApplicationContext(), deck.getCardList()));
                return true;
            }
        });
    }


    public void generateHand(View view) {
        Log.d("SIZE", String.valueOf(deck.deckSize));
        if (deck.deckSize != 60) {
            Toast toast = Toast.makeText(this, "Deck size not 60 yet", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Intent goGenerateHand = new Intent(this, HandActivity.class);
            goGenerateHand.putExtra("deck", deck);
            startActivity(goGenerateHand);
            finish();
        }
    }

    public void clearDeck(View view) {
        deck = new Deck();
        deckRef.child(mAuth.getCurrentUser().getUid()).setValue(deck);
    }
}
