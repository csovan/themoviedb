package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.api.ApiClient;
import com.csovan.themoviedb.data.api.ApiInterface;
import com.csovan.themoviedb.data.model.movie.Movie;
import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.data.model.movie.MovieCastBrief;
import com.csovan.themoviedb.data.model.movie.MovieCreditsResponse;
import com.csovan.themoviedb.data.model.movie.MovieCrewBrief;
import com.csovan.themoviedb.data.model.movie.MovieGenres;
import com.csovan.themoviedb.data.model.movie.MoviesSimilarResponse;
import com.csovan.themoviedb.data.model.video.Video;
import com.csovan.themoviedb.data.model.video.VideosResponse;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.MovieCardSmallAdapter;
import com.csovan.themoviedb.ui.adapter.MovieCastAdapter;
import com.csovan.themoviedb.ui.adapter.MovieCrewAdapter;
import com.csovan.themoviedb.ui.adapter.VideoAdapter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;
import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_1280;
import static com.csovan.themoviedb.util.Constant.MOVIE_ID;
import static com.csovan.themoviedb.util.Constant.REGION;

public class MovieDetailsActivity extends AppCompatActivity {

    private int movieId;

    private boolean movieDetailsLoaded;
    private boolean videosSectionLoaded;
    private boolean creditsSectionLoaded;
    private boolean isActivityLoaded;
    private boolean isBroadcastReceiverRegistered;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar connectivitySnackbar;

    // ImageView
    private ImageView imageViewBackdrop;
    private ImageView imageViewPoster;

    // TextView
    private TextView textViewMovieDirectorName;
    private TextView textViewMovieReleaseDate;
    private TextView textViewMovieRuntime;
    private TextView textViewMovieRating;
    private TextView textViewMovieBudget;
    private TextView textViewMovieRevenue;
    private TextView textViewMovieGenres;
    private TextView textViewMovieOverview;

    // No data available
    private TextView textViewNoDataAvailableCasts;
    private TextView textViewNoDataAvailableVideos;
    private TextView textViewNoDataAvailableOverview;
    private TextView textViewNoDataAvailableSimilarMovies;

    // Videos
    private RecyclerView videoRecyclerView;
    private List<Video> videoList;
    private VideoAdapter videoAdapter;

    // Cast
    private List<MovieCastBrief> movieCastBriefList;
    private RecyclerView movieCastRecyclerView;
    private MovieCastAdapter movieCastAdapter;

    // Similar movies
    private List<MovieBrief> movieSimilarList;
    private RecyclerView moviesSimilarRecyclerView;
    private MovieCardSmallAdapter moviesSimilarAdapter;

    // Retrofit network calls
    private Call<Movie> movieDetailsCall;
    private Call<VideosResponse> videosResponseCall;
    private Call<MovieCreditsResponse> movieCreditsResponseCall;
    private Call<MoviesSimilarResponse> similarMoviesResponseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_movie_details);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("");

        // Receive intent movie id
        Intent receivedIntent = getIntent();
        movieId = receivedIntent.getIntExtra(MOVIE_ID,-1);

        if (movieId == -1) finish();

        movieDetailsLoaded = false;

        // Set findViewById
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_movie_details);
        progressBar = findViewById(R.id.progress_bar);
        nestedScrollView = findViewById(R.id.nested_scroll_view_movie_details);

        collapsingToolbarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);

        imageViewBackdrop = findViewById(R.id.image_view_backdrop);
        imageViewPoster = findViewById(R.id.image_view_poster);

        textViewMovieDirectorName = findViewById(R.id.text_view_director_name);
        textViewMovieReleaseDate = findViewById(R.id.text_view_release_date);
        textViewMovieRuntime = findViewById(R.id.text_view_runtime);
        textViewMovieRating = findViewById(R.id.text_view_rating);
        textViewMovieBudget = findViewById(R.id.text_view_budget);
        textViewMovieRevenue = findViewById(R.id.text_view_revenue);
        textViewMovieGenres = findViewById(R.id.text_view_genres);
        textViewMovieOverview = findViewById(R.id.text_view_overview_content_section);

        textViewNoDataAvailableCasts = findViewById(R.id.text_view_no_data_available_cast);
        textViewNoDataAvailableOverview = findViewById(R.id.text_view_overview_no_data);
        textViewNoDataAvailableVideos = findViewById(R.id.text_view_video_no_data);
        textViewNoDataAvailableSimilarMovies = findViewById(R.id.text_view_similar_movies_no_data);

        // Set adapter videos
        videoRecyclerView = findViewById(R.id.recycler_view_videos);
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(MovieDetailsActivity.this, videoList);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        videosSectionLoaded = false;

        // Set adapter cast
        movieCastRecyclerView = findViewById(R.id.recycler_view_cast);
        movieCastBriefList = new ArrayList<>();
        movieCastAdapter = new MovieCastAdapter(MovieDetailsActivity.this, movieCastBriefList);
        movieCastRecyclerView.setAdapter(movieCastAdapter);
        movieCastRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        creditsSectionLoaded = false;

        // Set adapter similar movies
        moviesSimilarRecyclerView = findViewById(R.id.recycler_view_similar_movies);
        movieSimilarList = new ArrayList<>();
        moviesSimilarAdapter = new MovieCardSmallAdapter(MovieDetailsActivity.this, movieSimilarList);
        moviesSimilarRecyclerView.setAdapter(moviesSimilarAdapter);
        moviesSimilarRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));

        if (NetworkConnection.isConnected(MovieDetailsActivity.this)) {
            isActivityLoaded = true;
            loadActivity();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        moviesSimilarAdapter.notifyDataSetChanged();
        videoAdapter.notifyDataSetChanged();
        movieCastAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityLoaded && !NetworkConnection.isConnected(MovieDetailsActivity.this)) {
            connectivitySnackbar = Snackbar.make(collapsingToolbarLayout, R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            connectivitySnackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    connectivitySnackbar.dismiss();
                    isActivityLoaded = true;
                    loadActivity();
                    isBroadcastReceiverRegistered = false;
                    unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            registerReceiver(connectivityBroadcastReceiver, intentFilter);
        } else if (!isActivityLoaded && NetworkConnection.isConnected(MovieDetailsActivity.this)) {
            isActivityLoaded = true;
            loadActivity();
        }
    }

    @Override
    public void onPause(){
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

        if (movieDetailsCall != null) movieDetailsCall.cancel();
        if (videosResponseCall != null) videosResponseCall.cancel();
        if (movieCreditsResponseCall != null) movieCreditsResponseCall.cancel();
        if (similarMoviesResponseCall != null) similarMoviesResponseCall.cancel();
    }

    private void loadActivity(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        movieDetailsCall = apiService.getMovieDetails(movieId, TMDB_API_KEY, REGION);
        movieDetailsCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull final Response<Movie> response) {
                if (!response.isSuccessful()) {
                    movieDetailsCall = call.clone();
                    movieDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                // Get movie title
                if (response.body().getTitle() != null
                        && !response.body().getTitle().trim().isEmpty()) {
                    collapsingToolbarLayout.setTitle(response.body().getTitle());
                }else {
                    collapsingToolbarLayout.setTitle(getString(R.string.no_title_available));
                }

                // Get movie backdrop
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getBackdropPath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_film)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewBackdrop);

                // Get movie poster
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getPosterPath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_film)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewPoster);

                // Get movie release date with simple date format
                if (response.body().getReleaseDate() != null
                        && !response.body().getReleaseDate().trim().isEmpty()){
                    SimpleDateFormat sdf1 = new SimpleDateFormat(
                            "yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat sdf2 = new SimpleDateFormat(
                            "MMMM d, yyyy", Locale.getDefault());
                    try{
                        Date releaseDate = sdf1.parse(response.body().getReleaseDate());
                        textViewMovieReleaseDate.setText(sdf2.format(releaseDate));
                    }catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    textViewMovieReleaseDate.setText("N/A");
                }

                // Get movie runtime and format it to hrs and mins
                Integer runtime = response.body().getRuntime();
                String runtimeFormat;
                if (runtime != null && runtime != 0){
                    if (runtime < 60){
                        runtimeFormat = runtime + " mins";
                    }else {
                        runtimeFormat = runtime /60 + " hr " + runtime % 60 + " mins";
                    }
                    textViewMovieRuntime.setText(runtimeFormat);
                } else{
                    textViewMovieRuntime.setText("N/A");
                }

                // Get movie rating
                Double rating = response.body().getVoteAverage();
                String ratingFormat;
                if (response.body().getVoteAverage() != null
                        && response.body().getVoteAverage() != 0){
                    ratingFormat = rating + "/10";
                    textViewMovieRating.setText(ratingFormat);
                }else {
                    textViewMovieRating.setText("N/A");
                }

                // Get movie budget
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                currencyFormat.setMinimumFractionDigits(0);

                int budget = response.body().getBudget();
                String budgetFormat = currencyFormat.format(budget) + " USD";
                if (response.body().getBudget() != 0){
                    textViewMovieBudget.setText(budgetFormat);
                }else {
                    textViewMovieBudget.setText("N/A");
                }

                //Get movie revenue
                long revenue = response.body().getRevenue();
                String revenueFormat = currencyFormat.format(revenue) + " USD";
                if (response.body().getRevenue() != 0){
                    textViewMovieRevenue.setText(revenueFormat);
                }else {
                    textViewMovieRevenue.setText("N/A");
                }

                // Get movie overview
                if (response.body().getOverview() != null
                        && !response.body().getOverview().trim().isEmpty()){
                    textViewNoDataAvailableOverview.setVisibility(View.GONE);
                    textViewMovieOverview.setText(response.body().getOverview());
                }
                else {
                    textViewMovieOverview.setText("");
                }

                setGenres(response.body().getGenres());
                setVideos();
                setCredits();
                setSimilarMovies();

                movieDetailsLoaded = true;
                checkMovieDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

            }
        });

    }

    // Get videos
    private void setVideos(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        videosResponseCall = apiService.getMovieVideos(movieId, TMDB_API_KEY, REGION);
        videosResponseCall.enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideosResponse> call, @NonNull Response<VideosResponse> response) {
                if (!response.isSuccessful()) {
                    videosResponseCall = call.clone();
                    videosResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getVideos() == null) return;

                videosSectionLoaded = true;
                checkMovieDetailsLoaded();

                for (Video video : response.body().getVideos()) {
                    if (video != null && video.getSite() != null && video.getSite().equals("YouTube")
                            && video.getType() != null && video.getType().equals("Trailer"))
                        textViewNoDataAvailableVideos.setVisibility(View.GONE);
                        videoList.add(video);
                }

                if (!videoList.isEmpty()){
                    videoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {

            }
        });
    }

    // Get movie genres
    private void setGenres(List<MovieGenres> genresList) {
        String genres = "";
        if (genresList != null) {
            for (int i = 0; i < genresList.size(); i++) {
                if (genresList.get(i) == null) continue;
                if (i == genresList.size() - 1) {
                    genres = genres.concat(genresList.get(i).getGenreName());
                } else {
                    genres = genres.concat(genresList.get(i).getGenreName() + " \u00b7 ");
                }
            }
        }else {
            textViewMovieGenres.setText("N/A");
        }
        textViewMovieGenres.setText(genres);
    }

    // Get movie credits
    private void setCredits(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        movieCreditsResponseCall = apiService.getMovieCredits(movieId, TMDB_API_KEY);
        movieCreditsResponseCall.enqueue(new Callback<MovieCreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieCreditsResponse> call, @NonNull Response<MovieCreditsResponse> response) {
                if (!response.isSuccessful()){
                    movieCreditsResponseCall = call.clone();
                    movieCreditsResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                creditsSectionLoaded = true;
                checkMovieDetailsLoaded();

                // Get movie casts
                for (MovieCastBrief castBrief : response.body().getCasts()) {
                    if (castBrief != null && castBrief.getName() != null)
                        textViewNoDataAvailableCasts.setVisibility(View.GONE);
                        movieCastBriefList.add(castBrief);
                }

                if (!movieCastBriefList.isEmpty()){
                    movieCastAdapter.notifyDataSetChanged();
                }

                // Get director name
                for (MovieCrewBrief crewBrief : response.body().getCrews()){
                    if (crewBrief.getJob().equals("Director")){
                        textViewMovieDirectorName.setText(crewBrief.getName());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieCreditsResponse> call, @NonNull Throwable t) {

            }
        });

    }

    // Get similar movies
    private void setSimilarMovies(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        similarMoviesResponseCall = apiService.getMoviesSimilar(movieId, TMDB_API_KEY, 1);
        similarMoviesResponseCall.enqueue(new Callback<MoviesSimilarResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesSimilarResponse> call, @NonNull Response<MoviesSimilarResponse> response) {
                if (!response.isSuccessful()){
                    similarMoviesResponseCall = call.clone();
                    similarMoviesResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (MovieBrief movieBrief : response.body().getResults()) {
                    if (movieBrief != null){
                        textViewNoDataAvailableSimilarMovies.setVisibility(View.GONE);
                        movieSimilarList.add(movieBrief);
                    }
                }

                if (!movieSimilarList.isEmpty()){
                    moviesSimilarAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesSimilarResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void checkMovieDetailsLoaded(){
        if (movieDetailsLoaded && videosSectionLoaded && creditsSectionLoaded){
            progressBar.setVisibility(View.GONE);
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
