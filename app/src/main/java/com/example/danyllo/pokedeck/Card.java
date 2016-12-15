package com.example.danyllo.pokedeck;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Danyllo on 8-12-2016.
 */

public class Card implements Serializable{
    //Globals of pokemon characteristics
    private String id;
    private String name;
    private int pokedexEntry;
    private String ImageLink;
    private String subType;
    private String superType;
    private int HP;
    private Tuple retreatCost;
    private ArrayList<String> types = new ArrayList<String>();
    private Tuple weakness;
    private Tuple resistance;

    //constructor for firebase
    public Card() {
        Log.d("PLACEHOLDER", "PLACEHOLDER");
    }

    public Card(JSONObject jsonObject) {
        try {
            setVariables(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Public getters functions to work with setValue function of Firebase
    public String getId() {
        return this.id;
    }

    public String getImageURL() {
        return this.ImageLink;
    }

    public String getName() {
        return this.name;
    }

    public int getPokedexEntry() {
        return this.pokedexEntry;
    }

    public String getSubType() {
        return this.subType;
    }

    public String getSuperType() {
        return this.superType;
    }

    public int getHP() {
        return this.HP;
    }

    public Tuple getRetreatCost() {
        return this.retreatCost;
    }

    public Tuple getWeakness() {
        return this.weakness;
    }

    public Tuple getResistance() {
        return this.resistance;
    }

    public ArrayList<String> getTypes() {
        return this.types;
    }

    private void setVariables(JSONObject jsonObject) throws JSONException{
        this.id = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
        this.ImageLink = jsonObject.getString("imageUrl");
        this.subType = jsonObject.getString("subtype");
        this.superType = jsonObject.getString("supertype");
        Log.d("NAME", this.name);
        //Trainer and energy cards lack some features exclusive to the actual Pokémon
        //Thus the parsePokemon function called iff the supertype is pokémon
        if (jsonObject.getString("supertype").equals("Pokémon")) {
            parsePokemon(jsonObject);
        }
    }

    //This function adds all card information to an arraylist of Strings
    public ArrayList<String> getDetails() {
        ArrayList<String> details = new ArrayList<String>();
        //list additions
        details.add("id: " + this.id);
        if (!this.subType.equals("")) {
            details.add("Subtype: " + this.subType);
        } else {
            details.add("Subtype: N/A");
        }
        details.add("Supertype: " + this.superType);
        if (this.superType.equals("Pokémon")) {
            details.add("PokeDexNumber: " + String.valueOf(this.pokedexEntry));
            details.add("HP: " + String.valueOf(this.HP));

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
        this.pokedexEntry = Integer.parseInt(jsonObject.getString("nationalPokedexNumber"));
        this.subType = jsonObject.getString("subtype");
        Log.d("SUBTYPE", this.subType);
        this.superType = jsonObject.getString("supertype");
        this.HP = Integer.parseInt(jsonObject.getString("hp"));

        JSONArray typesArray = jsonObject.getJSONArray("types");
        for (int i = 0; i < typesArray.length(); i++) {
            this.types.add(typesArray.getString(i));
        }

        if (jsonObject.has("retreatCost")) {
            JSONArray retreatCostArray = jsonObject.getJSONArray("retreatCost");
            Log.d("COST", retreatCostArray.getString(0));
            this.retreatCost = new Tuple(String.valueOf(retreatCostArray.length()), retreatCostArray.getString(0));
        }

        if (jsonObject.has("weaknesses")) {
            JSONArray weaknessArray = jsonObject.getJSONArray("weaknesses");
            JSONObject weaknessJSON = weaknessArray.getJSONObject(0);
            String weaknessType = weaknessJSON.getString("type");
            String weaknessValue = weaknessJSON.getString("value");
            this.weakness = new Tuple(weaknessType, weaknessValue);
        }

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

    public boolean isBasicPokemon() {
        return this.superType.equals("Pokémon") && this.subType.equals("Basic");
    }
}
