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
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.ui.activity.TVShowDetailsActivity;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_780;
import static com.csovan.themoviedb.util.Constant.TV_SHOW_ID;

public class TVShowCardSmallAdapter extends RecyclerView.Adapter<TVShowCardSmallAdapter.TVShowViewHolder> {

    private Context context;
    private List<TVShowBrief> tvshowList;

    public TVShowCardSmallAdapter(Context context, List<TVShowBrief> tvshowList) {
        this.context = context;
        this.tvshowList = tvshowList;
    }

    @NonNull
    @Override
    public TVShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TVShowViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_card_small, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_780 + tvshowList.get(position).getPosterPath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_film)
                .fallback(R.drawable.ic_film)
                .error(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewTVShowPoster);

        if (tvshowList.get(position).getName() != null){
            holder.tvTVShowTitle.setText(tvshowList.get(position).getName());
        }else {
            holder.tvTVShowTitle.setText("N/A");
        }

    }

    @Override
    public int getItemCount() {
        return tvshowList.size();
    }

    class TVShowViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewTVShowCardSmall;
        ImageView imageViewTVShowPoster;
        TextView tvTVShowTitle;

        ConstraintLayout constraintLayoutItemCardSmall;

        TVShowViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewTVShowCardSmall = itemView.findViewById(R.id.card_view_small);
            imageViewTVShowPoster = itemView.findViewById(R.id.image_view_poster);
            tvTVShowTitle = itemView.findViewById(R.id.text_view_title);

            constraintLayoutItemCardSmall = itemView.findViewById(R.id.constraint_layout_item_card_small);

            cardViewTVShowCardSmall.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.25);
            cardViewTVShowCardSmall.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.25) / 0.65);

            constraintLayoutItemCardSmall.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(context, TVShowDetailsActivity.class);
                    intent.putExtra(TV_SHOW_ID, tvshowList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });

        }
    }
}

