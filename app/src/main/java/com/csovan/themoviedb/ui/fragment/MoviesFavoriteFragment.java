package com.csovan.themoviedb.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.database.Favorites;
import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.ui.adapter.MovieCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;

public class MoviesFavoriteFragment extends Fragment {

    private RecyclerView recyclerViewMoviesFavorite;
    private List<MovieBrief> moviesFavoriteList;
    private MovieCardSmallAdapter moviesFavoriteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies_favorite, container, false);

        // Set adapter
        recyclerViewMoviesFavorite = view.findViewById(R.id.recycler_view_movies_favorite);
        moviesFavoriteList = new ArrayList<>();
        moviesFavoriteAdapter = new MovieCardSmallAdapter(getContext(), moviesFavoriteList);
        recyclerViewMoviesFavorite.setAdapter(moviesFavoriteAdapter);
        recyclerViewMoviesFavorite.setLayoutManager(new GridLayoutManager(getContext(), 3));

        loadFavoriteMovies();

        return view;
    }

    private void loadFavoriteMovies(){
        List<MovieBrief> favoriteMovies = Favorites.getFavoriteMovieBriefs(getContext());

        for (MovieBrief movieBrief : favoriteMovies){
            moviesFavoriteList.add(movieBrief);
        }
        moviesFavoriteAdapter.notifyDataSetChanged();
    }

}
