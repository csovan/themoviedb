package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.csovan.themoviedb.ui.activity.MovieDetailsActivity;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;
import static com.csovan.themoviedb.util.Constant.MOVIE_ID;

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

        if (movieList.get(position).getTitle() != null){
            holder.tvMovieTitle.setText(movieList.get(position).getTitle());
        }else {
            holder.tvMovieTitle.setText("N/A");
        }

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        CardView cvMovieCard;
        ImageView ivMoviePoster;
        TextView tvMovieTitle;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            cvMovieCard = itemView.findViewById(R.id.card_view_small);
            ivMoviePoster = itemView.findViewById(R.id.image_view_poster);
            tvMovieTitle = itemView.findViewById(R.id.text_view_title);

            cvMovieCard.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.25);
            cvMovieCard.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.25) / 0.65);

            cvMovieCard.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(MOVIE_ID, movieList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            } );

        }
    }
}
