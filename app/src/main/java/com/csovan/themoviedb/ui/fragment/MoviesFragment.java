package com.csovan.themoviedb.ui.fragment;

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
import com.csovan.themoviedb.ui.adapter.MovieCardLargeAdapter;
import com.csovan.themoviedb.ui.adapter.MovieCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;

public class MoviesFragment extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout moviesLayout;

    // Movies now playing
    private List<MovieBrief> movieNowPlayingList;
    private Call<MoviesNowPlayingResponse> moviesNowPlayingResponseCall;
    private TextView tvMoviesNowPlayingViewAll;
    private RecyclerView rvMoviesNowPlaying;
    private MovieCardLargeAdapter moviesNowPlayingAdapter;
    private boolean moviesNowPlayingSectionLoaded;

    // Movies popular
    private List<MovieBrief> moviePopularList;
    private Call<MoviesPopularResponse> moviesPopularResponseCall;
    private TextView tvMoviesPopularViewAll;
    private RecyclerView rvMoviesPopular;
    private MovieCardSmallAdapter moviesPopularAdapter;
    private boolean moviesPopularSectionLoaded;

    // Movies upcoming
    private List<MovieBrief> movieUpcomingList;
    private Call<MoviesUpcomingResponse> moviesUpcomingResponseCall;
    private TextView tvMoviesUpcomingViewAll;
    private RecyclerView rvMoviesUpcoming;
    private MovieCardSmallAdapter moviesUpcomingAdapter;
    private boolean moviesUpcomingSectionLoaded;

    // Movies top rated
    private List<MovieBrief> movieTopRatedList;
    private Call<MoviesTopRatedResponse> moviesTopRatedResponseCall;
    private TextView tvMoviesTopRatedViewAll;
    private RecyclerView rvMoviesTopRated;
    private MovieCardSmallAdapter moviesTopRatedAdapter;
    private boolean moviesTopRatedSectionLoaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        moviesLayout = view.findViewById(R.id.linear_layout_movies);
        moviesLayout.setVisibility(View.GONE);

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

        // Movies popular section
        tvMoviesPopularViewAll = view.findViewById(R.id.text_view_movies_popular_view_all);
        rvMoviesPopular = view.findViewById(R.id.recycler_view_movies_popular);

        moviePopularList = new ArrayList<>();
        moviesPopularAdapter = new MovieCardSmallAdapter(getContext(), moviePopularList);
        rvMoviesPopular.setAdapter(moviesPopularAdapter);
        rvMoviesPopular.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesPopularSectionLoaded = false;

        // Movies upcoming section
        tvMoviesUpcomingViewAll = view.findViewById(R.id.text_view_movies_upcoming_view_all);
        rvMoviesUpcoming = view.findViewById(R.id.recycler_view_movies_upcoming);

        movieUpcomingList = new ArrayList<>();
        moviesUpcomingAdapter = new MovieCardSmallAdapter(getContext(), movieUpcomingList);
        rvMoviesUpcoming.setAdapter(moviesUpcomingAdapter);
        rvMoviesUpcoming.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesUpcomingSectionLoaded = false;

        // Movies top rated section
        tvMoviesTopRatedViewAll = view.findViewById(R.id.text_view_movies_top_rated_view_all);
        rvMoviesTopRated = view.findViewById(R.id.recycler_view_movies_top_rated);

        movieTopRatedList = new ArrayList<>();
        moviesTopRatedAdapter = new MovieCardSmallAdapter(getContext(), movieTopRatedList);
        rvMoviesTopRated.setAdapter(moviesTopRatedAdapter);
        rvMoviesTopRated.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesTopRatedSectionLoaded = false;

        loadMoviesFragment();

        return view;
    }

    private void loadMoviesFragment() {
        loadMoviesNowPlaying();
        loadMoviesPopular();
        loadMoviesUpcoming();
        loadMoviesTopRated();
    }

    private void loadMoviesNowPlaying() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesNowPlayingResponseCall = apiService.getNowPlayingMovies(TMDB_API_KEY, 1, "US");
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

    private void loadMoviesPopular() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesPopularResponseCall = apiService.getPopularMovies(TMDB_API_KEY, 1, "US");
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

    private void loadMoviesUpcoming(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesUpcomingResponseCall = apiService.getUpcomingMovies(TMDB_API_KEY, 1, "US");
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

    private void loadMoviesTopRated(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesTopRatedResponseCall = apiService.getTopRatedMovies(TMDB_API_KEY, 1, "US");
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

    private void checkAllSectionLoaded() {
        if (moviesNowPlayingSectionLoaded && moviesPopularSectionLoaded
                && moviesUpcomingSectionLoaded && moviesTopRatedSectionLoaded){

            progressBar.setVisibility(View.GONE);
            moviesLayout.setVisibility(View.VISIBLE);
        }
    }
}
