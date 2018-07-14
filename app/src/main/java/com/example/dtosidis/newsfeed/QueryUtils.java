package com.example.dtosidis.newsfeed;

import android.text.TextUtils;
import android.util.Log;

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
import java.util.List;

/**
 * Helper methods related to requesting and receiving Newsfeed data from Guardian api.
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Newsfeed} objects.
     */
    public static List<Newsfeed> fetchNewsfeedData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Newsfeed}s
        List<Newsfeed> Newsfeeds = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Newsfeed}s
        return Newsfeeds;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(R.string.read_timeout);
            urlConnection.setConnectTimeout(R.string.connect_timeout);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Newsfeed JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
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
     * Return a list of {@link Newsfeed} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Newsfeed> extractFeatureFromJson(String NewsfeedJSON) {
        if (TextUtils.isEmpty(NewsfeedJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Newsfeeds to
        List<Newsfeed> Newsfeeds = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(NewsfeedJSON);

            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features (or Newsfeeds).
            JSONArray NewsfeedArray = response.getJSONArray("results");

            // For each Newsfeed in the NewsfeedArray, create an {@link Newsfeed} object
            for (int i = 0; i < NewsfeedArray.length(); i++) {

                // Get a single Newsfeed at position i within the list of Newsfeeds
                JSONObject currentNewsfeed = NewsfeedArray.getJSONObject(i);

                // Extract the value for the key called "section"
                String section = currentNewsfeed.getString("sectionName");

                // Extract the value for the key called "title"
                String title = currentNewsfeed.getString("webTitle");

                JSONArray tags = currentNewsfeed.getJSONArray("tags");

                String authorName = null;
                String authorSurname = null;
                for (int j = 0; j < tags.length(); j++) {
                    JSONObject currentAuthor = tags.getJSONObject(j);
                    authorName = currentAuthor.getString("firstName");
                    authorSurname = currentAuthor.getString("lastName");
                }

                // Extract the value for the key called "time"
                String time = currentNewsfeed.getString("webPublicationDate");

                // Extract the value for the key called "url"
                String url = currentNewsfeed.getString("webUrl");


                // Create a new {@link Newsfeed} object with the magnitude, location, time,
                // and url from the JSON response.
                Newsfeed Newsfeed = new Newsfeed(section, title, authorName, authorSurname, time, url);

                // Add the new {@link Newsfeed} to the list of Newsfeeds.
                Newsfeeds.add(Newsfeed);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the Newsfeed JSON results", e);
        }

        // Return the list of Newsfeeds
        return Newsfeeds;
    }
}