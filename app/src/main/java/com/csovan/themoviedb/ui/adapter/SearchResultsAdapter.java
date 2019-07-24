package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.csovan.themoviedb.data.model.search.SearchResult;
import com.csovan.themoviedb.ui.activity.MovieDetailsActivity;
import com.csovan.themoviedb.ui.activity.PersonDetailsActivity;
import com.csovan.themoviedb.ui.activity.TVShowDetailsActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;
import static com.csovan.themoviedb.util.Constant.MOVIE_ID;
import static com.csovan.themoviedb.util.Constant.PERSON_ID;
import static com.csovan.themoviedb.util.Constant.TV_SHOW_ID;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder> {

    private Context context;
    private List<SearchResult> searchResults;

    public SearchResultsAdapter(Context context, List<SearchResult> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public SearchResultsAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResultViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_card_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_342 + searchResults.get(position).getPosterPath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.posterImageView);

        if (searchResults.get(position).getName() != null && !searchResults.get(position).getName().trim().isEmpty())
            holder.nameTextView.setText(searchResults.get(position).getName());
        else
            holder.nameTextView.setText("");

        if (searchResults.get(position).getMediaType() != null && searchResults.get(position).getMediaType().equals("movie"))
            holder.mediaTypeTextView.setText(R.string.movie);
        else if (searchResults.get(position).getMediaType() != null && searchResults.get(position).getMediaType().equals("tv"))
            holder.mediaTypeTextView.setText(R.string.tv_show);
        else if (searchResults.get(position).getMediaType() != null && searchResults.get(position).getMediaType().equals("person"))
            holder.mediaTypeTextView.setText(R.string.person);
        else
            holder.mediaTypeTextView.setText("");

        if (searchResults.get(position).getOverview() != null && !searchResults.get(position).getOverview().trim().isEmpty())
            holder.overviewTextView.setText(searchResults.get(position).getOverview());
        else
            holder.overviewTextView.setText("");

        if (searchResults.get(position).getReleaseDate() != null && !searchResults.get(position).getReleaseDate().trim().isEmpty()) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy", Locale.getDefault());
            try {
                Date releaseDate = sdf1.parse(searchResults.get(position).getReleaseDate());
                holder.yearTextView.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            holder.yearTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayoutSearchResults;

        CardView cardViewPoster;
        ImageView posterImageView;
        TextView nameTextView;
        TextView mediaTypeTextView;
        TextView overviewTextView;
        TextView yearTextView;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayoutSearchResults = itemView.findViewById(R.id.constraint_layout_search_result);

            cardViewPoster = itemView.findViewById(R.id.card_view_movie_poster);
            posterImageView = itemView.findViewById(R.id.image_view_poster_search);
            nameTextView = itemView.findViewById(R.id.text_view_name_search);
            mediaTypeTextView = itemView.findViewById(R.id.text_view_media_type_search);
            overviewTextView = itemView.findViewById(R.id.text_view_overview_search);
            yearTextView = itemView.findViewById(R.id.text_view_release_date_search);

            cardViewPoster.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.26);
            cardViewPoster.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.25) / 0.65);

            constraintLayoutSearchResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (searchResults.get(getAdapterPosition()).getMediaType()) {
                        case "movie": {
                            Intent intent = new Intent(context, MovieDetailsActivity.class);
                            intent.putExtra(MOVIE_ID, searchResults.get(getAdapterPosition()).getId());
                            context.startActivity(intent);
                            break;
                        }
                        case "tv": {
                            Intent intent = new Intent(context, TVShowDetailsActivity.class);
                            intent.putExtra(TV_SHOW_ID, searchResults.get(getAdapterPosition()).getId());
                            context.startActivity(intent);
                            break;
                        }
                        case "person": {
                            Intent intent = new Intent(context, PersonDetailsActivity.class);
                            intent.putExtra(PERSON_ID, searchResults.get(getAdapterPosition()).getId());
                            context.startActivity(intent);
                            break;
                        }
                    }
                }
            });
        }
    }
}
