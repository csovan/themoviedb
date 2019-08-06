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

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_780;
import static com.csovan.themoviedb.util.Constant.MOVIE_ID;

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
                .fallback(R.drawable.ic_film)
                .error(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewMovieBackdrop);

        if (movieList.get(position).getTitle() != null)
            holder.textViewMovieTitle.setText(movieList.get(position).getTitle());
        else
            holder.textViewMovieTitle.setText("");

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewMovieCardLarge;
        ImageView imageViewMovieBackdrop;
        TextView textViewMovieTitle;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewMovieCardLarge = itemView.findViewById(R.id.card_view_large);
            imageViewMovieBackdrop = itemView.findViewById(R.id.image_view_backdrop);
            textViewMovieTitle = itemView.findViewById(R.id.text_view_title_card_large);

            cardViewMovieCardLarge.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
            cardViewMovieCardLarge.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.85) / 1.75);

            cardViewMovieCardLarge.setOnClickListener(new View.OnClickListener(){
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
