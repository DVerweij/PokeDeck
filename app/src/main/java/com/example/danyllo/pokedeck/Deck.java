package com.example.danyllo.pokedeck;

import java.util.ArrayList;

/**
 * Created by Danyllo on 8-12-2016.
 */

public class Deck {
    //Globals
    private ArrayList<Card> deckList; //List of cards
    private int deckSize; //Size of the deck, needed for hand testing function
    private int rating; //Cumulative rating of generated hands by the user
    private int ratingCount; //Amount of ratings
    private double avgRating; //Average of the ratings, made double for precision

    public Deck() {
        this.deckList = new ArrayList<Card>();
        this.deckSize = deckList.size();
        this.rating = 0;
        this.ratingCount = 0;
        this.avgRating = 0;
    }
}
