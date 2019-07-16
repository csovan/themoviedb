package com.csovan.themoviedb.ui.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.search.SearchAsyncTaskLoader;
import com.csovan.themoviedb.data.model.search.SearchResponse;
import com.csovan.themoviedb.data.model.search.SearchResult;
import com.csovan.themoviedb.ui.adapter.SearchResultsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.csovan.themoviedb.util.Constant.QUERY;

public class SearchActivity extends AppCompatActivity {

    private String query;

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

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);

        Intent receivedIntent = getIntent();
        query = receivedIntent.getStringExtra(QUERY);

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

        loadSearchResults();
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
                    if (searchResult != null)
                        searchResultList.add(searchResult);
                }
                searchResultsAdapter.notifyDataSetChanged();
                if (searchResponse.getPage() == searchResponse.getTotalPages())
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
