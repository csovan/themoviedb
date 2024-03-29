package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.csovan.themoviedb.data.database.Favorites;
import com.csovan.themoviedb.data.model.review.Review;
import com.csovan.themoviedb.data.model.review.ReviewsResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowCreator;
import com.csovan.themoviedb.data.model.tvshow.TVShowNetwork;
import com.csovan.themoviedb.data.model.tvshow.TVShowNextEpisode;
import com.csovan.themoviedb.data.model.tvshow.TVShowsSimilarResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShow;
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowCastBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowCreditsResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowGenres;
import com.csovan.themoviedb.data.model.video.Video;
import com.csovan.themoviedb.data.model.video.VideosResponse;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.ReviewAdapter;
import com.csovan.themoviedb.ui.adapter.TVShowCardSmallAdapter;
import com.csovan.themoviedb.ui.adapter.TVShowCastAdapter;
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
import static com.csovan.themoviedb.util.Constant.REGION;
import static com.csovan.themoviedb.util.Constant.TV_SHOW_ID;

public class TVShowDetailsActivity extends AppCompatActivity {

    private int tvshowId;
    private String posterPath;
    private String tvshowTitle;

    private boolean tvshowDetailsLoaded;
    private boolean reviewsSectionLoaded;
    private boolean videosSectionLoaded;
    private boolean castsSectionLoaded;
    private boolean similarTVShowsSectionLoaded;
    private boolean isActivityLoaded;
    private boolean isBroadcastReceiverRegistered;
    private boolean isOverviewTextViewClicked;
    private boolean isGenresTextViewClicked;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar snackbar;

    // ImageView
    private ImageView imageViewBackdrop;
    private ImageView imageViewPoster;

    // TextView
    private TextView textViewTVShowCreatorName;
    private TextView textViewTVShowFirstAirDate;
    private TextView textViewTVShowEpisodeRuntime;
    private TextView textViewTVShowRating;
    private TextView textViewTVShowNetwork;
    private TextView textViewTVShowNextEpisode;
    private TextView textViewTVShowGenres;
    private TextView textViewTVShowOverview;

    // No data available
    private TextView textViewNoDataAvailableCasts;
    private TextView textViewNoDataAvailableVideos;
    private TextView textViewNoDataAvailableOverview;
    private TextView textViewNoDataAvailableSimilarTVShows;
    private TextView textViewNoDataAvailableReviews;

    // Reviews
    private RecyclerView reviewRecyclerView;
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;

    // Videos
    private RecyclerView videoRecyclerView;
    private List<Video> videoList;
    private VideoAdapter videoAdapter;

    // Casts
    private List<TVShowCastBrief> tvshowCastBriefList;
    private RecyclerView tvshowCastRecyclerView;
    private TVShowCastAdapter tvshowCastAdapter;

    // Similar TV Shows
    private List<TVShowBrief> tvshowSimilarList;
    private RecyclerView tvshowsSimilarRecyclerView;
    private TVShowCardSmallAdapter tvshowsSimilarAdapter;

    // Retrofit network calls
    private Call<TVShow> tvshowDetailsCall;
    private Call<ReviewsResponse> reviewsResponseCall;
    private Call<VideosResponse> videosResponseCall;
    private Call<TVShowCreditsResponse> tvshowCreditsResponseCall;
    private Call<TVShowsSimilarResponse> similarTVShowsResponseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_show_details);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_tv_show_details);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("");

        // Receive intent tv show id
        Intent receivedIntent = getIntent();
        tvshowId = receivedIntent.getIntExtra(TV_SHOW_ID, -1);

        if (tvshowId == -1) finish();

        tvshowDetailsLoaded = false;

        // Set findViewByIds
        progressBar = findViewById(R.id.progress_bar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_tv_show_details);
        nestedScrollView = findViewById(R.id.nested_scroll_view_tv_show_details);

        collapsingToolbarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);

        imageViewBackdrop = findViewById(R.id.image_view_tv_show_details_backdrop);
        imageViewPoster = findViewById(R.id.image_view_tv_show_details_poster);

        textViewTVShowCreatorName = findViewById(R.id.text_view_creator_name);
        textViewTVShowFirstAirDate = findViewById(R.id.text_view_first_air_date);
        textViewTVShowEpisodeRuntime = findViewById(R.id.text_view_episode_runtime);
        textViewTVShowRating = findViewById(R.id.text_view_tv_show_details_rating);
        textViewTVShowNetwork = findViewById(R.id.text_view_network);
        textViewTVShowNextEpisode = findViewById(R.id.text_view_next_episode);
        textViewTVShowGenres = findViewById(R.id.text_view_tv_show_details_genres);
        textViewTVShowOverview = findViewById(R.id.text_view_overview_content_section);

        textViewNoDataAvailableCasts = findViewById(R.id.text_view_no_data_available_cast);
        textViewNoDataAvailableOverview = findViewById(R.id.text_view_overview_no_data);
        textViewNoDataAvailableVideos = findViewById(R.id.text_view_video_no_data);
        textViewNoDataAvailableSimilarTVShows = findViewById(R.id.text_view_similar_tv_shows_no_data);
        textViewNoDataAvailableReviews = findViewById(R.id.text_view_reviews_no_data);

        // Set adapter reviews
        reviewRecyclerView = findViewById(R.id.recycler_view_reviews);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(TVShowDetailsActivity.this, reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(TVShowDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        reviewsSectionLoaded = false;

        // Set adapter videos
        videoRecyclerView = findViewById(R.id.recycler_view_videos);
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(TVShowDetailsActivity.this, videoList);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(TVShowDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        videosSectionLoaded = false;

        // Set adapter cast
        tvshowCastRecyclerView = findViewById(R.id.recycler_view_cast);
        tvshowCastBriefList = new ArrayList<>();
        tvshowCastAdapter = new TVShowCastAdapter(TVShowDetailsActivity.this, tvshowCastBriefList);
        tvshowCastRecyclerView.setAdapter(tvshowCastAdapter);
        tvshowCastRecyclerView.setLayoutManager(new LinearLayoutManager(TVShowDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        castsSectionLoaded = false;

        // Set adapter similar tv shows
        tvshowsSimilarRecyclerView = findViewById(R.id.recycler_view_similar_tv_shows);
        tvshowSimilarList = new ArrayList<>();
        tvshowsSimilarAdapter = new TVShowCardSmallAdapter(TVShowDetailsActivity.this,tvshowSimilarList);
        tvshowsSimilarRecyclerView.setAdapter(tvshowsSimilarAdapter);
        tvshowsSimilarRecyclerView.setLayoutManager(new LinearLayoutManager(TVShowDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        similarTVShowsSectionLoaded = false;

        // Set onClickerListener to overview textView
        textViewTVShowOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOverviewTextViewClicked){
                    textViewTVShowOverview.setMaxLines(5);
                    isOverviewTextViewClicked = false;
                }else {
                    textViewTVShowOverview.setMaxLines(Integer.MAX_VALUE);
                    isOverviewTextViewClicked = true;
                }
            }
        });

        // Set onClickListener to genres textView to expand or collapse
        textViewTVShowGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGenresTextViewClicked){
                    textViewTVShowGenres.setMaxLines(1);
                    isGenresTextViewClicked = false;
                }else {
                    textViewTVShowGenres.setMaxLines(Integer.MAX_VALUE);
                    isGenresTextViewClicked = true;
                }
            }
        });

        if (NetworkConnection.isConnected(TVShowDetailsActivity.this)){
            isActivityLoaded = true;
            loadActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        tvshowsSimilarAdapter.notifyDataSetChanged();
        videoAdapter.notifyDataSetChanged();
        tvshowCastAdapter.notifyDataSetChanged();
        reviewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityLoaded && !NetworkConnection.isConnected(TVShowDetailsActivity.this)) {
            snackbar = Snackbar.make(collapsingToolbarLayout, R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    snackbar.dismiss();
                    isActivityLoaded = true;
                    loadActivity();
                    isBroadcastReceiverRegistered = false;
                    unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            registerReceiver(connectivityBroadcastReceiver, intentFilter);
        } else if (!isActivityLoaded && NetworkConnection.isConnected(TVShowDetailsActivity.this)) {
            isActivityLoaded = true;
            loadActivity();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if (isBroadcastReceiverRegistered){
            snackbar.dismiss();
            isBroadcastReceiverRegistered = false;
            unregisterReceiver(connectivityBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (tvshowDetailsCall != null) tvshowDetailsCall.cancel();
        if (reviewsResponseCall != null) reviewsResponseCall.cancel();
        if (videosResponseCall != null) videosResponseCall.cancel();
        if (tvshowCreditsResponseCall != null) tvshowCreditsResponseCall.cancel();
        if (similarTVShowsResponseCall != null) similarTVShowsResponseCall.cancel();
    }

    private void loadActivity(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        tvshowDetailsCall = apiService.getTVShowDetails(tvshowId, TMDB_API_KEY, REGION);
        tvshowDetailsCall.enqueue(new Callback<TVShow>() {
            @Override
            public void onResponse(@NonNull Call<TVShow> call, @NonNull final Response<TVShow> response) {
                if (!response.isSuccessful()) {
                    tvshowDetailsCall = call.clone();
                    tvshowDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                // Get tv show title
                if (response.body().getName() != null
                        && !response.body().getName().trim().isEmpty()) {
                    collapsingToolbarLayout.setTitle(response.body().getName());
                }else {
                    collapsingToolbarLayout.setTitle(getString(R.string.no_title_available));
                }

                // Get tv show backdrop
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getBackdropPath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_film)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewBackdrop);

                // Get tv show poster
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getPosterPath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_film)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewPoster);

                // Get tv show overview
                if (response.body().getOverview() != null && !response.body().getOverview().trim().isEmpty()) {
                    textViewNoDataAvailableOverview.setVisibility(View.GONE);
                    textViewTVShowOverview.setText(response.body().getOverview());
                }
                else {
                    textViewTVShowOverview.setText("");
                }

                setFirstAirDate(response.body().getFirstAirDate());
                setNextEpisodeAirDate(response.body().getNextEpisode());
                setEpisodeRunTime(response.body().getEpisodeRunTime());
                setTVShowRating(response.body().getVoteAverage());
                setCreators(response.body().getCreators());
                setNetworks(response.body().getNetworks());
                setGenres(response.body().getGenres());
                setReviews();
                setVideos();
                setCasts();
                setSimilarTVShows();

                posterPath = response.body().getPosterPath();
                tvshowTitle = response.body().getName();

                tvshowDetailsLoaded = true;
                checkTVShowDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<TVShow> call, @NonNull Throwable t) {

            }
        });

    }

    // Get tv show first air date
    private void setFirstAirDate(String firstAirDateString){
        if (firstAirDateString != null && !firstAirDateString.trim().isEmpty()){

            SimpleDateFormat sdf1 = new SimpleDateFormat
                    ("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat
                    ("MMMM d, yyyy", Locale.getDefault());

            try {
                Date firstAirDate = sdf1.parse(firstAirDateString);
                textViewTVShowFirstAirDate.setText(sdf2.format(firstAirDate));
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            textViewTVShowFirstAirDate.setText("N/A");
        }
    }

    // Get tv show next episode air date
    private void setNextEpisodeAirDate(TVShowNextEpisode tvshowNextEpisode){

        if (tvshowNextEpisode != null){

            SimpleDateFormat sdf1 = new SimpleDateFormat
                    ("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat
                    ("MMMM d, yyyy", Locale.getDefault());

            try{
                Date nextAirDate = sdf1.parse(tvshowNextEpisode.getNextAirDate());
                textViewTVShowNextEpisode.setText(sdf2.format(nextAirDate));
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            textViewTVShowNextEpisode.setText("N/A");
        }
    }

    // Get tv show run time
    private void setEpisodeRunTime(List<Integer> episodeRunTime){

        String episodeRuntimeString;
        if (episodeRunTime != null && !episodeRunTime.isEmpty() && episodeRunTime.get(0) != 0){
            if (episodeRunTime.get(0) < 60){
                episodeRuntimeString = episodeRunTime.get(0) + " mins";
            }else{
                episodeRuntimeString = episodeRunTime.get(0) / 60 + " hr " + episodeRunTime.get(0) % 60 + " mins";
            }
            textViewTVShowEpisodeRuntime.setText(episodeRuntimeString);
        }else{
            textViewTVShowEpisodeRuntime.setText("N/A");
        }
    }

    // Get tv show rating
    private void setTVShowRating(Double rating){
        String ratingString;
        if (rating != null && rating != 0){
            ratingString = rating + "/10";
            textViewTVShowRating.setText(ratingString);
        }else {
            textViewTVShowRating.setText("N/A");
        }
    }

    // Get tv show genres
    private void setGenres(List<TVShowGenres> genresList) {
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
            textViewTVShowGenres.setText(genres);
        }else {
            textViewTVShowGenres.setText("N/A");
        }
    }

    // Get tv show networks
    private void setNetworks(List<TVShowNetwork> networkList){
        String networks = "";
        if (networkList != null && !networkList.isEmpty()){
            for (int i = 0; i < networkList.size(); i++){
                if (networkList.get(i) == null) continue;
                if (i == networkList.size() - 1){
                    networks = networks.concat(networkList.get(i).getName());
                }else {
                    networks = networks.concat(networkList.get(i).getName() + " \u00b7 ");
                }
            }
            textViewTVShowNetwork.setText(networks);
        }else {
            textViewTVShowNetwork.setText("N/A");
        }
    }

    // Get tv show creators
    private void setCreators(List<TVShowCreator> creatorList){
        String creators = "";
        if (creatorList != null && !creatorList.isEmpty()){
            for (int i = 0; i < creatorList.size(); i++){
                if (creatorList.get(i) == null) continue;
                if (i == creatorList.size() - 1){
                    creators = creators.concat(creatorList.get(i).getName());
                }else {
                    creators = creators.concat(creatorList.get(i).getName() + " \u00b7 ");
                }
            }
            textViewTVShowCreatorName.setText(creators);
        }else {
            textViewTVShowCreatorName.setText("N/A");
        }
    }

    // Get tv show reviews
    private void setReviews(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        reviewsResponseCall = apiService.getTVShowReviews(tvshowId, TMDB_API_KEY);
        reviewsResponseCall.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewsResponse> call, @NonNull Response<ReviewsResponse> response) {
                if (!response.isSuccessful()) {
                    reviewsResponseCall = call.clone();
                    reviewsResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getReviews() == null) return;

                for (Review review : response.body().getReviews()){
                    if (review != null && review.getAuthor() != null && !review.getAuthor().contains("tmdb")){
                        textViewNoDataAvailableReviews.setVisibility(View.GONE);
                        reviewList.add(review);
                    }
                }

                if (!reviewList.isEmpty()){
                    reviewAdapter.notifyDataSetChanged();
                }

                reviewsSectionLoaded = true;
                checkTVShowDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsResponse> call, @NonNull Throwable t) {

            }
        });

    }

    // Get videos
    private void setVideos(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        videosResponseCall = apiService.getTVShowVideos(tvshowId, TMDB_API_KEY);
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
                    if (video != null && video.getSite() != null && video.getSite().equals("YouTube"))
                        textViewNoDataAvailableVideos.setVisibility(View.GONE);
                        videoList.add(video);
                }

                if (!videoList.isEmpty()){
                    videoAdapter.notifyDataSetChanged();
                }

                videosSectionLoaded = true;
                checkTVShowDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable t) {

            }
        });
    }

    // Get tv show casts
    private void setCasts(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvshowCreditsResponseCall = apiService.getTVShowCredits(tvshowId, TMDB_API_KEY);
        tvshowCreditsResponseCall.enqueue(new Callback<TVShowCreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowCreditsResponse> call, @NonNull Response<TVShowCreditsResponse> response) {
                if (!response.isSuccessful()){
                    tvshowCreditsResponseCall = call.clone();
                    tvshowCreditsResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                for (TVShowCastBrief castBrief : response.body().getCasts()) {
                    if (castBrief != null && castBrief.getName() != null)
                        textViewNoDataAvailableCasts.setVisibility(View.GONE);
                        tvshowCastBriefList.add(castBrief);
                }

                if (!tvshowCastBriefList.isEmpty()){
                    tvshowCastAdapter.notifyDataSetChanged();
                }

                castsSectionLoaded = true;
                checkTVShowDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowCreditsResponse> call, @NonNull Throwable t) {

            }
        });
    }

    // Get similar tv shows
    private void setSimilarTVShows(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        similarTVShowsResponseCall = apiService.getTVShowsSimilar(tvshowId, TMDB_API_KEY, 1);
        similarTVShowsResponseCall.enqueue(new Callback<TVShowsSimilarResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsSimilarResponse> call, @NonNull Response<TVShowsSimilarResponse> response) {
                if (!response.isSuccessful()){
                    similarTVShowsResponseCall = call.clone();
                    similarTVShowsResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (TVShowBrief tvshowBrief : response.body().getResults()) {
                    if (tvshowBrief != null){
                        textViewNoDataAvailableSimilarTVShows.setVisibility(View.GONE);
                        tvshowSimilarList.add(tvshowBrief);
                    }
                }

                if (!tvshowSimilarList.isEmpty()){
                    tvshowsSimilarAdapter.notifyDataSetChanged();
                }

                similarTVShowsSectionLoaded = true;
                checkTVShowDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsSimilarResponse> call, @NonNull Throwable t) {

            }
        });
    }

    // Check if all details are loaded
    private void checkTVShowDetailsLoaded(){
        if (tvshowDetailsLoaded && reviewsSectionLoaded
                && videosSectionLoaded && castsSectionLoaded
                && similarTVShowsSectionLoaded){
            progressBar.setVisibility(View.GONE);
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);

        final MenuItem favoriteItem = menu.findItem(R.id.action_favorite);

        // If tv show is favorite set icon to favorite else set icon to favorite border
        if (Favorites.isTVShowFavorites(this, tvshowId)){
            favoriteItem.setIcon(R.drawable.ic_favorite)
                    .setTitle(R.string.action_favorite);
        }else {
            favoriteItem.setIcon(R.drawable.ic_favorite_border)
                    .setTitle(R.string.action_remove_from_favorites);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        if (item.getItemId() == R.id.action_favorite){
            onFavoriteSelected();
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onFavoriteSelected(){
        // If tv show is not favorite add it to favorites else remove tv show from favorites
        if (!Favorites.isTVShowFavorites(this, tvshowId)){
            Favorites.addTVShowToFavorites(TVShowDetailsActivity.this, tvshowId, posterPath, tvshowTitle);
            snackbar = Snackbar.make(collapsingToolbarLayout,
                    R.string.added_to_favorites, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.colorAccent));
            snackbar.show();
        }else {
            Favorites.removeTVShowFromFavorites(this, tvshowId);
            snackbar = Snackbar.make(collapsingToolbarLayout,
                    R.string.removed_from_favorites, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

}
