package com.csovan.themoviedb.ui.fragment;

import android.content.Context;
import android.net.Uri;
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
import com.csovan.themoviedb.ui.adapter.MovieCardLargeAdapter;

public class MoviesFragment extends Fragment {

    // Movies now playing
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

        moviesNowPlayingAdapter = new MovieCardLargeAdapter(getContext());
        rvMoviesNowPlaying.setAdapter(moviesNowPlayingAdapter);
        rvMoviesNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        (new LinearSnapHelper()).attachToRecyclerView(rvMoviesNowPlaying);

        return view;
    }
}
