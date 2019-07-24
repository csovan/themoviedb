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
import com.csovan.themoviedb.data.model.movie.MovieCastOfPerson;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;

public class MovieCastOfPersonAdapter extends RecyclerView.Adapter<MovieCastOfPersonAdapter.MovieViewHolder> {

    private Context context;
    private List<MovieCastOfPerson> movieCastOfPersonList;

    public MovieCastOfPersonAdapter(Context context, List<MovieCastOfPerson> movieCastOfPersonList) {
        this.context = context;
        this.movieCastOfPersonList = movieCastOfPersonList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_cast_of, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_342 + movieCastOfPersonList.get(position).getPosterPath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewMoviePoster);

        if (movieCastOfPersonList.get(position).getTitle() != null){
            holder.textViewMovieTitle.setText(movieCastOfPersonList.get(position).getTitle());
        }
        else {
            holder.textViewMovieTitle.setText("");
        }
        if (movieCastOfPersonList.get(position).getCharacter() != null
                && !movieCastOfPersonList.get(position).getCharacter().trim().isEmpty()){
            holder.textViewCastCharacter.setText("as " + movieCastOfPersonList.get(position).getCharacter());
        }
        else {
            holder.textViewCastCharacter.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return movieCastOfPersonList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewMovieCard;
        ImageView imageViewMoviePoster;
        TextView textViewMovieTitle;
        TextView textViewCastCharacter;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewMovieCard = itemView.findViewById(R.id.card_view_cast_of);
            imageViewMoviePoster = itemView.findViewById(R.id.image_view_movie_poster);
            textViewMovieTitle = itemView.findViewById(R.id.text_view_title);
            textViewCastCharacter = itemView.findViewById(R.id.text_view_cast_as);

            cardViewMovieCard.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.26);
            cardViewMovieCard.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.25) / 0.65);
        }
    }
}
