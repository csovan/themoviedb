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
import com.csovan.themoviedb.data.model.tvshow.TVShowCastOfPerson;
import com.csovan.themoviedb.ui.activity.TVShowDetailsActivity;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;
import static com.csovan.themoviedb.util.Constant.TV_SHOW_ID;

public class TVShowCastOfPersonAdapter extends RecyclerView.Adapter<TVShowCastOfPersonAdapter.TVShowViewHolder> {

    private Context context;
    private List<TVShowCastOfPerson> tvshowCastOfPersonList;

    public TVShowCastOfPersonAdapter(Context context, List<TVShowCastOfPerson> tvshowCastOfPersonList) {
        this.context = context;
        this.tvshowCastOfPersonList = tvshowCastOfPersonList;
    }

    @NonNull
    @Override
    public TVShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TVShowViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_cast_of, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_342 + tvshowCastOfPersonList.get(position).getPosterPath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewTVShowPoster);

        if (tvshowCastOfPersonList.get(position).getName() != null){
            holder.textViewTVShowTitle.setText(tvshowCastOfPersonList.get(position).getName());
        }
        else {
            holder.textViewTVShowTitle.setText("");
        }
        if (tvshowCastOfPersonList.get(position).getCharacter() != null
                && !tvshowCastOfPersonList.get(position).getCharacter().trim().isEmpty()){
            String asCharacterString = "as " + tvshowCastOfPersonList.get(position).getCharacter();
            holder.textViewCastCharacter.setText(asCharacterString);
        }
        else {
            holder.textViewCastCharacter.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return tvshowCastOfPersonList.size();
    }

    class TVShowViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewTVShowCard;
        ImageView imageViewTVShowPoster;
        TextView textViewTVShowTitle;
        TextView textViewCastCharacter;

        ConstraintLayout constraintLayoutItemCastOf;

        TVShowViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewTVShowCard = itemView.findViewById(R.id.card_view_cast_of);
            imageViewTVShowPoster = itemView.findViewById(R.id.image_view_movie_poster);
            textViewTVShowTitle = itemView.findViewById(R.id.text_view_title);
            textViewCastCharacter = itemView.findViewById(R.id.text_view_cast_as);

            constraintLayoutItemCastOf = itemView.findViewById(R.id.constraint_layout_item_cast_of);

            cardViewTVShowCard.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.26);
            cardViewTVShowCard.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.25) / 0.65);

            constraintLayoutItemCastOf.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(context, TVShowDetailsActivity.class);
                    intent.putExtra(TV_SHOW_ID, tvshowCastOfPersonList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
