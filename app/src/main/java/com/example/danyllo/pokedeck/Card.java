package com.example.danyllo.pokedeck;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Danyllo on 8-12-2016.
 */

/*This is an object specifically made for the card itself with variables
denoting the card information*/

public class Card implements Serializable{
    //Globals of card characteristics
    private String id;
    private String name;
    private String setName;
    private int pokedexEntry;
    private String imageLink;
    private String subType;
    private String superType;
    private int hitPoints;
    private Tuple retreatCost;
    private ArrayList<String> types = new ArrayList<String>();
    private Tuple weakness;
    private Tuple resistance;

    //constructor for firebase
    public Card() {}

    //actual constructor
    public Card(JSONObject jsonObject) {
        try {
            setVariables(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Public getters functions to work with setValue function of Firebase
    public String getId() {
        return id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getName() {
        return name;
    }

    public String getSetName() {
        return setName;
    }

    public int getPokedexEntry() {
        return pokedexEntry;
    }

    public String getSubType() {
        return subType;
    }

    public String getSuperType() {
        return superType;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public Tuple getRetreatCost() {
        return retreatCost;
    }

    public Tuple getWeakness() {
        return weakness;
    }

    public Tuple getResistance() {
        return resistance;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    private void setVariables(JSONObject jsonObject) throws JSONException{
        //All cards will have the following five variables (there are more like artist and set name)
        this.id = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
        this.imageLink = jsonObject.getString("imageUrl");
        this.subType = jsonObject.getString("subtype");
        this.superType = jsonObject.getString("supertype");
        this.setName = jsonObject.getString("set");
        //Trainer and energy cards lack some features exclusive to the actual Pokémon
        //Thus the parsePokemon function called iff the supertype is Pokémon
        if (jsonObject.getString("supertype").equals("Pokémon")) {
            parsePokemon(jsonObject);
        }
    }

    //This function adds all card information to an arraylist of Strings to be used in the
    // CardActivity ListView
    public ArrayList<String> getDetails() {
        ArrayList<String> details = new ArrayList<String>();
        //list additions
        details.add("id: " + this.id);
        //set name
        details.add("Set Name: " + this.setName);
        //subTypes may be non-existent (for regular Trainer cards for example)
        if (!this.subType.equals("")) {
            details.add("Subtype: " + this.subType);
        } else {
            details.add("Subtype: N/A");
        }
        details.add("Supertype: " + this.superType);
        //the pokemon traits are added in this separate if-statement
        if (this.superType.equals("Pokémon")) {
            details.add("PokeDexNumber: " + String.valueOf(this.pokedexEntry));
            details.add("HP: " + String.valueOf(this.hitPoints));

            String typeString = "Types: ";
            for (int i = 0; i<this.types.size(); i++) {
                if (i != this.types.size() - 1) {
                    typeString += this.types.get(i) + ", ";
                } else {
                    typeString += this.types.get(i);
                }
            }
            details.add(typeString);

            if (this.retreatCost != null) {
                details.add("Retreat Cost: " + this.retreatCost.first + " " + this.retreatCost.second);
            }
            if (this.weakness != null) {
                details.add("Weakness: " + this.weakness.first + " " + this.weakness.second);
            }
            if (this.resistance != null) {
                details.add("Resistance: " + this.resistance.first + " " + this.resistance.second);
            }
        }
        return details;
    }

    //parsePokemon is a function that only saves the information exclusive to the actual Pokémon
    private void parsePokemon(JSONObject jsonObject) throws JSONException{
        //The following traits are traits that all Pokémon have
        this.pokedexEntry = Integer.parseInt(jsonObject.getString("nationalPokedexNumber"));
        this.hitPoints = Integer.parseInt(jsonObject.getString("hp"));

        //Some have multiple (two) types which are added to an ArrayList of Strings
        //Not entirely sure if all pokemon cards are just dual-typed, otherwise Tuple could be used
        //instead of the list
        JSONArray typesArray = jsonObject.getJSONArray("types");
        for (int i = 0; i < typesArray.length(); i++) {
            this.types.add(typesArray.getString(i));
        }

        //not every pokemon has a retreatcost so this needs to be checked
        if (jsonObject.has("retreatCost")) {
            JSONArray retreatCostArray = jsonObject.getJSONArray("retreatCost");
            this.retreatCost = new Tuple(String.valueOf(retreatCostArray.length()), retreatCostArray.getString(0));
        }

        //the same applies for weakness
        if (jsonObject.has("weaknesses")) {
            JSONArray weaknessArray = jsonObject.getJSONArray("weaknesses");
            JSONObject weaknessJSON = weaknessArray.getJSONObject(0);
            String weaknessType = weaknessJSON.getString("type");
            String weaknessValue = weaknessJSON.getString("value");
            this.weakness = new Tuple(weaknessType, weaknessValue);
        }

        //Very little pokemon, from what I've seen, have resistances, probably to maintain balance
        if (jsonObject.has("resistances")) {
            JSONArray resistanceArray = jsonObject.getJSONArray("resistances");
            JSONObject resistanceJSON = resistanceArray.getJSONObject(0);
            String resistanceType = resistanceJSON.getString("type");
            String resistanceValue = resistanceJSON.getString("value");
            this.resistance = new Tuple(resistanceType, resistanceValue);
        }
    }

    //Basic energies can be stocked limitlessly in your deck (bar other restrictions)
    //This boolean is used as a check for deck building to prevent rule breaking
    public boolean isBasicEnergy() {
        return this.superType.equals("Energy") && this.subType.equals("Basic");
    }


    //A deck needs at least one basic pokemon, this boolean is used to prevent decks with no
    //basic pokemon
    public boolean isBasicPokemon() {
        return this.superType.equals("Pokémon") && this.subType.equals("Basic");
    }
}
