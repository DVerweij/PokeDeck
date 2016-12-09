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

    public Card(JSONObject jsonObject) {
        try {
            setVariables(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setVariables(JSONObject jsonObject) throws JSONException{
        this.id = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
        Log.d("NAME", this.name);
        this.pokedexEntry = Integer.parseInt(jsonObject.getString("nationalPokedexNumber"));
        this.ImageLink = jsonObject.getString("imageUrl");
        this.subType = jsonObject.getString("subtype");
        Log.d("SUBTYPE", this.subType);
        this.superType = jsonObject.getString("supertype");
        this.HP = Integer.parseInt(jsonObject.getString("hp"));

        JSONArray retreatCostArray = jsonObject.getJSONArray("retreatCost");
        Log.d("COST", retreatCostArray.getString(0));
        this.retreatCost = new Tuple(String.valueOf(retreatCostArray.length()), retreatCostArray.getString(0));

        JSONArray typesArray = jsonObject.getJSONArray("types");
        for (int i = 0; i < typesArray.length(); i++) {
            this.types.add(typesArray.getString(i));
        }

        JSONArray weaknessArray = jsonObject.getJSONArray("weaknesses");
        JSONObject weaknessJSON = weaknessArray.getJSONObject(0);
        String weaknessType = weaknessJSON.getString("type");
        String weaknessValue = weaknessJSON.getString("value");
        this.weakness = new Tuple(weaknessType, weaknessValue);


    }
    public String getImageURL() {
        return this.ImageLink;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getDetails() {
        ArrayList<String> details = new ArrayList<String>();
        //list additions
        details.add("id: " + this.id);
        details.add("PokeDexNumber: " + String.valueOf(this.pokedexEntry));
        details.add("HP: " + String.valueOf(this.HP));
        details.add("Subtype: " + this.subType);
        details.add("Supertype: " + this.superType);

        String typeString = "Types: ";
        for (int i = 0; i<this.types.size(); i++) {
            if (i != this.types.size() - 1) {
                typeString += this.types.get(i) + ", ";
            } else {
                typeString += this.types.get(i);
            }
        }
        details.add(typeString);

        details.add("Retreat Cost: " + this.retreatCost.first + " " + this.retreatCost.second);
        details.add("Weakness: " + this.weakness.first + " " + this.weakness.second);

        return details;
    }
}
