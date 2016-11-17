package com.example.rick.rickvergunst_pset3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Rick on 11/15/2016.
 */
public class MovieInf extends AppCompatActivity {

    // Assign variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageView posterImg;
    TextView titleText;
    TextView infoText;
    TextView plotText;
    Button button;
    String title;

    public static final String myPreference = "mypreference";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info);

        // Assign the specific views to variables
        posterImg = (ImageView)findViewById(R.id.posterImg);
        titleText = (TextView)findViewById(R.id.specTitle);
        infoText = (TextView)findViewById(R.id.specInfo);
        plotText = (TextView)findViewById(R.id.specPlot);
        button = (Button)findViewById(R.id.watchListMove);

        // Finds the preferences and assign an editor
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Retrieves the extra information from the intent that called this activity
        Bundle extras = getIntent().getExtras();
        String query = extras.getString("Info");
        try {
            // Makes an JSON object of the query and assign the information to variables
            JSONObject obj = new JSONObject(query);
            String poster = obj.getString("Poster");
            title = obj.getString("Title");
            String released = obj.getString("Released");
            String runtime = obj.getString("Runtime");
            String genre = obj.getString("Genre");
            String director = obj.getString("Director");
            String actors = obj.getString("Actors");
            String plot = obj.getString("Plot");

            // Uses all the variables to fill the page with information of the particular movie
            infoText.setText(
                    "Title: " + title +  "\r\n"
                    + "Released: " + released + "\r\n"
                    + "Runtime: " + runtime + "\r\n"
                    + "Genre: " + genre + "\r\n"
                    + "Director: " + director + "\r\n"
                    + "Actors: " + actors + "\r\n"
            );
            plotText.setText(plot);

            // Checks whether the movie is inside the prefernces and renames the button accordingly
            if (sharedPreferences.contains(title)) {
                button.setText("Remove");
            }
            else {
                button.setText("Add");
            }

            // AsyncTask to retrieve the poster of the movie online
            // If there is no poster a placeholder is loaded
            if (poster.equals("N/A")){
                poster = "http://www.popcorn.sg/assets/app/images/placeholder-movieimage.png";
            }
            new BitMap().execute(poster).get();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Function for the button to either remove or add the movie to the preferences
    public void addRemoveMovie(View view) {
        if (button.getText().equals("Add")) {
            editor.putString(title, title);
            editor.commit();
            button.setText("Remove");
        }
        else {
            editor.remove(title);
            editor.commit();
            button.setText("Add");
        }
    }

    // AsyncTask to retrieve the movie poster
    private class BitMap extends AsyncTask<String, Object, Bitmap> {

        protected Bitmap doInBackground(String... params) {
            try {
                // Retrieves a BitMap from the given link and returns that bitmap
                URL url = new URL(params[0]);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Log.d("Back", "Back " + bmp);
                return bmp;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void onPostExecute(Bitmap bmp) {
            // Sets the image within the page to the retrieved BitMap
            posterImg.setImageBitmap(bmp);
        }

    }

    // Generic buttons on the bottom of the page
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
