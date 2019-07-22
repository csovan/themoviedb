package com.csovan.themoviedb.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.api.ApiClient;
import com.csovan.themoviedb.data.api.ApiInterface;
import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;
import com.csovan.themoviedb.data.model.movie.MoviesTopRatedResponse;
import com.csovan.themoviedb.data.model.movie.MoviesUpcomingResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowsAiringTodayResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsOnTheAirResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsPopularResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsTopRatedResponse;
import com.csovan.themoviedb.ui.activity.MoviesViewAllActivity;
import com.csovan.themoviedb.ui.activity.TVShowsViewAllActivity;
import com.csovan.themoviedb.ui.adapter.MovieCardLargeAdapter;
import com.csovan.themoviedb.ui.adapter.MovieCardSmallAdapter;
import com.csovan.themoviedb.ui.adapter.TVShowCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;
import static com.csovan.themoviedb.util.Constant.AIRING_TODAY_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.NOW_PLAYING_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.ON_THE_AIR_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.POPULAR_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.POPULAR_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.TOP_RATED_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.TOP_RATED_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.UPCOMING_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.VIEW_ALL_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.VIEW_ALL_TV_SHOWS_TYPE;

public class HomeFragment extends Fragment {
    private ProgressBar progressBar;
    private LinearLayout homeLayout;

    // Movies now playing
    private List<MovieBrief> movieNowPlayingList;
    private Call<MoviesNowPlayingResponse> moviesNowPlayingResponseCall;
    private TextView tvMoviesNowPlayingViewAll;
    private RecyclerView rvMoviesNowPlaying;
    private MovieCardLargeAdapter moviesNowPlayingAdapter;
    private boolean moviesNowPlayingSectionLoaded;

    // TV shows on the air
    private List<TVShowBrief> tvshowOnTheAirList;
    private Call<TVShowsOnTheAirResponse> tvShowsOnTheAirResponseCall;
    private TextView tvTVShowsOnTheAirViewAll;
    private RecyclerView rvTVShowsOnTheAir;
    private TVShowCardSmallAdapter tvshowsOnTheAirAdapter;
    private boolean tvshowsOnTheAirSectionLoaded;

    // Movies popular
    private List<MovieBrief> moviePopularList;
    private Call<MoviesPopularResponse> moviesPopularResponseCall;
    private TextView tvMoviesPopularViewAll;
    private RecyclerView rvMoviesPopular;
    private MovieCardSmallAdapter moviesPopularAdapter;
    private boolean moviesPopularSectionLoaded;

    // TV shows popular
    private List<TVShowBrief> tvshowPopularList;
    private Call<TVShowsPopularResponse> tvShowsPopularResponseCall;
    private TextView tvTVShowsPopularViewAll;
    private RecyclerView rvTVShowsPopular;
    private TVShowCardSmallAdapter tvshowsPopularAdapter;
    private boolean tvshowsPopularSectionLoaded;

    // Movies upcoming
    private List<MovieBrief> movieUpcomingList;
    private Call<MoviesUpcomingResponse> moviesUpcomingResponseCall;
    private TextView tvMoviesUpcomingViewAll;
    private RecyclerView rvMoviesUpcoming;
    private MovieCardSmallAdapter moviesUpcomingAdapter;
    private boolean moviesUpcomingSectionLoaded;

    // TV shows airing today
    private List<TVShowBrief> tvshowAiringTodayList;
    private Call<TVShowsAiringTodayResponse> tvShowsAiringTodayResponseCall;
    private TextView tvTVShowsAiringTodayViewAll;
    private RecyclerView rvTVShowsAiringToday;
    private TVShowCardSmallAdapter tvshowsAiringTodayAdapter;
    private boolean tvshowsAiringTodaySectionLoaded;

    // Movies top rated
    private List<MovieBrief> movieTopRatedList;
    private Call<MoviesTopRatedResponse> moviesTopRatedResponseCall;
    private TextView tvMoviesTopRatedViewAll;
    private RecyclerView rvMoviesTopRated;
    private MovieCardSmallAdapter moviesTopRatedAdapter;
    private boolean moviesTopRatedSectionLoaded;

    // TV shows top rated
    private List<TVShowBrief> tvshowTopRatedList;
    private Call<TVShowsTopRatedResponse> tvShowsTopRatedResponseCall;
    private TextView tvTVShowsTopRatedViewAll;
    private RecyclerView rvTVShowsTopRated;
    private TVShowCardSmallAdapter tvshowsTopRatedAdapter;
    private boolean tvshowsTopRatedSectionLoaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        homeLayout = view.findViewById(R.id.linear_layout_home);
        homeLayout.setVisibility(View.GONE);

        // Movies now playing section
        tvMoviesNowPlayingViewAll = view.findViewById(R.id.text_view_movies_now_playing_view_all);
        rvMoviesNowPlaying = view.findViewById(R.id.recycler_view_movies_now_playing);

        movieNowPlayingList = new ArrayList<>();
        moviesNowPlayingAdapter = new MovieCardLargeAdapter(getContext(), movieNowPlayingList);
        rvMoviesNowPlaying.setAdapter(moviesNowPlayingAdapter);
        rvMoviesNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        (new LinearSnapHelper()).attachToRecyclerView(rvMoviesNowPlaying);

        moviesNowPlayingSectionLoaded = false;

        // TV shows on the air section
        tvTVShowsOnTheAirViewAll = view.findViewById(R.id.text_view_tv_shows_on_the_air_view_all);
        rvTVShowsOnTheAir = view.findViewById(R.id.recycler_view_tv_shows_on_the_air);

        tvshowOnTheAirList = new ArrayList<>();
        tvshowsOnTheAirAdapter = new TVShowCardSmallAdapter(getContext(), tvshowOnTheAirList);
        rvTVShowsOnTheAir.setAdapter(tvshowsOnTheAirAdapter);
        rvTVShowsOnTheAir.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsOnTheAirSectionLoaded = false;

        // Movies popular section
        tvMoviesPopularViewAll = view.findViewById(R.id.text_view_movies_popular_view_all);
        rvMoviesPopular = view.findViewById(R.id.recycler_view_movies_popular);

        moviePopularList = new ArrayList<>();
        moviesPopularAdapter = new MovieCardSmallAdapter(getContext(), moviePopularList);
        rvMoviesPopular.setAdapter(moviesPopularAdapter);
        rvMoviesPopular.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesPopularSectionLoaded = false;

        // TV shows popular section
        tvTVShowsPopularViewAll = view.findViewById(R.id.text_view_tv_shows_popular_view_all);
        rvTVShowsPopular = view.findViewById(R.id.recycler_view_tv_shows_popular);

        tvshowPopularList = new ArrayList<>();
        tvshowsPopularAdapter = new TVShowCardSmallAdapter(getContext(), tvshowPopularList);
        rvTVShowsPopular.setAdapter(tvshowsPopularAdapter);
        rvTVShowsPopular.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsPopularSectionLoaded = false;

        // Movies upcoming section
        tvMoviesUpcomingViewAll = view.findViewById(R.id.text_view_movies_upcoming_view_all);
        rvMoviesUpcoming = view.findViewById(R.id.recycler_view_movies_upcoming);

        movieUpcomingList = new ArrayList<>();
        moviesUpcomingAdapter = new MovieCardSmallAdapter(getContext(), movieUpcomingList);
        rvMoviesUpcoming.setAdapter(moviesUpcomingAdapter);
        rvMoviesUpcoming.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesUpcomingSectionLoaded = false;

        // TV shows airing today section
        tvTVShowsAiringTodayViewAll = view.findViewById(R.id.text_view_tv_shows_airing_today_view_all);
        rvTVShowsAiringToday = view.findViewById(R.id.recycler_view_tv_shows_airing_today);

        tvshowAiringTodayList = new ArrayList<>();
        tvshowsAiringTodayAdapter = new TVShowCardSmallAdapter(getContext(), tvshowAiringTodayList);
        rvTVShowsAiringToday.setAdapter(tvshowsAiringTodayAdapter);
        rvTVShowsAiringToday.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsAiringTodaySectionLoaded = false;

        // Movies top rated section
        tvMoviesTopRatedViewAll = view.findViewById(R.id.text_view_movies_top_rated_view_all);
        rvMoviesTopRated = view.findViewById(R.id.recycler_view_movies_top_rated);

        movieTopRatedList = new ArrayList<>();
        moviesTopRatedAdapter = new MovieCardSmallAdapter(getContext(), movieTopRatedList);
        rvMoviesTopRated.setAdapter(moviesTopRatedAdapter);
        rvMoviesTopRated.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesTopRatedSectionLoaded = false;

        // TV shows top rated section
        tvTVShowsTopRatedViewAll = view.findViewById(R.id.text_view_tv_shows_top_rated_view_all);
        rvTVShowsTopRated = view.findViewById(R.id.recycler_view_tv_shows_top_rated);

        tvshowTopRatedList = new ArrayList<>();
        tvshowsTopRatedAdapter = new TVShowCardSmallAdapter(getContext(), tvshowTopRatedList);
        rvTVShowsTopRated.setAdapter(tvshowsTopRatedAdapter);
        rvTVShowsTopRated.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsTopRatedSectionLoaded = false;

        // Set on click listener for view all
        tvMoviesNowPlayingViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, NOW_PLAYING_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        tvMoviesPopularViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, POPULAR_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        tvMoviesUpcomingViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, UPCOMING_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        tvMoviesTopRatedViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, TOP_RATED_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        tvTVShowsOnTheAirViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, ON_THE_AIR_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        tvTVShowsPopularViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, POPULAR_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        tvTVShowsAiringTodayViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, AIRING_TODAY_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        tvTVShowsTopRatedViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, TOP_RATED_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        loadHomeFragment();

        return view;
    }

    private void loadHomeFragment() {
        loadMoviesNowPlaying();
        loadTVShowsOnTheAir();
        loadMoviesPopular();
        loadTVShowsPopular();
        loadMoviesUpcoming();
        loadTVShowsAiringToday();
        loadMoviesTopRated();
        loadTVShowsTopRated();
    }

    private void loadMoviesNowPlaying() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesNowPlayingResponseCall = apiService.getMoviesNowPlaying(TMDB_API_KEY, 1, "US");
        moviesNowPlayingResponseCall.enqueue(new Callback<MoviesNowPlayingResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesNowPlayingResponse> call, @NonNull Response<MoviesNowPlayingResponse> response) {
                if (!response.isSuccessful()) {
                    moviesNowPlayingResponseCall = call.clone();
                    moviesNowPlayingResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesNowPlayingSectionLoaded = true;
                checkAllSectionLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getBackdropPath() != null)
                        movieNowPlayingList.add(movie);
                }
                moviesNowPlayingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesNowPlayingResponse> call, @NonNull Throwable t) {

            }

        });
    }

    private void loadTVShowsOnTheAir() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsOnTheAirResponseCall = apiService.getTVShowsOnTheAir(TMDB_API_KEY, 1, "US");
        tvShowsOnTheAirResponseCall.enqueue(new Callback<TVShowsOnTheAirResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsOnTheAirResponse> call, @NonNull Response<TVShowsOnTheAirResponse> response) {
                if (!response.isSuccessful()) {
                    tvShowsOnTheAirResponseCall = call.clone();
                    tvShowsOnTheAirResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsOnTheAirSectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()) {
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowOnTheAirList.add(tvshow);
                }
                tvshowsOnTheAirAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsOnTheAirResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void loadMoviesPopular() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesPopularResponseCall = apiService.getMoviesPopular(TMDB_API_KEY, 1, "US");
        moviesPopularResponseCall.enqueue(new Callback<MoviesPopularResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesPopularResponse> call, @NonNull Response<MoviesPopularResponse> response) {
                if (!response.isSuccessful()) {
                    moviesPopularResponseCall = call.clone();
                    moviesPopularResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesPopularSectionLoaded = true;
                checkAllSectionLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getPosterPath() != null)
                        moviePopularList.add(movie);
                }
                moviesPopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesPopularResponse> call, @NonNull Throwable t) {

            }

        });

    }

    private void loadTVShowsPopular() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsPopularResponseCall = apiService.getTVShowsPopular(TMDB_API_KEY, 1, "US");
        tvShowsPopularResponseCall.enqueue(new Callback<TVShowsPopularResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsPopularResponse> call, @NonNull Response<TVShowsPopularResponse> response) {
                if (!response.isSuccessful()) {
                    tvShowsPopularResponseCall = call.clone();
                    tvShowsPopularResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsPopularSectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()) {
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowPopularList.add(tvshow);
                }
                tvshowsPopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsPopularResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void loadMoviesUpcoming() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesUpcomingResponseCall = apiService.getMoviesUpcoming(TMDB_API_KEY, 1, "US");
        moviesUpcomingResponseCall.enqueue(new Callback<MoviesUpcomingResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesUpcomingResponse> call, @NonNull Response<MoviesUpcomingResponse> response) {
                if (!response.isSuccessful()) {
                    moviesUpcomingResponseCall = call.clone();
                    moviesUpcomingResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesUpcomingSectionLoaded = true;
                checkAllSectionLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getPosterPath() != null)
                        movieUpcomingList.add(movie);
                }
                moviesUpcomingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesUpcomingResponse> call, @NonNull Throwable t) {

            }

        });

    }

    private void loadTVShowsAiringToday() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsAiringTodayResponseCall = apiService.getTVShowsAiringToday(TMDB_API_KEY, 1, "US");
        tvShowsAiringTodayResponseCall.enqueue(new Callback<TVShowsAiringTodayResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsAiringTodayResponse> call, @NonNull Response<TVShowsAiringTodayResponse> response) {
                if (!response.isSuccessful()) {
                    tvShowsAiringTodayResponseCall = call.clone();
                    tvShowsAiringTodayResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsAiringTodaySectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()) {
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowAiringTodayList.add(tvshow);
                }
                tvshowsAiringTodayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsAiringTodayResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void loadMoviesTopRated() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesTopRatedResponseCall = apiService.getMoviesTopRated(TMDB_API_KEY, 1, "US");
        moviesTopRatedResponseCall.enqueue(new Callback<MoviesTopRatedResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesTopRatedResponse> call, @NonNull Response<MoviesTopRatedResponse> response) {
                if (!response.isSuccessful()) {
                    moviesTopRatedResponseCall = call.clone();
                    moviesTopRatedResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesTopRatedSectionLoaded = true;
                checkAllSectionLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getPosterPath() != null)
                        movieTopRatedList.add(movie);
                }
                moviesTopRatedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesTopRatedResponse> call, @NonNull Throwable t) {

            }

        });

    }

    private void loadTVShowsTopRated() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsTopRatedResponseCall = apiService.getTVShowsTopRated(TMDB_API_KEY, 1, "US");
        tvShowsTopRatedResponseCall.enqueue(new Callback<TVShowsTopRatedResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsTopRatedResponse> call, @NonNull Response<TVShowsTopRatedResponse> response) {
                if (!response.isSuccessful()) {
                    tvShowsTopRatedResponseCall = call.clone();
                    tvShowsTopRatedResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsTopRatedSectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()) {
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowTopRatedList.add(tvshow);
                }
                tvshowsTopRatedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsTopRatedResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void checkAllSectionLoaded() {
        if (moviesNowPlayingSectionLoaded && tvshowsOnTheAirSectionLoaded
                && moviesPopularSectionLoaded && tvshowsPopularSectionLoaded
                && moviesUpcomingSectionLoaded && tvshowsAiringTodaySectionLoaded
                && moviesTopRatedSectionLoaded && tvshowsTopRatedSectionLoaded) {

            progressBar.setVisibility(View.GONE);
            homeLayout.setVisibility(View.VISIBLE);
        }
    }
}