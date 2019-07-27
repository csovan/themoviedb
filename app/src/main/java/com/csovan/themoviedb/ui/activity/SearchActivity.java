package com.csovan.themoviedb.ui.activity;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.search.SearchAsyncTaskLoader;
import com.csovan.themoviedb.data.model.search.SearchResponse;
import com.csovan.themoviedb.data.model.search.SearchResult;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.SearchResultsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.csovan.themoviedb.util.Constant.QUERY;

public class SearchActivity extends AppCompatActivity {

    private String query;

    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar connectivitySnackbar;
    private boolean isBroadcastReceiverRegistered;
    private boolean isActivityLoaded;

    private RecyclerView searchResultsRecyclerView;
    private List<SearchResult> searchResultList;
    private SearchResultsAdapter searchResultsAdapter;

    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);

        Intent receivedIntent = getIntent();

        if (Intent.ACTION_SEARCH.equals(receivedIntent.getAction())){
            query = receivedIntent.getStringExtra(QUERY);
        }

        if (query == null || query.trim().isEmpty()) finish();

        setTitle(query);

        searchResultsRecyclerView = findViewById(R.id.recycler_view_search);
        searchResultsRecyclerView.setVisibility(View.INVISIBLE);

        searchResultList = new ArrayList<>();
        searchResultsAdapter = new SearchResultsAdapter(SearchActivity.this, searchResultList);

        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this,
                LinearLayoutManager.VERTICAL, false);
        searchResultsRecyclerView.setLayoutManager(linearLayoutManager);

        searchResultsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    loadSearchResults();
                    loading = true;
                }
            }
        });

        if (NetworkConnection.isConnected(SearchActivity.this)) {
            isActivityLoaded = true;
            loadSearchResults();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        searchResultsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityLoaded && !NetworkConnection.isConnected(SearchActivity.this)) {
            connectivitySnackbar = Snackbar.make(findViewById(R.id.frame_layout_search_results),
                    R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            connectivitySnackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    connectivitySnackbar.dismiss();
                    isActivityLoaded = true;
                    loadSearchResults();
                    isBroadcastReceiverRegistered = false;
                    unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            registerReceiver(connectivityBroadcastReceiver, intentFilter);
        } else if (!isActivityLoaded && NetworkConnection.isConnected(SearchActivity.this)) {
            isActivityLoaded = true;
            loadSearchResults();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if (isBroadcastReceiverRegistered){
            connectivitySnackbar.dismiss();
            isBroadcastReceiverRegistered = false;
            unregisterReceiver(connectivityBroadcastReceiver);
        }
    }

    private void loadSearchResults() {
        if (pagesOver) return;

        getLoaderManager().initLoader(presentPage, null, new LoaderManager.LoaderCallbacks<SearchResponse>() {

            @Override
            public Loader<SearchResponse> onCreateLoader(int i, Bundle bundle) {
                return new SearchAsyncTaskLoader(SearchActivity.this, query, String.valueOf(presentPage));
            }

            @Override
            public void onLoadFinished(Loader<SearchResponse> loader, SearchResponse searchResponse) {

                if (searchResponse == null) return;
                if (searchResponse.getResults() == null) return;

                progressBar.setVisibility(View.GONE);
                searchResultsRecyclerView.setVisibility(View.VISIBLE);

                for (SearchResult searchResult : searchResponse.getResults()) {
                    if (searchResult != null) {
                        searchResultList.add(searchResult);
                    }
                }
                searchResultsAdapter.notifyDataSetChanged();
                if (searchResponse.getPage().equals(searchResponse.getTotalPages()))
                    pagesOver = true;
                else
                    presentPage++;
            }

            @Override
            public void onLoaderReset(Loader<SearchResponse> loader) {

            }
        }).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setQueryHint(getResources().getString(R.string.search_hints));

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(
                this, SearchActivity.class)));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
