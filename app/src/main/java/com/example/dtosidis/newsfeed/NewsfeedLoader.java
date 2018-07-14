package com.example.dtosidis.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of Newsfeeds by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsfeedLoader extends AsyncTaskLoader<List<Newsfeed>> {

    private static final String LOG_TAG = NewsfeedLoader.class.getName();

    private String mUrl;

    /**
     * Constructs a new {@link NewsfeedLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsfeedLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Newsfeed> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of Newsfeeds.
        List<Newsfeed> newsfeeds = QueryUtils.fetchNewsfeedData(mUrl);
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return newsfeeds;
    }
}