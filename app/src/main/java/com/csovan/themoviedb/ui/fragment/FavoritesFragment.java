package com.csovan.themoviedb.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.ui.adapter.FavoritesPagerAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;


public class FavoritesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        SmartTabLayout smartTabLayout = view.findViewById(R.id.tab_view_pager_favorite);
        ViewPager viewPager = view.findViewById(R.id.view_pager_favorite);
        viewPager.setAdapter(new FavoritesPagerAdapter(getChildFragmentManager(), getContext()));
        smartTabLayout.setViewPager(viewPager);

        return view;
    }
}
