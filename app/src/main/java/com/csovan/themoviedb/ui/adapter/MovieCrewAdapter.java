package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.movie.MovieCrewBrief;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;

public class MovieCrewAdapter extends RecyclerView.Adapter<MovieCrewAdapter.CrewViewHolder> {

    private Context context;
    private List<MovieCrewBrief> crewBriefList;

    public MovieCrewAdapter(Context context, List<MovieCrewBrief> crewBriefList) {
        this.context = context;
        this.crewBriefList = crewBriefList;
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CrewViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_crew, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_342 + crewBriefList.get(position).getProfilePath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.crewImageView);

        if (crewBriefList.get(position).getName() != null)
            holder.crewName.setText(crewBriefList.get(position).getName());
        else
            holder.crewName.setText("");

        if (crewBriefList.get(position).getJob() != null)
            holder.crewJob.setText(crewBriefList.get(position).getJob());
        else
            holder.crewJob.setText("");
    }

    @Override
    public int getItemCount() {
        return crewBriefList.size();
    }

    class CrewViewHolder extends RecyclerView.ViewHolder {

        ImageView crewImageView;
        TextView crewName;
        TextView crewJob;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);

            crewImageView = itemView.findViewById(R.id.image_view_crew_profile_pic);
            crewName = itemView.findViewById(R.id.text_view_crew_name);
            crewJob = itemView.findViewById(R.id.text_view_crew_job);
        }
    }
}
