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
import com.csovan.themoviedb.data.model.tvshow.TVShowCrewBrief;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;

public class TVShowCrewAdapter extends RecyclerView.Adapter<TVShowCrewAdapter.CrewViewHolder> {

    private Context context;
    private List<TVShowCrewBrief> crewBriefList;

    public TVShowCrewAdapter(Context context, List<TVShowCrewBrief> tvshowCrewBriefList) {
        this.context = context;
        this.crewBriefList = tvshowCrewBriefList;
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

    }

    @Override
    public int getItemCount() {
        return crewBriefList.size();
    }

    class CrewViewHolder extends RecyclerView.ViewHolder {

        ImageView crewImageView;
        TextView crewName;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);

            crewImageView = itemView.findViewById(R.id.image_view_crew_profile_pic);
            crewName = itemView.findViewById(R.id.text_view_crew_name);
        }
    }
}
