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

    protected void onPreExecute(){
        Toast preexe = Toast.makeText(context, "Acquiring data", Toast.LENGTH_LONG);
        preexe.show();
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
        Log.d("RESULT", result);
        if(result.length() == 0) {
            Toast noData = Toast.makeText(context, "No data was found", Toast.LENGTH_LONG);
            noData.show();
        } else {
            ArrayList<String> names = new ArrayList<String>();
            Map<String, JSONObject> cardMap = new HashMap<String, JSONObject>();
            try {
                JSONObject json = new JSONObject(result);
                JSONArray listOfCards = json.getJSONArray("cards");
                for(int i=0; i < listOfCards.length(); i++) {
                    JSONObject jsonObj = listOfCards.getJSONObject(i);
                    names.add(jsonObj.getString("name"));
                    cardMap.put(jsonObj.getString("name"), jsonObj);
                }
                Log.d("LOL", listOfCards.getJSONObject(0).getString("Title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.activity.setData(names, cardMap);
        }
    }
}
