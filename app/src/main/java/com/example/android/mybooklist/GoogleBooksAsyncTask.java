package com.example.android.mybooklist;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.net.ConnectivityManager;

import com.example.android.mybooklist.Book;
import com.example.android.mybooklist.BookAdapter;
import com.example.android.mybooklist.MainActivity;
import com.example.android.mybooklist.R;

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


public class GoogleBooksAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = GoogleBooksAsyncTask.class.getSimpleName();

    private final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private String query;
    public Activity activity;

    public GoogleBooksAsyncTask(CharSequence param, Activity activity) {
        query = String.valueOf(param);
        this.activity = activity;
    }


    @Override
    protected ArrayList<Book> doInBackground(String... params) {
        String jsonResponse = "";
        ArrayList<Book> books = new ArrayList<>();
        if (isNetworkAvailable()) {
            try {
                URL url = createUrl(GOOGLE_BOOKS_URL + query);
                jsonResponse = makeHttpRequest(url);
                if (!jsonResponse.isEmpty()) {
                    // Extract relevant fields from the JSON response and create an {@link Book} object
                    books = extractFeatureFromJson(jsonResponse);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem Geeting  Books from Google API", e);
            }
        }
        // Return the {@link Book} object as the result fo the {@link TsunamiAsyncTask}
        return books;
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        if (books == null) {
            return;
        }

        updateUi(books);
    }

    private void updateUi(ArrayList<Book> books) {
        // Display the earthquake title in the UI
        ListView booksList = (ListView) activity.findViewById(R.id.books_list);
        BookAdapter adapter = new BookAdapter(activity, books);
        booksList.setAdapter(adapter);

    }


    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                return jsonResponse;
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem Http Request", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem Http Request", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first books from the input booksJSON string.
     */
    private ArrayList<Book> extractFeatureFromJson(String booksJSON) {
        try {
            JSONObject baseJsonResponse = new JSONObject(booksJSON);
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
            ArrayList<Book> books = new ArrayList<>();
            Log.e(LOG_TAG, "+++++++++++++" + baseJsonResponse.toString());

            // If there are results in the features array
            if (itemsArray.length() > 0) {
                for (int i = 0; i < itemsArray.length(); i++) {
                    // Extract out the first feature (which is an books)
                    JSONObject item = itemsArray.getJSONObject(i);
                    JSONObject volume = item.getJSONObject("volumeInfo");

                    // Extract out the title, time, and tsunami values
                    String title = volume.getString("title");
                    ArrayList<String> authorsList = new ArrayList<>();
                    if (volume.has("authors")) {
                        JSONArray authors = volume.optJSONArray("authors");
                        if (authors != null) {
                            for (int j = 0; j < authors.length(); j++) {
                                authorsList.add(authors.getString(j));
                            }
                        } else {
                            authorsList.add(volume.optString("authors"));
                        }
                        // Create a new {@link Book} object
                        books.add(new Book(title, authorsList));
                    }
                }


                return books;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Books JSON results", e);
        }
        return null;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
