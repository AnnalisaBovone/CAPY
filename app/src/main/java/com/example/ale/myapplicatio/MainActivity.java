package com.example.ale.myapplicatio;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity  {

    private AutoCompleteTextView cerca;
    private Button bottone;
    private String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButtonListener buttonListener = new ButtonListener();
        cityList = new ArrayList<>();
        bottone = (Button) findViewById(R.id.bottone);
        cerca = (AutoCompleteTextView) findViewById(R.id.cerca);
        final TextWatcher passwordWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >2) {
                    new GetCity().execute(s.toString());
                }
            }

            public void afterTextChanged(Editable s) {
                cityList.clear();
            }
        };
        cerca.addTextChangedListener(passwordWatcher);
        bottone.setOnClickListener(buttonListener);



    }



    private class GetCity extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // Toast.makeText(MainActivity.this, "Json Data is downloading " , Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(String... arg0) {
            HttpHandler sh = new HttpHandler();
            String url1 = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
            String url2 ="&types=(cities)&key=AIzaSyBieTKI8Lmg7TuF2MgUUtK93bjpWylxLBM";
            String url= url1 + arg0[0] + url2 ;

            String jsonStr = sh.makeServiceCall(url);
                       if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray predictions = jsonObj.getJSONArray("predictions");

                    // looping through All Contacts
                    for (int i = 0; i < predictions.length(); i++) {
                        JSONObject c = predictions.getJSONObject(i);
                        String description = c.getString("description");
                        String id = c.getString("id");
                        JSONArray matched_substrings = c.getJSONArray("matched_substrings");
                        for (int j = 0; j < matched_substrings.length(); j++) {
                            JSONObject m = matched_substrings.getJSONObject(j);
                            String length = m.getString("length");
                            String offset = m.getString("offset");
                        }
                        String placeid = c.getString("place_id");
                        String reference = c.getString("reference");
                        // Phone node is JSON Object
                        JSONObject structured_formatting = c.getJSONObject("structured_formatting");
                        String main_text = structured_formatting.getString("main_text");
                        JSONArray main_text_matched_substrings = structured_formatting.getJSONArray("main_text_matched_substrings");
                        for (int k = 0; k < main_text_matched_substrings.length(); k++) {
                            JSONObject mt = main_text_matched_substrings.getJSONObject(k);
                            String length1 = mt.getString("length");
                            String offset1 = mt.getString("offset");
                        }
                        String secondary_text = structured_formatting.getString("secondary_text");
                        JSONArray terms = c.getJSONArray("terms");
                        for (int p = 0; p < terms.length(); p++) {
                            JSONObject t = terms.getJSONObject(p);
                            String offset = t.getString("offset");
                            String value = t.getString("value");
                        }
                        String types = c.getString("types");
                        cityList.add(description);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                //Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(getApplicationContext(),"Couldn't get json from server. Check LogCat for possible errors!",Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, cityList);
            cerca.setAdapter(adapter);
            if (!cerca.isPopupShowing()) {
                cerca.showDropDown();
            }

        }
    }
    public class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String ricerca = cerca.getText().toString();
            Intent intent = new Intent(MainActivity.this, RicercaActivity.class);
            intent.putExtra("citta", ricerca);
            startActivity(intent);

        }


    }

}


