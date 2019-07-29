package com.csovan.themoviedb.ui.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.database.Favorites;
import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.MovieCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoviesFavoriteFragment extends Fragment {

    private RecyclerView recyclerViewMoviesFavorite;
    private List<MovieBrief> movieFavoriteList;
    private MovieCardSmallAdapter moviesFavoriteAdapter;

    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar snackbar;
    private boolean isBroadcastReceiverRegistered;

    private boolean isMoviesFavoriteFragmentLoaded;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies_favorite, container, false);

        progressBar = view.findViewById(R.id.progress_bar);

        // Set adapter
        recyclerViewMoviesFavorite = view.findViewById(R.id.recycler_view_movies_favorite);
        recyclerViewMoviesFavorite.setVisibility(View.INVISIBLE);
        movieFavoriteList = new ArrayList<>();
        moviesFavoriteAdapter = new MovieCardSmallAdapter(getContext(), movieFavoriteList);
        recyclerViewMoviesFavorite.setAdapter(moviesFavoriteAdapter);
        recyclerViewMoviesFavorite.setLayoutManager(new GridLayoutManager(getContext(), 3));

        if (NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            isMoviesFavoriteFragmentLoaded = true;
            loadFavoriteMovies();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        moviesFavoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!isMoviesFavoriteFragmentLoaded && !NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            snackbar = Snackbar.make(Objects.requireNonNull(getActivity())
                            .findViewById(R.id.frame_layout_movies_favorite),
                    R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(
                    new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                        @Override
                        public void onNetworkConnectionConnected() {
                            snackbar.dismiss();
                            isMoviesFavoriteFragmentLoaded = true;
                            loadFavoriteMovies();
                            isBroadcastReceiverRegistered = false;
                            Objects.requireNonNull(getActivity()).unregisterReceiver(connectivityBroadcastReceiver);
                        }
                    });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            getActivity().registerReceiver(connectivityBroadcastReceiver, intentFilter);
        }else if (!isMoviesFavoriteFragmentLoaded && NetworkConnection.isConnected(getContext())){
            isMoviesFavoriteFragmentLoaded = true;
            loadFavoriteMovies();
        }
        // Clear old reference list
        movieFavoriteList.clear();
        // Add current list
        movieFavoriteList.addAll(Favorites.getFavoriteMovieBriefs(getContext()));
        moviesFavoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();

        if (isBroadcastReceiverRegistered){
            snackbar.dismiss();
            isBroadcastReceiverRegistered = false;
            Objects.requireNonNull(getActivity()).unregisterReceiver(connectivityBroadcastReceiver);
        }
    }

    private void loadFavoriteMovies(){
        List<MovieBrief> favoriteMovieBriefs = Favorites.getFavoriteMovieBriefs(getContext());

        movieFavoriteList.addAll(favoriteMovieBriefs);
        progressBar.setVisibility(View.GONE);
        recyclerViewMoviesFavorite.setVisibility(View.VISIBLE);
        moviesFavoriteAdapter.notifyDataSetChanged();
    }

}
