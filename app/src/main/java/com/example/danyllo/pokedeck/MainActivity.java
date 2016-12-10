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

public class MainActivity extends AppCompatActivity {

    private EditText searchET;
    private ListView searchList;

    //private CustomAdapter searchAdapter;
    private ArrayList<Tuple> cardList = new ArrayList<Tuple>();
    private Map<String, Card> cardMap = new HashMap<String, Card>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        searchET = (EditText) findViewById(R.id.editText);
        searchList = (ListView) findViewById(R.id.cardList);
        searchList.setAdapter(new CustomAdapter(this, cardList));
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Tuple cardNameAndID = cardList.get(position);
                goToTitlePage(cardNameAndID);
            }
        });
    }

    private void goToTitlePage(Tuple nameAndID) {
        //Log.d("STRING", string);
        //Log.d("STRING2", cardMap.get(string).toString());
        //Card card = new Card(cardMap.get(string));
        Intent cardActivity = new Intent(this, CardActivity.class);
        cardActivity.putExtra("card", cardMap.get(nameAndID.second));
        startActivity(cardActivity);
    }

    public void searchCard(View view) {
        String name = searchET.getText().toString().trim();
        AppSyncTask task = new AppSyncTask(this, "https://api.pokemontcg.io/v1/cards");
        task.execute(name);
        Log.d("W0t", name);
    }

    public void setData(ArrayList<Tuple> names, Map<String, Card> mapWithCards) {
        cardList = names;
        cardMap = mapWithCards;
        searchList.setAdapter(new CustomAdapter(this, names));
        Log.d("GOT", "HERE");
    }

    /*public void goToList(View view) {
    }*/
}
