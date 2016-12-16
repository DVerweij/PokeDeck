package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/*The activity which is opened when the user wants to generate a random hand*/

public class HandActivity extends AppCompatActivity {
    //Views
    private ListView handListView;
    private EditText ratingET;
    
    //Globals
    private Deck deckList;
    private ArrayList<Card> hand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand);
        deckList = (Deck) getIntent().getSerializableExtra("deck");
        handListView = (ListView) findViewById(R.id.handList);
        ratingET = (EditText) findViewById(R.id.giveRating);
        generateRandomHand();
        setHandInView();
    }

    //Function which sets the generated hand in the listview and adds click listeners so it can
    //go to the CardActivity for the card clicked
    private void setHandInView() {
        handListView.setAdapter(new CardsAdapter(this, hand));
        handListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Card card = hand.get(position);
                Intent goToCard = new Intent(getApplicationContext(), CardActivity.class);
                goToCard.putExtra("card", card);
                goToCard.putExtra("Activity", "Hand");
                startActivity(goToCard);
            }
        });
    }

    //Function which generates the random hand
    private void generateRandomHand() {
        //duplication of list so it won't overwrite the actual deckList
        ArrayList<Card> duplicateList = new ArrayList<Card>(deckList.getCardList());
        //list where the seven card hand will be put
        ArrayList<Card> generatedHand = new ArrayList<Card>();
        Random rand = new Random();
        //This for-loop takes 7 random cards from the duplicated decklist
        // and puts in the generatedHand
        for (int i = 0; i < 7; i++) {
            int randNum = rand.nextInt(duplicateList.size());
            Card randomCard = duplicateList.get(randNum);
            duplicateList.remove(randomCard);
            generatedHand.add(randomCard);
        }
        //Rule: the first hand needs to have a basic pokemon or a mulligan occurs
        if (containsBasicPokemon(generatedHand)) {
            hand = generatedHand;
        //So if there's no basic pokemon, the hand is recursively regenerated
        } else {
            generateRandomHand();
        }
    }

    //function which checks if the hand contains a basic pokemon
    private boolean containsBasicPokemon(ArrayList<Card> generatedHand) {
        for (int i = 0; i < generatedHand.size(); i++) {
            if (generatedHand.get(i).isBasicPokemon()) {
                return true;
            }
        }
        return false;
    }

    //onClick function which allows the user to put in a random integer between 0 and 10
    // and give that as a score to the hand which is later added to the deck
    public void giveRating(View view) {
        String input = ratingET.getText().toString().trim();
        //To hide the soft keyboard after click
        ratingET.onEditorAction(EditorInfo.IME_ACTION_DONE);
        Scanner numberScan = new Scanner(input);
        //No empty inputs
        if (input.length() == 0) {
            Toast noInput = Toast.makeText(this, "No input", Toast.LENGTH_SHORT);
            noInput.show();
        //has to be an int
        } else if (!numberScan.hasNextInt()) {
            Toast noInt = Toast.makeText(this, "Please put in an integer", Toast.LENGTH_SHORT);
            noInt.show();
        } else {
            int num = numberScan.nextInt();
            //The int cannot be below 0 or above 10
            if (num < 0 || num > 10) {
                Toast wrongNum = Toast.makeText(this, "Between 0 and 10 please", Toast.LENGTH_SHORT);
                wrongNum.show();
            } else {
                addRating(num);
                Intent backToDeck = new Intent(this, DeckActivity.class);
                backToDeck.putExtra("rating", deckList);
                backToDeck.putExtra("Activity", "Hand");
                startActivity(backToDeck);
                finish();
            }
        }
    }

    private void addRating(int number) {
        deckList.rating += number;
        deckList.ratingCount++;
        deckList.avgRating = (double) deckList.rating / (double) deckList.ratingCount;
    }
}
