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
import com.csovan.themoviedb.data.model.movie.MovieCastBrief;
import com.csovan.themoviedb.ui.activity.PersonDetailsActivity;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_342;
import static com.csovan.themoviedb.util.Constant.PERSON_ID;

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.CastViewHolder> {

    private Context context;
    private List<MovieCastBrief> castBriefList;

    public MovieCastAdapter(Context context, List<MovieCastBrief> castBriefList) {
        this.context = context;
        this.castBriefList = castBriefList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CastViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_cast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(IMAGE_LOADING_BASE_URL_342 + castBriefList.get(position).getProfilePath())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .fallback(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.castImageView);

        if (castBriefList.get(position).getName() != null)
            holder.castName.setText(castBriefList.get(position).getName());
        else
            holder.castName.setText("");

        if (castBriefList.get(position).getCharacter() != null)
            holder.castCharacter.setText(castBriefList.get(position).getCharacter());
        else holder.castCharacter.setText("");
    }

    @Override
    public int getItemCount() {
        return castBriefList.size();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {

        ImageView castImageView;
        TextView castName;
        TextView castCharacter;

        CardView castCardView;
        ConstraintLayout constraintLayoutItemCast;

        CastViewHolder(@NonNull View itemView) {
            super(itemView);

            castImageView = itemView.findViewById(R.id.image_view_cast_profile_pic);
            castName = itemView.findViewById(R.id.text_view_cast_name);
            castCharacter = itemView.findViewById(R.id.text_view_cast_as);
            castCardView = itemView.findViewById(R.id.card_view_cast);

            constraintLayoutItemCast = itemView.findViewById(R.id.constraint_layout_item_cast);

            constraintLayoutItemCast.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.25);

            castCardView.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * .25) / 1);

            constraintLayoutItemCast.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(context, PersonDetailsActivity.class);
                    intent.putExtra(PERSON_ID, castBriefList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
