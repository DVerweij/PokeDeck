package com.example.danyllo.pokedeck;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private TextView usernameView;
    private Button logButton;
    //The lists that will be filled by the searching of the cards
    //Tuple is a simple pair class (in this case of two Strings)
    private ArrayList<Tuple> cardList = new ArrayList<Tuple>();
    private Map<String, Card> cardMap = new HashMap<String, Card>();

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogin();
        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void checkLogin() {
        //Courtesy of: http://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        usernameView = (TextView) findViewById(R.id.username);
        logButton = (Button) findViewById(R.id.signout);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //when not triggered by the continue offline button
                    if (getIntent().getStringExtra("offline") == null) {
                        // launch login activity
                        Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
                        goToLogin.putExtra("Activity", "Main");
                        startActivity(goToLogin);
                        finish();
                    }
                }
            }
        };
        if (user != null) {
            String viewString = "Logged in as: " + user.getEmail();
            usernameView.setText(viewString);
            logButton.setText("Sign Out");
        } else {
            usernameView.setText("Not logged in");
            logButton.setText("Sign In");
        }
        //click listeners for the button
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logButton.getText().toString().equals("Sign In")) {
                    Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
                    goToLogin.putExtra("Activity", "Main");
                    startActivity(goToLogin);
                    finish();
                } else {
                    mAuth.signOut();
                }
            }
        });
    }


    //Initialization function which sets up the views and the adapter for the listview
    private void initialize() {
        searchET = (EditText) findViewById(R.id.editText);
        searchList = (ListView) findViewById(R.id.cardList);
        searchList.setAdapter(new CardAdapter(this, cardList));
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
        task.execute(name);
        Log.d("W0t", name);
    }

    //Function called from AppSyncTask which sets the data in the structures
    public void setData(ArrayList<Tuple> names, Map<String, Card> mapWithCards) {
        cardList = names;
        cardMap = mapWithCards;
        searchList.setAdapter(new CardAdapter(this, names));
        Log.d("GOT", "HERE");
    }


    public void goToDeckList(View view) {
        if (mAuth.getCurrentUser() != null) {
            Intent deckList = new Intent(this, DeckActivity.class);
            startActivity(deckList);
        } else {
            Toast notAUser = Toast.makeText(this, "Have to be logged in", Toast.LENGTH_SHORT);
            notAUser.show();
        }
    }
}
