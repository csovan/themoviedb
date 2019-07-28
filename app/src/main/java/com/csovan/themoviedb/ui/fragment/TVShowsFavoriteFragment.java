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
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.ui.adapter.TVShowCardSmallAdapter;

import java.util.ArrayList;
import java.util.List;

public class TVShowsFavoriteFragment extends Fragment {

    private RecyclerView mFavTVShowsRecyclerView;
    private List<TVShowBrief> mFavTVShows;
    private TVShowCardSmallAdapter mFavTVShowsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tvshows_favorite, container, false);

        mFavTVShowsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_tv_shows_favorite);
        mFavTVShows = new ArrayList<>();
        mFavTVShowsAdapter = new TVShowCardSmallAdapter(getContext(), mFavTVShows);
        mFavTVShowsRecyclerView.setAdapter(mFavTVShowsAdapter);
        mFavTVShowsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        loadFavTVShows();

        return  view;
    }

    private void loadFavTVShows() {
        List<TVShowBrief> favTVShowBriefs = Favorites.getFavTVShowBriefs(getContext());

        for (TVShowBrief tvShowBrief : favTVShowBriefs) {
            mFavTVShows.add(tvShowBrief);
        }
        mFavTVShowsAdapter.notifyDataSetChanged();
    }

}
