package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.video.Video;

import java.util.List;

import static com.csovan.themoviedb.util.Constant.YOUTUBE_THUMBNAIL_BASE_URL;
import static com.csovan.themoviedb.util.Constant.YOUTUBE_THUMBNAIL_IMAGE_QUALITY;
import static com.csovan.themoviedb.util.Constant.YOUTUBE_WATCH_BASE_URL;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videoList;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {

        Glide.with(context.getApplicationContext())
                .load(YOUTUBE_THUMBNAIL_BASE_URL + videoList.get(position).getKey() + YOUTUBE_THUMBNAIL_IMAGE_QUALITY)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_film)
                .fallback(R.drawable.ic_film)
                .error(R.drawable.ic_film)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageViewVideoThumbnail);

        if (videoList.get(position).getName() != null)
            holder.textViewVideoTitle.setText(videoList.get(position).getName());
        else
            holder.textViewVideoTitle.setText("");
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewVideo;
        ImageView imageViewVideoThumbnail;
        TextView textViewVideoTitle;
        ImageButton imageButtonPlayButton;

        ConstraintLayout constraintLayoutItemVideo;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewVideo = itemView.findViewById(R.id.card_view_video);
            imageViewVideoThumbnail = itemView.findViewById(R.id.image_view_video_thumbnail);
            textViewVideoTitle = itemView.findViewById(R.id.text_view_video_title);
            imageButtonPlayButton = itemView.findViewById(R.id.image_button_video_play_button);

            constraintLayoutItemVideo = itemView.findViewById(R.id.constraint_layout_item_video);

            cardViewVideo.getLayoutParams().width =
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
            cardViewVideo.getLayoutParams().height =
                    (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.80) / 1.8);

            constraintLayoutItemVideo.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(YOUTUBE_WATCH_BASE_URL + videoList.get(getAdapterPosition()).getKey()));
                    context.startActivity(youtubeIntent);
                }
            });

        }
    }
}
