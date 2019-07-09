package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.movie.MovieBrief;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_780;

public class MovieCardLargeAdapter extends RecyclerView.Adapter<MovieCardLargeAdapter.MovieViewHolder> {

    private Context context;
    private List<MovieBrief> movieList;

    public MovieCardLargeAdapter(Context context, List<MovieBrief> movieList) {
        this.context = context;
        this.movieList = movieList;
    }


    @NonNull
    @Override
    public MovieCardLargeAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_card_large, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCardLargeAdapter.MovieViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_780 + movieList.get(position).getBackdropPath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivMovieBackDrop);

        if (movieList.get(position).getTitle() != null)
            holder.tvMovieTitle.setText(movieList.get(position).getTitle());
        else
            holder.tvMovieTitle.setText("");

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cvMovieCard;
        ImageView ivMovieBackDrop;
        TextView tvMovieTitle;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            cvMovieCard = itemView.findViewById(R.id.card_view_large);
            ivMovieBackDrop = itemView.findViewById(R.id.image_view_backdrop);
            tvMovieTitle = itemView.findViewById(R.id.text_view_title_card_large);

            cvMovieCard.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
            cvMovieCard.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.85) / 1.75);

        }
    }
}
