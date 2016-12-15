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

public class DeckAdapter extends ArrayAdapter<Card> {
    private final Context context;
    private ArrayList<Card> deck;


    public DeckAdapter(Context context, ArrayList<Card> deckList) {
        super(context, R.layout.activity_main, deckList);
        this.context = context;
        this.deck = deckList;
    }

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
