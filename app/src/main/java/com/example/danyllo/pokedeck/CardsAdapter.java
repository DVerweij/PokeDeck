package com.example.danyllo.pokedeck;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Danyllo on 12-12-2016.
 */

/*This the Adapter that maps the cards to the ListViews in Main-, Deck- and HandActivity
 It fills TextViews with the Card's name and ID (picture may be added after layout changes)*/

public class CardsAdapter extends ArrayAdapter<Card> {
    private final Context context;
    private ArrayList<Card> deck;


    public CardsAdapter(Context context, ArrayList<Card> deckList) {
        super(context, R.layout.activity_main, deckList);
        this.context = context;
        this.deck = deckList;
    }

    //function which sets the card name and id in the listView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_row_item, parent, false);
        TextView listRow = (TextView) rowView.findViewById(R.id.rowItem);
        Card thisCard = this.deck.get(position);
        listRow.setText(thisCard.getName() + " (" + thisCard.getId() + ")");
        setTypeIcons(rowView, thisCard);
        return rowView;
    }

    //This function sets the type icons for the card on the imageViews
    private void setTypeIcons(View rowView, Card card) {
        ImageView firstPicture = (ImageView) rowView.findViewById(R.id.rowImage1);
        ImageView secondPicture = (ImageView) rowView.findViewById(R.id.rowImage2);
        //All pokemon and basic energy cards will display a type icon
        //Get type string from pokemon or energy card
        if (card.getSuperType().equals("Pokémon")) {
            setDrawable(card.getTypes().get(0), firstPicture);
            //If dual-typed, display the second type as well
            if (card.getTypes().size() > 1) {
                setDrawable(card.getTypes().get(1), secondPicture);
            }
        } else if (card.isBasicEnergy()) {
            setDrawable(card.getName().split(" ")[0], firstPicture);
        }
    }

    //This function sets the drawable on an ImageView to a certain icon, dependant on the type of
    // the pokemon or energy card
    private void setDrawable(String type, ImageView picture) {
        switch(type.toLowerCase()) {
            case "fire":
                picture.setImageResource(R.drawable.fire);
                break;
            case "lightning":
                picture.setImageResource(R.drawable.lightning);
                break;
            case "water":
                picture.setImageResource(R.drawable.water);
                break;
            case "grass":
                picture.setImageResource(R.drawable.grass);
                break;
            case "psychic":
                picture.setImageResource(R.drawable.psychic);
                break;
            case "fighting":
                picture.setImageResource(R.drawable.fighting);
                break;
            case "metal":
                picture.setImageResource(R.drawable.metal);
                break;
            case "darkness":
                picture.setImageResource(R.drawable.darkness);
                break;
            case "dragon":
                picture.setImageResource(R.drawable.dragon);
                break;
            case "fairy":
                picture.setImageResource(R.drawable.fairy);
                break;
            case "colorless":
                picture.setImageResource(R.drawable.colorless);
                break;
        }
    }
}
