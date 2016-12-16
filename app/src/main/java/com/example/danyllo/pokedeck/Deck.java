package com.example.danyllo.pokedeck;

import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Danyllo on 8-12-2016.
 */

/*Deck object containing the list of cards from the deck among other variables like size and deck rating*/
public class Deck implements Serializable{
    //Globals
    private ArrayList<Card> deckList; //List of cards
    public int deckSize; //Size of the deck, needed for hand testing function
    public int rating; //Cumulative rating of generated hands by the user
    public int ratingCount; //Amount of ratings
    public double avgRating; //Average of the ratings, made double for precision

    //Empty constructor for creation when not trying to add
    public Deck() {
        this.deckList = new ArrayList<Card>();
        this.deckSize = deckList.size();
        this.rating = 0;
        this.ratingCount = 0;
        this.avgRating = 0;
    }

    //Constructor with built-in add
    public Deck(Card card) {
        this.deckList = new ArrayList<Card>();
        this.deckSize = deckList.size();
        this.rating = 0;
        this.ratingCount = 0;
        this.avgRating = 0;
        addCard(card);
    }

    //public getters and setters for use with firebase
    public ArrayList<Card> getCardList() {
        return this.deckList;
    }

    public void setCardList(ArrayList<Card> cardList) {
        this.deckList = cardList;
    }

    public int getDeckSize() {
        return this.deckSize;
    }

    public int getRating() {
        return this.rating;
    }

    public int getRatingCount() {
        return this.ratingCount;
    }

    public double getAvgRating() {
        return this.avgRating;
    }

    //Add function, this returns a boolean so the activity can check if the add was successful
    public boolean addCard(Card card) {
        boolean canBeAdded = ableToAdd(card);
        //if the card can be added, add it to the list and increment the deckSize
        if (canBeAdded) {
            deckList.add(card);
            deckSize++;
        }
        return canBeAdded;
    }

    //function which checks if the conditions are right for the card to be added
    private boolean ableToAdd(Card card) {
        //Only up to 4 of each non-basic energy card and non-energy cards
        if (!(card.isBasicEnergy()) && occurences(card) == 4) {
            return false;
            //a deck has to contain at least one basic pokemon so the if 59 cards are all not basic
            //the last card has to be basic
        } else if (!(containsBasicPokemon()) && deckSize == 59) {
            //returns true if basic pokemon so it's able to add in this condition
            return card.isBasicPokemon();
            //decksize has to be max 60 so this returns true if it's below 60 so cards can be added
        } else {
            //it can't get above 60 cards, but an alternative could be return deckSize =< 60;
            return deckSize != 60;
        }
    }

    //boolean function which checks if the deck contains a basic pokemon, which the deck needs at
    //least one of
    private boolean containsBasicPokemon() {
        for (int i = 0; i < deckSize; i++) {
            if (deckList.get(i).getSuperType().equals("Pokémon")
                    && deckList.get(i).getSubType().equals("Basic")) {
                return true;
            }
        }
        return false;
    }

    //counts the occurences of a card by NAME, not by ID
    //This is done because a deck can only contain four cards with the same name (regardless of ID)
    public int occurences(Card card) {
        int occurences = 0;
        for (int i = 0; i < deckSize; i++) {
            if (deckList.get(i).getName().equals(card.getName())) {
                occurences++;
            }
        }
        return occurences;
    }

    //delete function
    public void deleteCard(Card card) {
        deckList.remove(card);
        deckSize--;
    }

    //get card at a specific position the list
    public Card getCard(int index) {
        return deckList.get(index);
    }
}
