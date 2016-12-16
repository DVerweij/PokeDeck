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

/*This is the activity that displays the card information, including it's picture*/

public class CardActivity extends AppCompatActivity {
    //private card and deck objects
    private Deck deck;
    private Card card;

    //global view variables;
    private Button logButton;
    Button addButton;
    Button deckButton;
    ListView details;
    ArrayAdapter<String> detailAdapter;
    TextView usernameView;
    TextView nameView;
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
            //It will reload the page when rotating, but no loss of variables
            card = (Card) savedInstanceState.getSerializable("card");
            setUpCardProfile();
        }
        setUpDatabase();
        checkLogin();
        //If coming from the random generated Hand or Deck activity, turn off signIn, add and deckButton
        String activityString = getIntent().getStringExtra("Activity");
        if (activityString.equals("Hand") || activityString.equals("Deck")) {
            turnOffButtons();
        }
    }

    //function that turns off certain buttons
    private void turnOffButtons() {
        addButton = (Button) findViewById(R.id.button4);
        deckButton = (Button) findViewById(R.id.button6);
        logButton.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.INVISIBLE);
        deckButton.setVisibility(View.INVISIBLE);
    }

    //function which setsup the database to retrieve the deck object from
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
        //save card object so at rotation, it doesn't disappear
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
        //Like in the main activity, checks if user is logged in and changes views accordingly
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
                    //To hold the card and send it back later, only way to prevent crashing
                    goToLogin.putExtra("card", card);
                    startActivity(goToLogin);
                    finish();
                } else {
                    mAuth.signOut();
                }
            }
        });
    }

    //function which sets up the card information for use in the views
    private void setUpCardProfile() {
        cardImage = (ImageView) findViewById(R.id.imageView);
        CardSyncTask task = new CardSyncTask(this);
        task.execute(card);
        setUpDetails();
    }


    //This function sets the listView and textView after having attainted the card information
    private void setUpDetails() {
        details = (ListView) findViewById(R.id.detailList);
        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(card.getName());
        detailAdapter = new ArrayAdapter<String>(this, simple_list_item_1, card.getDetails());
        details.setAdapter(detailAdapter);

    }

    //function called from the Asynctasks to put picture in the imageView on the left
    public void setBitmap(Bitmap picture) {
        cardImage.setImageBitmap(picture);
    }

    //onclick function which goes from this activity to the deckActivity
    public void goToDeckList(View view) {
        //Only logged in users get to access this list
        if (mAuth.getCurrentUser() != null) {
            Intent deckList = new Intent(this, DeckActivity.class);
            deckList.putExtra("Activity", "Card");
            startActivity(deckList);
        } else {
            Toast notAUser = Toast.makeText(this, "Have to be logged in", Toast.LENGTH_SHORT);
            notAUser.show();
        }
    }

    //onclick function which adds the card in the activity to the user's deck
    public void addToDeckList(View view) {
        //if there is already a deck (read from the database), add the card to it
        if (deck != null) {
            //addCard returns a boolean to make sure this activity knows if the add was successful
            boolean wasAdded = deck.addCard(card);
            //if the card wasn't added, the toasts tell the user why
            if (!wasAdded) {
                //The deck size may be 60 which means no card can be added
                if (deck.deckSize == 60) {
                    Toast deckFull = Toast.makeText(this, "Deck is full", Toast.LENGTH_SHORT);
                    deckFull.show();
                //Either there needs to be a basic pokemon added or there's too many of this card
                //in the deck
                } else {
                    Toast tooManyOfCard = Toast.makeText(this,
                            "Cannot add any more of this card", Toast.LENGTH_SHORT);
                    tooManyOfCard.show();
                }
            } else {
                //if it was added, update the database with the changed deck
                putInDatabase();
            }
        } else {
            //if there was no deck yet, make a new deck and put it in the database
            deck = new Deck(card);
            putInDatabase();
        }
    }

    //This function puts the deck in the database iff the user is logged in
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
