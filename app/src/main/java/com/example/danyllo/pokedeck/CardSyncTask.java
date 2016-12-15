package com.example.danyllo.pokedeck;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Danyllo on 9-12-2016.
 */

public class CardSyncTask extends AsyncTask<Card, Integer, Bitmap> {
    private Context context;
    private CardActivity activity;

    // Constructor
    public CardSyncTask(CardActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
    }

    @Override
    protected Bitmap doInBackground(Card... params) {
        String imageURL = params[0].getImageURL();
        Bitmap picture = null;
        try {
            InputStream input = new java.net.URL(imageURL).openStream();
            picture = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return picture;
    }

    protected void onPreExecute(){
        Toast preexe = Toast.makeText(context, "Acquiring data", Toast.LENGTH_LONG);
        preexe.show();
    }

    protected void onPostExecute(Bitmap picture){
        super.onPostExecute(picture);
        if(picture == null) {
            Toast noData = Toast.makeText(context, "No picture was found", Toast.LENGTH_LONG);
            noData.show();
        } else {
            this.activity.setBitmap(picture);
        }
    }
}

