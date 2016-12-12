package com.example.danyllo.pokedeck;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Danyllo on 7-12-2016.
 */

public class AppSyncTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private MainActivity activity;
    private String apiURL;

    // Constructor
    public AppSyncTask(MainActivity activity, String api) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        apiURL = api;
        Log.d("CONS", apiURL);
    }

    //Extract from API using HttpParser class
    @Override
    protected String doInBackground(String... params) {
        HttpParser apiParser = new HttpParser(apiURL);
        try {
            return apiParser.extractFromURL(params);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //Simple toast notifying user that data is being gathered
    protected void onPreExecute(){
        Toast preexe = Toast.makeText(context, "Acquiring data", Toast.LENGTH_LONG);
        preexe.show();
    }

    //Function which collects data and saves it in the activity
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        Log.d("RESULT", result);
        if(result.length() == 0) {
            Toast noData = Toast.makeText(context, "No data was found", Toast.LENGTH_LONG);
            noData.show();
        } else {
            ArrayList<Tuple> names = new ArrayList<Tuple>();

            Map<String, Card> cardMap = new HashMap<String, Card>();
            try {
                JSONObject json = new JSONObject(result);
                JSONArray listOfCards = json.getJSONArray("cards");
                for(int i=0; i < listOfCards.length(); i++) {
                    JSONObject jsonObj = listOfCards.getJSONObject(i);
                    Log.d("SIZE", String.valueOf(jsonObj.length()));
                    /*Iterator iterator = jsonObj.keys();
                    while(iterator.hasNext()){
                        String key = (String)iterator.next();
                        Log.d("KEY", key + jsonObj.getString(key));
                    }*/
                    names.add(new Tuple(jsonObj.getString("name"), jsonObj.getString("id")));
                    cardMap.put(jsonObj.getString("id"), new Card(jsonObj));
                    Log.d("MAP", jsonObj.toString());
                }
                Log.d("LOL", listOfCards.getJSONObject(0).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.activity.setData(names, cardMap);
        }
    }
}
