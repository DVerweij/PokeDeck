package com.example.danyllo.pokedeck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        return rowView;
    }
}
