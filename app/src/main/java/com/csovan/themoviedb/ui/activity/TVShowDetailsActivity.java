package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.csovan.themoviedb.data.model.tvshow.TVShow;
import com.csovan.themoviedb.data.model.tvshow.TVShowCastBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowCreditsResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowGenres;
import com.csovan.themoviedb.data.model.video.Video;
import com.csovan.themoviedb.data.model.video.VideosResponse;
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
import static com.csovan.themoviedb.util.Constant.TV_SHOW_ID;

public class TVShowDetailsActivity extends AppCompatActivity {

    private int tvshowId;
    private boolean tvshowDetailsLoaded;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;

    private ImageView backdropImageView;
    private ImageView posterImageView;

    private TextView tvshowTitle;
    private TextView tvshowRating;
    private TextView firstAirDate;
    private TextView tvshowRuntime;
    private TextView tvshowOverview;
    private TextView tvshowGenres;

    private RecyclerView videoRecyclerView;
    private List<Video> videoList;
    private VideoAdapter videoAdapter;

    private List<TVShowCastBrief> tvshowCastBriefList;
    private RecyclerView tvshowCastRecyclerView;
    private TVShowCastAdapter tvshowCastAdapter;

    private Call<TVShow> tvshowDetailCall;
    private Call<VideosResponse> videosResponseCall;
    private Call<TVShowCreditsResponse> tvshowCreditsResponseCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_show_details);

        Toolbar toolbar = findViewById(R.id.toolbar_tv_show_details);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("");
        tvshowDetailsLoaded = false;

        Intent receivedIntent = getIntent();
        tvshowId = receivedIntent.getIntExtra(TV_SHOW_ID, -1);

        if (tvshowId == -1) finish();

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_tv_show_details);
        appBarLayout = findViewById(R.id.app_bar_tv_show_details);
        progressBar = findViewById(R.id.progress_bar);
        nestedScrollView = findViewById(R.id.nested_scroll_view_tv_show_details);

        collapsingToolbarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);

        backdropImageView = findViewById(R.id.image_view_backdrop);
        posterImageView = findViewById(R.id.image_view_poster);

        tvshowTitle = findViewById(R.id.text_view_tv_show_title);
        tvshowRating = findViewById(R.id.text_view_rating);
        firstAirDate = findViewById(R.id.text_view_release_date);
        tvshowRuntime = findViewById(R.id.text_view_runtime);
        tvshowOverview = findViewById(R.id.text_view_overview_content_section);
        tvshowGenres = findViewById(R.id.text_view_genres);

        tvshowCastRecyclerView = findViewById(R.id.recycler_view_cast);
        tvshowCastBriefList = new ArrayList<>();
        tvshowCastAdapter = new TVShowCastAdapter(TVShowDetailsActivity.this, tvshowCastBriefList);
        tvshowCastRecyclerView.setAdapter(tvshowCastAdapter);
        tvshowCastRecyclerView.setLayoutManager(new LinearLayoutManager(TVShowDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));

        videoRecyclerView = findViewById(R.id.recycler_view_videos);
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(TVShowDetailsActivity.this, videoList);
        videoRecyclerView.setAdapter(videoAdapter);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(TVShowDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));

        loadActivity();
    }

    private void loadActivity(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        tvshowDetailCall = apiService.getTVShowDetails(tvshowId, TMDB_API_KEY, "US");
        tvshowDetailCall.enqueue(new Callback<TVShow>() {
            @Override
            public void onResponse(@NonNull Call<TVShow> call, @NonNull final Response<TVShow> response) {
                if (!response.isSuccessful()) {
                    tvshowDetailCall = call.clone();
                    tvshowDetailCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                //Get tv show title
                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (response.body().getName() != null){
                            if (appBarLayout.getTotalScrollRange() + verticalOffset == 0){
                                collapsingToolbarLayout.setTitle(response.body().getName());
                                tvshowTitle.setText("");
                            }else {
                                collapsingToolbarLayout.setTitle("");
                                tvshowTitle.setText(response.body().getName());
                            }
                        }else {
                            tvshowTitle.setText("");
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

                // Get tv show first air date and re-format
                if (response.body().getFirstAirDate() != null){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                    try {
                        Date releaseDate = sdf1.parse(response.body().getFirstAirDate());
                        firstAirDate.setText(sdf2.format(releaseDate));
                    }catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else{
                    firstAirDate.setText("");
                }

                // Get tv show rating
                if (response.body().getVoteAverage() != null
                        && response.body().getVoteAverage() != 0){
                    tvshowRating.setText(String.valueOf(response.body().getVoteAverage()));
                }

                List<Integer> runtime = response.body().getEpisodeRunTime();
                String runtimeFormat;
                if (runtime != null && !runtime.isEmpty() && runtime.get(0) != 0){
                    if (runtime.get(0) < 60){
                        runtimeFormat = runtime.get(0) + " mins";
                    }else{
                        runtimeFormat = runtime.get(0) / 60 + " hr " + runtime.get(0) % 60 + " mins";
                    }
                    tvshowRuntime.setText(runtimeFormat);
                } else{
                    tvshowRuntime.setText("");
                }

                if (response.body().getOverview() != null)
                    tvshowOverview.setText(response.body().getOverview());
                else
                    tvshowOverview.setText("");

                setGenres(response.body().getGenres());
                setVideos();
                setCasts();

                tvshowDetailsLoaded = true;
                checkTVShowDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<TVShow> call, @NonNull Throwable t) {

            }
        });

    }

    private void setVideos(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        videosResponseCall = apiService.getTVShowVideos(tvshowId, TMDB_API_KEY, "US");
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

    private void setGenres(List<TVShowGenres> genresList) {
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
        tvshowGenres.setText(genres);
    }

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
                        tvshowCastBriefList.add(castBrief);
                }

                if (!tvshowCastBriefList.isEmpty()){
                    tvshowCastAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TVShowCreditsResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void checkTVShowDetailsLoaded(){
        if (tvshowDetailsLoaded){
            progressBar.setVisibility(View.GONE);
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
