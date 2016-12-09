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

    private ArrayAdapter<String> searchAdapter;
    private ArrayList<String> cardList = new ArrayList<String>();
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
        searchAdapter = new ArrayAdapter<String>(this, simple_list_item_1, cardList);
        searchList.setAdapter(searchAdapter);
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object cardName = searchList.getItemAtPosition(position);
                goToTitlePage(cardName.toString());
            }
        });
    }

    private void goToTitlePage(String string) {
        Log.d("STRING", string);
        Log.d("STRING2", cardMap.get(string).toString());
        //Card card = new Card(cardMap.get(string));
        Intent cardActivity = new Intent(this, CardActivity.class);
        cardActivity.putExtra("card", cardMap.get(string));
        startActivity(cardActivity);
    }

    public void searchCard(View view) {
        String name = searchET.getText().toString().trim();
        AppSyncTask task = new AppSyncTask(this, "https://api.pokemontcg.io/v1/cards");
        task.execute(name);
        Log.d("W0t", name);
    }

    public void setData(ArrayList<String> names, Map<String, Card> mapWithCards) {
        cardList = names;
        cardMap = mapWithCards;
        searchAdapter.clear();
        searchAdapter.addAll(cardList);
        searchAdapter.notifyDataSetChanged();
    }

    /*public void goToList(View view) {
    }*/
}
