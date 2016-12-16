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
    private ListView deckList;
    private Button generateButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);
        checkLogin();
        setButtons();
        setUpDatabase();
        setDeckFromActivity();
    }

    //This function sets the buttons and makes them invisible
    private void setButtons() {
        generateButton = (Button) findViewById(R.id.button3);
        clearButton = (Button) findViewById(R.id.button5);
        generateButton.setVisibility(View.INVISIBLE);
        clearButton.setVisibility(View.INVISIBLE);

    }

    //function which sets the deck back after having attained a new rating from HandActivity
    private void setDeckFromActivity() {
        if (getIntent().getStringExtra("Activity").equals("Hand")) {
            deck = (Deck) getIntent().getSerializableExtra("rating");
            deckRef.child(mAuth.getCurrentUser().getUid()).setValue(deck);
        }
    }

    private void setRating() {
        TextView ratingView = (TextView) findViewById(R.id.deckRating);
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
        TextView usernameView = (TextView) findViewById(R.id.username);
        Button logButton = (Button) findViewById(R.id.signout);
        //Set data on the logged-in user
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
        deckList.setAdapter(new CardsAdapter(this, deck.getCardList()));
        //Once the deck is loaded, I make the buttons visible again
        generateButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
        deckList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deck.deleteCard(deck.getCard(position));
                deckList.setAdapter(new CardsAdapter(getApplicationContext(), deck.getCardList()));
                return true;
            }
        });
        deckList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Card card = deck.getCard(position);
                Intent goToCard = new Intent(getApplicationContext(), CardActivity.class);
                goToCard.putExtra("card", card);
                goToCard.putExtra("Activity", "Deck");
                startActivity(goToCard);
            }
        });
    }


    public void generateHand(View view) {
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

    public void goToMainPage(View view) {
        Intent mainPage = new Intent(this, MainActivity.class);
        mainPage.putExtra("Activity", "Deck");
        startActivity(mainPage);
        finish();
    }
}
