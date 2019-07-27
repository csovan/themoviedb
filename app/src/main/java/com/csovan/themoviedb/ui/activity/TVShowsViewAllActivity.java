package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.api.ApiClient;
import com.csovan.themoviedb.data.api.ApiInterface;
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowsAiringTodayResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsOnTheAirResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsPopularResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsTopRatedResponse;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.TVShowCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;
import static com.csovan.themoviedb.util.Constant.AIRING_TODAY_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.ON_THE_AIR_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.POPULAR_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.REGION;
import static com.csovan.themoviedb.util.Constant.TOP_RATED_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.VIEW_ALL_TV_SHOWS_TYPE;

public class TVShowsViewAllActivity extends AppCompatActivity {

    private RecyclerView viewAllRecyclerView;
    private List<TVShowBrief> tvshowList;
    private TVShowCardSmallAdapter tvshowCardSmallAdapter;

    private int tvshowsTypeList;

    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    private boolean isTVShowsLoaded;
    private boolean isBroadcastReceiverRegistered;

    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar connectivitySnackbar;

    private Call<TVShowsOnTheAirResponse> tvShowsOnTheAirResponseCall;
    private Call<TVShowsPopularResponse> tvShowsPopularResponseCall;
    private Call<TVShowsAiringTodayResponse> tvShowsAiringTodayResponseCall;
    private Call<TVShowsTopRatedResponse> tvShowsTopRatedResponseCall;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_shows_view_all);

        Toolbar toolbar = findViewById(R.id.toolbar_view_all);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);

        Intent receivedIntent = getIntent();

        tvshowsTypeList = receivedIntent.getIntExtra(VIEW_ALL_TV_SHOWS_TYPE, -1);

        if (tvshowsTypeList == -1) finish();

        switch (tvshowsTypeList){
            case ON_THE_AIR_TV_SHOWS_TYPE:
                setTitle(R.string.tv_shows_on_the_air);
                break;
            case POPULAR_TV_SHOWS_TYPE:
                setTitle(R.string.tv_shows_popular);
                break;
            case AIRING_TODAY_TV_SHOWS_TYPE:
                setTitle(R.string.tv_shows_airing_today);
                break;
            case TOP_RATED_TV_SHOWS_TYPE:
                setTitle(R.string.tv_shows_top_rated);
                break;
        }

        viewAllRecyclerView = findViewById(R.id.recycler_view_tv_shows_view_all);
        viewAllRecyclerView.setVisibility(View.INVISIBLE);

        tvshowList = new ArrayList<>();

        tvshowCardSmallAdapter = new TVShowCardSmallAdapter(TVShowsViewAllActivity.this, tvshowList);

        viewAllRecyclerView.setAdapter(tvshowCardSmallAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(
                TVShowsViewAllActivity.this, 3);

        viewAllRecyclerView.setLayoutManager(gridLayoutManager);

        viewAllRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    loadTVShows(tvshowsTypeList);
                    loading = true;
                }
            }
        });

        if (NetworkConnection.isConnected(TVShowsViewAllActivity.this)) {
            isTVShowsLoaded = true;
            loadTVShows(tvshowsTypeList);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        tvshowCardSmallAdapter.notifyDataSetChanged();
    }

    protected void onResume() {
        super.onResume();

        if (!isTVShowsLoaded && !NetworkConnection.isConnected(TVShowsViewAllActivity.this)) {
            connectivitySnackbar = Snackbar.make(findViewById(R.id.frame_layout_tv_shows_view_all),
                    R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            connectivitySnackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    connectivitySnackbar.dismiss();
                    isTVShowsLoaded = true;
                    loadTVShows(tvshowsTypeList);
                    isBroadcastReceiverRegistered = false;
                    unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            registerReceiver(connectivityBroadcastReceiver, intentFilter);
        } else if (!isTVShowsLoaded && NetworkConnection.isConnected(TVShowsViewAllActivity.this)) {
            isTVShowsLoaded = true;
            loadTVShows(tvshowsTypeList);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tvShowsAiringTodayResponseCall != null) tvShowsAiringTodayResponseCall.cancel();
        if (tvShowsOnTheAirResponseCall != null) tvShowsOnTheAirResponseCall.cancel();
        if (tvShowsPopularResponseCall != null) tvShowsPopularResponseCall.cancel();
        if (tvShowsTopRatedResponseCall != null) tvShowsTopRatedResponseCall.cancel();
    }

    private void loadTVShows(int tvshowsTypeList) {

        if (pagesOver) return;

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        switch (tvshowsTypeList){
            case ON_THE_AIR_TV_SHOWS_TYPE:
                tvShowsOnTheAirResponseCall = apiService.getTVShowsOnTheAir(TMDB_API_KEY, presentPage,REGION);
                tvShowsOnTheAirResponseCall.enqueue(new Callback<TVShowsOnTheAirResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TVShowsOnTheAirResponse> call, @NonNull Response<TVShowsOnTheAirResponse> response) {
                        if (!response.isSuccessful()){
                            tvShowsOnTheAirResponseCall = call.clone();
                            tvShowsOnTheAirResponseCall.enqueue(this);
                            return;
                        }
                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {
                            if (tvShowBrief != null && tvShowBrief.getPosterPath() != null)
                                tvshowList.add(tvShowBrief);
                        }
                        tvshowCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TVShowsOnTheAirResponse> call, @NonNull Throwable t) {

                    }
                });
                break;

            case POPULAR_TV_SHOWS_TYPE:
                tvShowsPopularResponseCall = apiService.getTVShowsPopular(TMDB_API_KEY, presentPage,REGION);
                tvShowsPopularResponseCall.enqueue(new Callback<TVShowsPopularResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TVShowsPopularResponse> call, @NonNull Response<TVShowsPopularResponse> response) {
                        if (!response.isSuccessful()){
                            tvShowsPopularResponseCall = call.clone();
                            tvShowsPopularResponseCall.enqueue(this);
                            return;
                        }
                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {
                            if (tvShowBrief != null && tvShowBrief.getPosterPath() != null)
                                tvshowList.add(tvShowBrief);
                        }
                        tvshowCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TVShowsPopularResponse> call, @NonNull Throwable t) {

                    }
                });
                break;

            case AIRING_TODAY_TV_SHOWS_TYPE:
                tvShowsAiringTodayResponseCall = apiService.getTVShowsAiringToday(TMDB_API_KEY, presentPage,REGION);
                tvShowsAiringTodayResponseCall.enqueue(new Callback<TVShowsAiringTodayResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TVShowsAiringTodayResponse> call, @NonNull Response<TVShowsAiringTodayResponse> response) {
                        if (!response.isSuccessful()){
                            tvShowsAiringTodayResponseCall = call.clone();
                            tvShowsAiringTodayResponseCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {
                            if (tvShowBrief != null && tvShowBrief.getPosterPath() != null)
                                tvshowList.add(tvShowBrief);

                        }
                        tvshowCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TVShowsAiringTodayResponse> call, @NonNull Throwable t) {

                    }
                });
                break;

            case TOP_RATED_TV_SHOWS_TYPE:
                tvShowsTopRatedResponseCall = apiService.getTVShowsTopRated(TMDB_API_KEY, presentPage,REGION);
                tvShowsTopRatedResponseCall.enqueue(new Callback<TVShowsTopRatedResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TVShowsTopRatedResponse> call, @NonNull Response<TVShowsTopRatedResponse> response) {
                        if (!response.isSuccessful()){
                            tvShowsTopRatedResponseCall = call.clone();
                            tvShowsTopRatedResponseCall.enqueue(this);
                            return;
                        }
                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (TVShowBrief tvShowBrief : response.body().getResults()) {
                            if (tvShowBrief != null && tvShowBrief.getPosterPath() != null)
                                tvshowList.add(tvShowBrief);
                        }
                        tvshowCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TVShowsTopRatedResponse> call, @NonNull Throwable t) {

                    }
                });
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
