package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_list_item_1;

/* The activity that the app starts with. It handels the searching of the pokemon cards based on name*/
public class MainActivity extends AppCompatActivity {
    //The views as global variables
    private EditText searchET;
    private ListView searchList;
    //The lists that will be filled by the searching of the cards
    //Tuple is a simple pair class (in this case of two Strings)
    private ArrayList<Tuple> cardList = new ArrayList<Tuple>();
    private Map<String, Card> cardMap = new HashMap<String, Card>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    //Initialization function which sets up the views and the adapter for the listview
    private void initialize() {
        searchET = (EditText) findViewById(R.id.editText);
        searchList = (ListView) findViewById(R.id.cardList);
        searchList.setAdapter(new CustomAdapter(this, cardList));
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Tuple cardNameAndID = cardList.get(position);
                goToCardPage(cardNameAndID);
            }
        });
    }

    //Function that on listView click goes to the Card found at the listView's child in question
    private void goToCardPage(Tuple nameAndID) {
        Intent cardActivity = new Intent(this, CardActivity.class);
        cardActivity.putExtra("card", cardMap.get(nameAndID.second)); //Gets Card object from ID
        startActivity(cardActivity);
    }

    //The search function
    public void searchCard(View view) {
        String name = searchET.getText().toString().trim(); //Trim for better effect
        AppSyncTask task = new AppSyncTask(this, "https://api.pokemontcg.io/v1/cards");
        task.execute(name); //Execute the syncTask with the input of the EditText
        Log.d("W0t", name);
    }

    //Function called from AppSyncTask which sets the data in the structures
    public void setData(ArrayList<Tuple> names, Map<String, Card> mapWithCards) {
        cardList = names;
        cardMap = mapWithCards;
        searchList.setAdapter(new CustomAdapter(this, names)); //Also resets adapter
        Log.d("GOT", "HERE");
    }

    /*public void goToList(View view) {
    }*/
}
