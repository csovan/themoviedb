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
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.TVShowCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TVShowsFavoriteFragment extends Fragment {

    private RecyclerView recyclerViewTVShowsFavorite;
    private List<TVShowBrief> tvshowFavoriteList;
    private TVShowCardSmallAdapter tvshowsFavoriteAdapter;

    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar snackbar;
    private boolean isBroadcastReceiverRegistered;

    private boolean isTVShowsFavoriteFragmentLoaded;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tvshows_favorite, container, false);

        progressBar = view.findViewById(R.id.progress_bar);

        recyclerViewTVShowsFavorite = view.findViewById(R.id.recycler_view_tv_shows_favorite);
        recyclerViewTVShowsFavorite.setVisibility(View.INVISIBLE);
        tvshowFavoriteList = new ArrayList<>();
        tvshowsFavoriteAdapter = new TVShowCardSmallAdapter(getContext(), tvshowFavoriteList);
        recyclerViewTVShowsFavorite.setAdapter(tvshowsFavoriteAdapter);
        recyclerViewTVShowsFavorite.setLayoutManager(new GridLayoutManager(getContext(), 3));

        if (NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            isTVShowsFavoriteFragmentLoaded = true;
            loadFavoriteTVShows();
        }

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        tvshowsFavoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!isTVShowsFavoriteFragmentLoaded && !NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            snackbar = Snackbar.make(Objects.requireNonNull(getActivity())
                            .findViewById(R.id.frame_layout_tv_shows_favorite),
                    R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(
                    new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                        @Override
                        public void onNetworkConnectionConnected() {
                            snackbar.dismiss();
                            isTVShowsFavoriteFragmentLoaded = true;
                            loadFavoriteTVShows();
                            isBroadcastReceiverRegistered = false;
                            Objects.requireNonNull(getActivity()).unregisterReceiver(connectivityBroadcastReceiver);
                        }
                    });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            getActivity().registerReceiver(connectivityBroadcastReceiver, intentFilter);
        }else if (!isTVShowsFavoriteFragmentLoaded && NetworkConnection.isConnected(getContext())){
            isTVShowsFavoriteFragmentLoaded = true;
            loadFavoriteTVShows();
        }
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

    private void loadFavoriteTVShows() {
        List<TVShowBrief> favoriteTVShowBriefs = Favorites.getFavoritesTVShowBriefs(getContext());

        for (TVShowBrief tvShowBrief : favoriteTVShowBriefs) {
            tvshowFavoriteList.add(tvShowBrief);
        }
        progressBar.setVisibility(View.GONE);
        recyclerViewTVShowsFavorite.setVisibility(View.VISIBLE);
        tvshowsFavoriteAdapter.notifyDataSetChanged();
    }

}
