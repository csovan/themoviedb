package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.csovan.themoviedb.data.model.movie.MovieCastBrief;
import com.csovan.themoviedb.data.model.movie.MovieCreditsResponse;
import com.csovan.themoviedb.data.model.movie.MovieCrewBrief;
import com.csovan.themoviedb.data.model.movie.MovieGenres;
import com.csovan.themoviedb.data.model.video.Video;
import com.csovan.themoviedb.data.model.video.VideosResponse;
import com.csovan.themoviedb.ui.adapter.MovieCastAdapter;
import com.csovan.themoviedb.ui.adapter.MovieCrewAdapter;
import com.csovan.themoviedb.ui.adapter.VideoAdapter;

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

public class MovieDetailsActivity extends AppCompatActivity {

    private int movieId;
    private boolean movieDetailsLoaded;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;

    // ImageView
    private ImageView backdropImageView;
    private ImageView posterImageView;

    // TextView
    private TextView movieTitle;
    private TextView movieReleaseDate;
    private TextView movieRuntime;
    private TextView movieOverview;
    private TextView movieGenres;
    private TextView movieRating;

    // Videos
    private RecyclerView videoRecyclerView;
    private List<Video> videoList;
    private VideoAdapter videoAdapter;

    // Cast
    private List<MovieCastBrief> movieCastBriefList;
    private RecyclerView movieCastRecyclerView;
    private MovieCastAdapter movieCastAdapter;

    // Crew
    private List<MovieCrewBrief> movieCrewBriefList;
    private RecyclerView movieCrewRecyclerView;
    private MovieCrewAdapter movieCrewAdapter;

    // Calls
    private Call<Movie> movieDetailsCall;
    private Call<VideosResponse> videosResponseCall;
    private Call<MovieCreditsResponse> movieCreditsResponseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_movie_details);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("");
        movieDetailsLoaded = false;

        // Receive intent movie id
        Intent receivedIntent = getIntent();
        movieId = receivedIntent.getIntExtra(MOVIE_ID,-1);

        if (movieId == -1) finish();

        // Set findViewById
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_movie_details);
        appBarLayout = findViewById(R.id.app_bar_movie_details);
        progressBar = findViewById(R.id.progress_bar);
        nestedScrollView = findViewById(R.id.nested_scroll_view_movie_details);

        collapsingToolbarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);

        backdropImageView = findViewById(R.id.image_view_backdrop);
        posterImageView = findViewById(R.id.image_view_poster);

        movieTitle = findViewById(R.id.text_view_movie_title);
        movieReleaseDate = findViewById(R.id.text_view_release_date);
        movieRuntime = findViewById(R.id.text_view_runtime);
        movieOverview = findViewById(R.id.text_view_overview_content_section);
        movieGenres = findViewById(R.id.text_view_genres);
        movieRating = findViewById(R.id.text_view_rating);

        // Set adapter videos
        videoRecyclerView = findViewById(R.id.recycler_view_videos);
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(MovieDetailsActivity.this, videoList);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));

        // Set adapter cast
        movieCastRecyclerView = findViewById(R.id.recycler_view_cast);
        movieCastBriefList = new ArrayList<>();
        movieCastAdapter = new MovieCastAdapter(MovieDetailsActivity.this, movieCastBriefList);
        movieCastRecyclerView.setAdapter(movieCastAdapter);
        movieCastRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));

        // Set adapter crew
        movieCrewRecyclerView = findViewById(R.id.recycler_view_crew);
        movieCrewBriefList = new ArrayList<>();
        movieCrewAdapter = new MovieCrewAdapter(MovieDetailsActivity.this, movieCrewBriefList);
        movieCrewRecyclerView.setAdapter(movieCrewAdapter);
        movieCrewRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));

        loadActivity();

    }

    private void loadActivity(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        movieDetailsCall = apiService.getMovieDetails(movieId, TMDB_API_KEY, "US");
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
                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (response.body().getTitle() != null) {
                            if (appBarLayout.getTotalScrollRange() + verticalOffset == 0){
                                collapsingToolbarLayout.setTitle(response.body().getTitle());
                                movieTitle.setText("");
                            } else{
                                collapsingToolbarLayout.setTitle("");
                                movieTitle.setText(response.body().getTitle());
                            }
                        } else {
                           movieTitle.setText("");
                        }
                    }
                });

                assert response.body() != null;
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getBackdropPath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_film)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(backdropImageView);

                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getPosterPath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_film)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(posterImageView);

                // Get movie release date with simple date format
                if (response.body().getReleaseDate() != null){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                    try{
                        Date releaseDate = sdf1.parse(response.body().getReleaseDate());
                        movieReleaseDate.setText(sdf2.format(releaseDate));
                    }catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    movieReleaseDate.setText("");
                }

                if (response.body().getVoteAverage() != null
                        && response.body().getVoteAverage() != 0){
                    movieRating.setText(String.valueOf(response.body().getVoteAverage()));
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
                    movieRuntime.setText(runtimeFormat);
                } else{
                    movieRuntime.setText("");
                }

                // Get movie overview
                if (response.body().getOverview() != null)
                    movieOverview.setText(response.body().getOverview());
                else
                    movieOverview.setText("");

                setGenres(response.body().getGenres());
                setVideos();
                setCasts();
                setCrews();

                movieDetailsLoaded = true;
                checkMovieDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

            }
        });

    }

    private void setVideos(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        videosResponseCall = apiService.getMovieVideos(movieId, TMDB_API_KEY, "US");
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

                for (Video video : response.body().getVideos()) {
                    if (video != null && video.getSite() != null && video.getSite().equals("YouTube")
                            && video.getType() != null && video.getType().equals("Trailer"))
                        videoList.add(video);
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void setGenres(List<MovieGenres> genresList) {
        String genres = "";
        if (genresList != null) {
            for (int i = 0; i < genresList.size(); i++) {
                if (genresList.get(i) == null) continue;
                if (i == genresList.size() - 1) {
                    genres = genres.concat(genresList.get(i).getGenreName());
                } else {
                    genres = genres.concat(genresList.get(i).getGenreName() + " | ");
                }
            }
        }
        movieGenres.setText(genres);
    }

    private void setCasts(){
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

                for (MovieCastBrief castBrief : response.body().getCasts()) {
                    if (castBrief != null && castBrief.getName() != null)
                        movieCastBriefList.add(castBrief);
                }

                if (!movieCastBriefList.isEmpty()){
                    movieCastAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieCreditsResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void setCrews(){
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
                if (response.body().getCrews() == null) return;

                for (MovieCrewBrief crewBrief : response.body().getCrews()) {
                    if (crewBrief != null && crewBrief.getName() != null)
                        movieCrewBriefList.add(crewBrief);
                }

                if (!movieCrewBriefList.isEmpty()){
                    movieCrewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieCreditsResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void checkMovieDetailsLoaded(){
        if (movieDetailsLoaded){
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
