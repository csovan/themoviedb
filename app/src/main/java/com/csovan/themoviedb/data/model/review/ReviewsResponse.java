package com.csovan.themoviedb.data.model.review;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsResponse {

    @SerializedName("id")
    private Integer id;

    @SerializedName("results")
    private List<Review> reviews;

    public ReviewsResponse(Integer id, List<Review> reviews) {
        this.id = id;
        this.reviews = reviews;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
