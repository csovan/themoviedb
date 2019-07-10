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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.movie.MovieBrief;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;

public class MovieCardSmallAdapter extends RecyclerView.Adapter<MovieCardSmallAdapter.MovieViewHolder>{

    private Context context;
    private List<MovieBrief> movieList;

    public MovieCardSmallAdapter(Context context, List<MovieBrief> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieCardSmallAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_card_small, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCardSmallAdapter.MovieViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_342 + movieList.get(position).getPosterPath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivMoviePoster);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        CardView cvMovieCard;
        ImageView ivMoviePoster;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            cvMovieCard = itemView.findViewById(R.id.card_view_size_small);
            ivMoviePoster = itemView.findViewById(R.id.image_view_poster);

            cvMovieCard.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.27);
            cvMovieCard.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.25) / 0.65);

        }
    }
}
