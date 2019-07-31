package com.csovan.themoviedb.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.model.review.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {

        if (reviewList.get(position).getAuthor() != null){
            holder.textViewAuthorName.setText(reviewList.get(position).getAuthor());
        }else {
            holder.textViewAuthorName.setText(R.string.reviews_author_name);
        }
        if (reviewList.get(position).getContent() != null){
            holder.textViewReviewContent.setText(reviewList.get(position).getContent());
        }else {
            holder.textViewReviewContent.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAuthorName;
        TextView textViewReviewContent;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAuthorName = itemView.findViewById(R.id.text_view_author_name);
            textViewReviewContent = itemView.findViewById(R.id.text_view_review_content);
        }
    }
}
