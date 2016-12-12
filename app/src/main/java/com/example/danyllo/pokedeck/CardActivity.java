package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static android.R.layout.simple_list_item_1;

public class CardActivity extends AppCompatActivity {
    private Card card;
    private ListView details;
    private ArrayAdapter<String> detailAdapter;
    private TextView nameView;
    private ImageView cardImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        setUpCardProfile();
    }

    private void setUpCardProfile() {
        card = (Card) getIntent().getSerializableExtra("card");
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
        Intent deckList = new Intent(this, DeckActivity.class);
        startActivity(deckList);
    }

    public void addToDeckList(View view) {
        DeckActivity deckList = new DeckActivity();
        deckList.deck.addCard(card);
    }
}
