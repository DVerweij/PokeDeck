package com.example.danyllo.pokedeck;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Danyllo on 9-12-2016.
 */

//Generic pair object which is serializable

public class Tuple implements Serializable{
    public String first;
    public String second;

    //constructor for use with firebase
    public Tuple() {
        Log.d("CONSTRUCTED", "CONSTRUCTED");
    }

    public Tuple (String one, String two) {
        this.first = one;
        this.second = two;
    }

    //Function which returns both Strings in one String for use in a ListView
    public String toString() {
        return this.first + ": (" + this.second + ")";
    }
}
