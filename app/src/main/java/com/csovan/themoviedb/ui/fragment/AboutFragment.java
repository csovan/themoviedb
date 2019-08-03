package com.csovan.themoviedb.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.csovan.themoviedb.R;

public class AboutFragment extends Fragment {

    private ImageView imageViewTMDBLink;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        imageViewTMDBLink = view.findViewById(R.id.image_view_tmdb_logo);

        loadAboutFragment();

        return view;
    }

    private void loadAboutFragment() {

        imageViewTMDBLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                String tmdbLink = "https://www.themoviedb.org";
                Intent tmdbLinkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tmdbLink));
                startActivity(tmdbLinkIntent);
            }
        });
    }
}
