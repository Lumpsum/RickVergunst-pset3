package com.example.rick.rickvergunst_pset3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Rick on 11/13/2016.
 */

public class SearchPage extends AppCompatActivity {

    // Assign variables
    EditText mEdit;
    public ListView lv;
    public ArrayList<String> items;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        // Sets variables to different views
        mEdit = (EditText)findViewById(R.id.searchQuery);
        lv = (ListView)findViewById(R.id.searchList);

        items = new ArrayList<String>();

        // Handles clicks on listview items
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Sets the title and param of the specific movie
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view).getText().toString();
                String param = "t";
                // Executes task to retrieve information of that specific movie
                try {
                    new Task().execute(title, param).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        // Checks for saved instance state and fills the listview with the found movies when the orientation is chagned
        if (savedInstanceState != null) {
            items = (ArrayList<String>) savedInstanceState.getSerializable("items");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textview, items);
            this.lv.setAdapter(arrayAdapter);
        }
    }

    // Sets the savedinstancestate when the orientation is changed with the listview items
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("items", items);
    }

    // AsyncTask to handle queries to the omdbapi, whether it is a search or a specific movie
    public class Task extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {

            // Retrieves the param and fills the URl accordingly and returns the results of the query
            String param = params[1];
            try {
                InputStream input = new URL("http://www.omdbapi.com/?" + URLEncoder.encode(param, "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return param + result;
            }
            catch (IOException e) {
                Log.d("MainActivity", "Error" + e);
                return null;
            }
        }

        public void onPostExecute(String result) {

            // Splits the result to differentiate between a search and a specific query
            String param = result.substring(0, 1);
            String query = result.substring(1);
            try {
                JSONObject jsonObject = new JSONObject(query);
                if (jsonObject.has("Error")) {
                    items.clear();
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SearchPage.this, R.layout.list_item, R.id.textview, items);
                    SearchPage.this.lv.setAdapter(arrayAdapter);
                }
                else {
                    // Statement for a search
                    if (param.equals("s")) {

                        //Fils an array with items of the returned query using a JsonArray
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray("Search");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObj = jsonArray.getJSONObject(i);
                                String title = jObj.getString("Title");
                                items.add(title);
                            }

                            // Fills the listview of the page with the results
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SearchPage.this, R.layout.list_item, R.id.textview, items);
                            SearchPage.this.lv.setAdapter(arrayAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Else starts a new activity with the information of a specific movie
                    else {
                        Intent intent = new Intent(SearchPage.this, MovieInf.class);
                        intent.putExtra("Info", query);
                        startActivity(intent);
                        SearchPage.this.finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    // Handles a search query and starts a task with the query
    public void search(View view) {
        String userQuery = mEdit.getText().toString();
        String param = "s";
        try {
            new Task().execute(userQuery, param).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Generic buttons in the bottom of the app
    public void toHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void toSearchPage(View view) {
        Intent intent = new Intent(this, SearchPage.class);
        startActivity(intent);
        this.finish();
    }

    public void toWatchList(View view) {
        Intent intent = new Intent(this, WatchList.class);
        startActivity(intent);
        this.finish();
    }

}
