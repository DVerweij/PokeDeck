/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (C) 2016 Pokémon. (C) 1995-2016 Nintendo/Creatures Inc./GAME FREAK Inc.
 * API: pokemontcg.io
 *
 * The Pokémon TCG is owned by Nintendo, Creatures Inc. and GAME FREAK Inc.
 * */

package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* The activity that the app starts with. It handels the searching of the pokemon cards based on name
 Credit to: https://api.pokemontcg.io*/
public class MainActivity extends AppCompatActivity {
    //The views as global variables
    private EditText searchET;
    private ListView searchList;
    private TextView usernameView;
    private Button logButton;
    //The lists that will be filled by the searching of the cards
    //Tuple is a simple pair class (in this case of two Strings)
    private ArrayList<Card> cardList = new ArrayList<Card>();
    //private Map<String, Card> cardMap = new HashMap<String, Card>();

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogin();
        initialize();
    }

    //onStart and onStop will enable and disable the authstatelisteners
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

    //function which checks the login status of the user
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
                    //when not triggered by the continue offline button, the login screen will be
                    //seen from the user's perspective
                    if (getIntent().getStringExtra("offline") == null) {
                        // launch login activity
                        Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
                        goToLogin.putExtra("Activity", "Main");
                        startActivity(goToLogin);
                        finish();
                    }
                }
            }
        };
        //the activity will display the login status to the user
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
                //if not logged in, clicking the button will bring the user to the sign-in screen
                if (logButton.getText().toString().equals("Sign In")) {
                    Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
                    goToLogin.putExtra("Activity", "Main");
                    startActivity(goToLogin);
                    finish();
                //otherwise: it will sign the user out
                } else {
                    mAuth.signOut();
                }
            }
        });
    }


    //Initialization function which sets up the views and the adapter for the ListView
    private void initialize() {
        searchET = (EditText) findViewById(R.id.editText);
        searchList = (ListView) findViewById(R.id.cardList);
        searchList.setAdapter(new CardsAdapter(this, cardList));
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Card card = cardList.get(position);
                goToCardPage(card);
            }
        });
    }

    //Function that on listView click goes to the Card found at the listView's child in question
    private void goToCardPage(Card card) {
        Intent cardActivity = new Intent(this, CardActivity.class);
        //This part will let the activity know from which activity it was triggered
        cardActivity.putExtra("Activity", "Main");
        cardActivity.putExtra("card", card); //Gets Card object from ID
        startActivity(cardActivity);
    }

    //The search function
    public void searchCard(View view) {
        //Trim for better effect
        String name = searchET.getText().toString().trim();
        //To hide the soft keyboard after click
        searchET.onEditorAction(EditorInfo.IME_ACTION_DONE);
        //Get the list of cards from the input
        if (name.length() == 0) {
            Toast noInput = Toast.makeText(this, "You haven't given input", Toast.LENGTH_SHORT);
            noInput.show();
        } else {
            SearchSyncTask task = new SearchSyncTask(this, "https://api.pokemontcg.io/v1/cards");
            task.execute(name);
        }
    }

    //Function called from SearchSyncTask which sets the data in the structures
    public void setData(ArrayList<Card> cards) {
        cardList = cards;
        //cardMap = mapWithCards;
        searchList.setAdapter(new CardsAdapter(this, cardList));
    }


    //onclick function which goes from main to the decklist
    public void goToDeckList(View view) {
        if (mAuth.getCurrentUser() != null) {
            Intent deckList = new Intent(this, DeckActivity.class);
            deckList.putExtra("Activity", "Main");
            startActivity(deckList);
            finish();
        //Design choice: No decklist if you're not logged in
        } else {
            Toast notAUser = Toast.makeText(this, "Have to be logged in", Toast.LENGTH_SHORT);
            notAUser.show();
        }
    }

}
