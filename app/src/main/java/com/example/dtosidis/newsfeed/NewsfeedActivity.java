package com.example.dtosidis.newsfeed;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsfeedActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Newsfeed>> {

    private static final String LOG_TAG = NewsfeedActivity.class.getName();

    /** URL for Newsfeed data from the guardian api dataset */
    private static final String USGS_REQUEST_URL =
            "http://content.guardianapis.com/search?show-tags=contributor&order-by=newest&page-size=10&api-key=1a6c86d0-7270-4bc3-bd92-11c8c0d9db02";

    /**
     * Constant value for the Newsfeed loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWSFEED_LOADER_ID = 1;

    /** Adapter for the list of Newsfeeds */
    private NewsfeedAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed_activity);

        ListView newsfeedListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsfeedListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of Newsfeeds as input
        mAdapter = new NewsfeedAdapter(this, new ArrayList<Newsfeed>());

        newsfeedListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected Newsfeed.
        newsfeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current Newsfeed that was clicked on
                Newsfeed currentNewsfeed = mAdapter.getItem(position);

                Uri newsfeedUri = Uri.parse(currentNewsfeed.getUrl());

                // Create a new intent to view the Newsfeed URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsfeedUri);

                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NEWSFEED_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Newsfeed>> onCreateLoader(int i, Bundle bundle) {
        return new NewsfeedLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Newsfeed>> loader, List<Newsfeed> newsfeeds) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "Ex- No News updates available, please check back later or try again."
        mEmptyStateTextView.setText(R.string.no_newsfeeds);

        // Clear the adapter of previous Newsfeed data
        mAdapter.clear();

        // If there is a valid list of {@link Newsfeed}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsfeeds != null && !newsfeeds.isEmpty()) {
            mAdapter.addAll(newsfeeds);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Newsfeed>> loader) {
        mAdapter.clear();
    }
}