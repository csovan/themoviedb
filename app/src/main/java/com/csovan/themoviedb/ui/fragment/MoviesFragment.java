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
import android.widget.TextView;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.api.ApiClient;
import com.csovan.themoviedb.data.api.ApiInterface;
import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.ui.adapter.MovieCardLargeAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;

public class MoviesFragment extends Fragment {

    // Movies now playing
    private List<MovieBrief> movieNowPlayingList;
    private Call<MoviesNowPlayingResponse> moviesNowPlayingResponseCall;
    private TextView tvMoviesNowPlayingViewAll;
    private RecyclerView rvMoviesNowPlaying;
    private MovieCardLargeAdapter moviesNowPlayingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        // Movies now playing section
        tvMoviesNowPlayingViewAll = view.findViewById(R.id.text_view_movies_now_playing_view_all);
        rvMoviesNowPlaying = view.findViewById(R.id.recycler_view_movies_now_playing);

        movieNowPlayingList = new ArrayList<>();
        moviesNowPlayingAdapter = new MovieCardLargeAdapter(getContext(), movieNowPlayingList);
        rvMoviesNowPlaying.setAdapter(moviesNowPlayingAdapter);
        rvMoviesNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        (new LinearSnapHelper()).attachToRecyclerView(rvMoviesNowPlaying);

        loadMoviesFragment();

        return view;
    }

    private void loadMoviesFragment() {
        loadMoviesNowPlaying();
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
}
