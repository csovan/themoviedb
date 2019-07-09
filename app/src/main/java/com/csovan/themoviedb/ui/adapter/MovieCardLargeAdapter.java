package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.csovan.themoviedb.R;

public class MovieCardLargeAdapter extends RecyclerView.Adapter<MovieCardLargeAdapter.MovieViewHolder> {

    private Context context;

    public MovieCardLargeAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public MovieCardLargeAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_card_large, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCardLargeAdapter.MovieViewHolder holder, int position) {

        Glide.with(context)
                .load(R.drawable.ic_film)
                .into(holder.imageViewMovieBackdrop);

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewMovieCardLarge;
        ImageView imageViewMovieBackdrop;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewMovieCardLarge = itemView.findViewById(R.id.card_view_large);
            imageViewMovieBackdrop = itemView.findViewById(R.id.image_view_backdrop);

        }
    }
}
