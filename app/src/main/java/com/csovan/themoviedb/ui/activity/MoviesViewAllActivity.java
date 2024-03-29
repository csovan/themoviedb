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
import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;
import com.csovan.themoviedb.data.model.movie.MoviesTopRatedResponse;
import com.csovan.themoviedb.data.model.movie.MoviesUpcomingResponse;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.MovieCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;
import static com.csovan.themoviedb.util.Constant.NOW_PLAYING_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.POPULAR_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.REGION;
import static com.csovan.themoviedb.util.Constant.TOP_RATED_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.UPCOMING_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.VIEW_ALL_MOVIES_TYPE;

public class MoviesViewAllActivity extends AppCompatActivity {

    private RecyclerView viewAllRecyclerView;
    private List<MovieBrief> movieList;
    private MovieCardSmallAdapter movieCardSmallAdapter;

    private int movieListType;

    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    private boolean isMoviesLoaded;
    private boolean isBroadcastReceiverRegistered;

    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar connectivitySnackbar;

    private Call<MoviesNowPlayingResponse> moviesNowPlayingResponseCall;
    private Call<MoviesPopularResponse> moviesPopularResponseCall;
    private Call<MoviesUpcomingResponse> moviesUpcomingResponseCall;
    private Call<MoviesTopRatedResponse> moviesTopRatedResponseCall;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_view_all);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_view_all);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_bar);

        Intent receivedIntent = getIntent();

        movieListType = receivedIntent.getIntExtra(VIEW_ALL_MOVIES_TYPE, -1);

        if (movieListType == -1) finish();

        switch (movieListType){
            case NOW_PLAYING_MOVIES_TYPE:
                setTitle(R.string.movies_now_playing);
                break;
            case POPULAR_MOVIES_TYPE:
                setTitle(R.string.movies_popular);
                break;
            case UPCOMING_MOVIES_TYPE:
                setTitle(R.string.movies_upcoming);
                break;
            case TOP_RATED_MOVIES_TYPE:
                setTitle(R.string.movies_top_rated);
                break;
        }

        viewAllRecyclerView = findViewById(R.id.recycler_view_movies_view_all);
        viewAllRecyclerView.setVisibility(View.INVISIBLE);

        movieList = new ArrayList<>();

        movieCardSmallAdapter = new MovieCardSmallAdapter(MoviesViewAllActivity.this, movieList);
        viewAllRecyclerView.setAdapter(movieCardSmallAdapter);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(
                MoviesViewAllActivity.this, 3);

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
                    loadMovies(movieListType);
                    loading = true;
                }
            }
        });

        if (NetworkConnection.isConnected(MoviesViewAllActivity.this)) {
            isMoviesLoaded = true;
            loadMovies(movieListType);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        movieCardSmallAdapter.notifyDataSetChanged();
    }

    protected void onResume() {
        super.onResume();

        if (!isMoviesLoaded && !NetworkConnection.isConnected(MoviesViewAllActivity.this)) {
            connectivitySnackbar = Snackbar.make(findViewById(R.id.frame_layout_movies_view_all),
                    R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            connectivitySnackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    connectivitySnackbar.dismiss();
                    isMoviesLoaded = true;
                    loadMovies(movieListType);
                    isBroadcastReceiverRegistered = false;
                    unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            registerReceiver(connectivityBroadcastReceiver, intentFilter);
        } else if (!isMoviesLoaded && NetworkConnection.isConnected(MoviesViewAllActivity.this)) {
            isMoviesLoaded = true;
            loadMovies(movieListType);
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

        if (moviesNowPlayingResponseCall != null) moviesNowPlayingResponseCall.cancel();
        if (moviesPopularResponseCall != null) moviesPopularResponseCall.cancel();
        if (moviesUpcomingResponseCall != null) moviesUpcomingResponseCall.cancel();
        if (moviesTopRatedResponseCall != null) moviesTopRatedResponseCall.cancel();
    }

    private void loadMovies(int movieListType){

        if (pagesOver) return;

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        switch (movieListType){
            case NOW_PLAYING_MOVIES_TYPE:
                moviesNowPlayingResponseCall = apiService.getMoviesNowPlaying(TMDB_API_KEY, presentPage, REGION);
                moviesNowPlayingResponseCall.enqueue(new Callback<MoviesNowPlayingResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MoviesNowPlayingResponse> call, @NonNull Response<MoviesNowPlayingResponse> response) {
                        if (!response.isSuccessful()){
                            moviesNowPlayingResponseCall = call.clone();
                            moviesNowPlayingResponseCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (MovieBrief movieBrief : response.body().getResults()) {
                            if (movieBrief != null && movieBrief.getPosterPath() != null)
                                movieList.add(movieBrief);
                        }
                        movieCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MoviesNowPlayingResponse> call, @NonNull Throwable t) {

                    }
                });
                break;

            case POPULAR_MOVIES_TYPE:
                moviesPopularResponseCall = apiService.getMoviesPopular(TMDB_API_KEY, presentPage,REGION);
                moviesPopularResponseCall.enqueue(new Callback<MoviesPopularResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MoviesPopularResponse> call, @NonNull Response<MoviesPopularResponse> response) {
                        if (!response.isSuccessful()){
                            moviesPopularResponseCall = call.clone();
                            moviesPopularResponseCall.enqueue(this);
                            return;
                        }
                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (MovieBrief movieBrief : response.body().getResults()) {
                            if (movieBrief != null && movieBrief.getPosterPath() != null)
                                movieList.add(movieBrief);
                        }
                        movieCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MoviesPopularResponse> call, @NonNull Throwable t) {

                    }
                });
                break;

            case UPCOMING_MOVIES_TYPE:
                moviesUpcomingResponseCall = apiService.getMoviesUpcoming(TMDB_API_KEY, presentPage,REGION);
                moviesUpcomingResponseCall.enqueue(new Callback<MoviesUpcomingResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MoviesUpcomingResponse> call, @NonNull Response<MoviesUpcomingResponse> response) {
                        if (!response.isSuccessful()){
                            moviesUpcomingResponseCall = call.clone();
                            moviesUpcomingResponseCall.enqueue(this);
                            return;
                        }
                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (MovieBrief movieBrief : response.body().getResults()) {
                            if (movieBrief != null && movieBrief.getPosterPath() != null)
                                movieList.add(movieBrief);
                        }
                        movieCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MoviesUpcomingResponse> call, @NonNull Throwable t) {

                    }
                });
                break;

            case TOP_RATED_MOVIES_TYPE:
                moviesTopRatedResponseCall = apiService.getMoviesTopRated(TMDB_API_KEY, presentPage,REGION);
                moviesTopRatedResponseCall.enqueue(new Callback<MoviesTopRatedResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MoviesTopRatedResponse> call, @NonNull Response<MoviesTopRatedResponse> response) {
                        if (!response.isSuccessful()){
                            moviesTopRatedResponseCall = call.clone();
                            moviesTopRatedResponseCall.enqueue(this);
                            return;
                        }
                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        progressBar.setVisibility(View.GONE);
                        viewAllRecyclerView.setVisibility(View.VISIBLE);

                        for (MovieBrief movieBrief : response.body().getResults()) {
                            if (movieBrief != null && movieBrief.getPosterPath() != null)
                                movieList.add(movieBrief);
                        }
                        movieCardSmallAdapter.notifyDataSetChanged();
                        if (response.body().getPage().equals(response.body().getTotalPages())){
                            pagesOver = true;
                        }else {
                            presentPage++;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MoviesTopRatedResponse> call, @NonNull Throwable t) {

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
