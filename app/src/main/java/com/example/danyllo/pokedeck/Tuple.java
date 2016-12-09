package com.example.danyllo.pokedeck;

import java.io.Serializable;

/**
 * Created by Danyllo on 9-12-2016.
 */

//Generic pair object which is serializable

public class Tuple implements Serializable{
    public String first;
    public String second;
    public Tuple (String one, String two) {
        this.first = one;
        this.second = two;
    }
}
