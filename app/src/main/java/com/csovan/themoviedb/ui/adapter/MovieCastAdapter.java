package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.movie.MovieCastBrief;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.CastViewHolder> {

    private Context context;
    private List<MovieCastBrief> castBriefList;

    public MovieCastAdapter(Context context, List<MovieCastBrief> castBriefList) {
        this.context = context;
        this.castBriefList = castBriefList;
    }

    @NonNull
    @Override
    public MovieCastAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CastViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_cast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_342 + castBriefList.get(position).getProfilePath())
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.castImageView);
    }

    @Override
    public int getItemCount() {
        return castBriefList.size();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView castImageView;

        CastViewHolder(@NonNull View itemView) {
            super(itemView);

            castImageView = itemView.findViewById(R.id.image_view_cast_profile_pic);
        }
    }
}
