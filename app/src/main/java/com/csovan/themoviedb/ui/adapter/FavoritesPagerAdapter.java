package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.ui.fragment.MoviesFavoriteFragment;
import com.csovan.themoviedb.ui.fragment.TVShowsFavoriteFragment;

public class FavoritesPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public FavoritesPagerAdapter(FragmentManager fragmentManager, Context context){
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new MoviesFavoriteFragment();

            case 1:
                return new TVShowsFavoriteFragment();
        }
        return null;
    }

    @Override
    public int getCount(){
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return mContext.getResources().getString(R.string.menu_movies);

            case 1:
                return mContext.getResources().getString(R.string.menu_tv_shows);
        }
        return null;
    }
}
