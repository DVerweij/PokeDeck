package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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

    private void setHandInView() {
        handListView.setAdapter(new DeckAdapter(this, hand));
        handListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Card card = hand.get(position);
                Intent goToCard = new Intent(getApplicationContext(), CardActivity.class);
                goToCard.putExtra("card", card);
                startActivity(goToCard);
            }
        });
    }

    private void generateRandomHand() {
        //duplication of list so it won't overwrite the actual deckList
        ArrayList<Card> duplicateList = new ArrayList<Card>(deckList.getCardList());
        //list where the seven card hand will be put
        ArrayList<Card> generatedHand = new ArrayList<Card>();
        Random rand = new Random();
        for (int i = 0; i < 7; i++) {
            int randNum = rand.nextInt(duplicateList.size());
            Card randomCard = duplicateList.get(randNum);
            duplicateList.remove(randomCard);
            generatedHand.add(randomCard);
        }
        if (containsBasicPokemon(generatedHand)) {
            hand = generatedHand;
        } else {
            generateRandomHand();
        }
    }

    private boolean containsBasicPokemon(ArrayList<Card> generatedHand) {
        for (int i = 0; i < generatedHand.size(); i++) {
            if (generatedHand.get(i).isBasicPokemon()) {
                return true;
            }
        }
        return false;
    }

    public void giveRating(View view) {
        String input = ratingET.getText().toString().trim();
        Scanner numberScan = new Scanner(input);
        if (input.length() == 0) {
            Toast noInput = Toast.makeText(this, "No input", Toast.LENGTH_SHORT);
            noInput.show();
        } else if (!numberScan.hasNextInt()) {
            Toast noInt = Toast.makeText(this, "Please put in an integer", Toast.LENGTH_SHORT);
            noInt.show();
        } else {
            int num = numberScan.nextInt();
            if (num < 0 || num > 10) {
                Toast wrongNum = Toast.makeText(this, "Between 0 and 10 please", Toast.LENGTH_SHORT);
                wrongNum.show();
            } else {
                Intent backToDeck = new Intent(this, DeckActivity.class);
                backToDeck.putExtra("rating", num);
                backToDeck.putExtra("Activity", "Hand");
                startActivity(backToDeck);
                finish();
            }
        }
    }
}
