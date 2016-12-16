package com.example.danyllo.pokedeck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Danyllo on 10-12-2016.
 */

/*Adapter used for the ListView in the MainActivity after a search in the API has been performed*/

public class CardAdapter extends ArrayAdapter<Tuple> {
    private final Context context;
    private ArrayList<Tuple> nameAndID;

    public CardAdapter(Context context, ArrayList<Tuple> nameAndIDList) {
        super(context, R.layout.activity_main, nameAndIDList);
        this.context = context;
        this.nameAndID = nameAndIDList;
    }

    @Override
    //This function sets a tuple in the listView
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View rowView = inflater.inflate(R.layout.activity_list, parent, false);
        View rowView = inflater.inflate(R.layout.listview_row_item, parent, false);
        TextView listRow = (TextView) rowView.findViewById(R.id.rowItem);
        listRow.setText(this.nameAndID.get(position).toString());
        return rowView;
    }
}
