package com.example.rick.rickvergunst_pset3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Rick on 11/15/2016.
 */
public class WatchList extends AppCompatActivity {

    // Create variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static final String myPreference = "mypreference";
    public static final String Name = "nameKey";

    TextView welcomeText;
    ListView lv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_list);

        // Retrieve preferences and assign editor
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Set welcome text and use username to personalize this text
        welcomeText = (TextView)findViewById(R.id.watchWelcome);
        welcomeText.setText(sharedPreferences.getString(Name, "") + welcomeText.getText());

        // Find ListView
        lv = (ListView)findViewById(R.id.watchList);

        // Loop through the added movies and put those movies inside an array
        ArrayList<String> items = new ArrayList<String>();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (!entry.getKey().equals(Name)) {
                items.add(entry.getValue().toString());
            }
        }

        // Use the created array to fill the listview through an adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textview, items);
        this.lv.setAdapter(arrayAdapter);

        // Hanlde clicks on the listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Retrieve the movie title and execute a Asynctask using that title
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view).getText().toString();
                try {
                    new WatchList.Task().execute(title).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Function to clear the full watchlist
    public void emptyWatchList(View view) {

        // Retrieves every key that is not Name and removes those keys
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (!entry.getKey().equals(Name)) {
                Log.d("Key", "Key " + entry.getKey());
                editor.remove(entry.getKey());
            }
        }
        // Resets the listview
        editor.commit();
        ArrayAdapter adapter = (ArrayAdapter)lv.getAdapter();
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    // Asyntask to handle the request from clicking on a movie
    public class Task extends AsyncTask<String, Object, StringBuilder> {

        @Override
        protected StringBuilder doInBackground(String... params) {
            try {
                // Retrieves the information from omdbapi and returns the result of the query
                InputStream input = new URL("http://www.omdbapi.com/?t=" + URLEncoder.encode(params[0], "UTF-8")).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result;
            }
            catch (IOException e) {
                Log.d("MainActivity", "Error" + e);
                return null;
            }
        }

        public void onPostExecute(StringBuilder result) {

            // Starts a MovieInf activity and passes the result of the query to this activity
            Intent intent = new Intent(WatchList.this, MovieInf.class);
            intent.putExtra("Info", result.toString());
            startActivity(intent);
            WatchList.this.finish();
        }
    }

    // Generic buttons on the bottom of the app
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
