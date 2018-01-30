package com.example.android.mybooklist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.example.android.mybooklist.GoogleBooksAsyncTask;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Book> books = new ArrayList<>();


        ListView booksList = (ListView) findViewById(R.id.books_list);

        BookAdapter adapter = new BookAdapter(this, books);

        booksList.setAdapter(adapter);
        booksList.setEmptyView((TextView) findViewById(R.id.emptyResults));

        Button button = (Button) findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence query = ((TextView) findViewById(R.id.search_text)).getText();
                GoogleBooksAsyncTask task = new GoogleBooksAsyncTask(query, MainActivity.this);
                task.execute();
            }
        });


    }


}
