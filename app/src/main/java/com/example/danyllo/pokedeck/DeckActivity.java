package com.example.danyllo.pokedeck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DeckActivity extends AppCompatActivity {
    //Globals
    public Deck deck = new Deck();

    //Views
    private ListView deckList = (ListView) findViewById(R.id.deckList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);
        showDeck();
    }

    private void showDeck() {
        deckList.setAdapter(new DeckAdapter(this, deck.getCardList()));
        deckList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deck.deleteCard(deck.getCard(position));
                deckList.setAdapter(new DeckAdapter(getApplicationContext(), deck.getCardList()));
                return false;
            }
        });
    }
}
