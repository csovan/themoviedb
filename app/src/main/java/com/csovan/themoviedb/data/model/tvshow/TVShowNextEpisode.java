package com.csovan.themoviedb.data.model.tvshow;

import com.google.gson.annotations.SerializedName;

public class TVShowNextEpisode {

    @SerializedName("id")
    private Integer id;

    @SerializedName("air_date")
    private String nextAirDate;

    public TVShowNextEpisode(Integer id, String nextAirDate) {
        this.id = id;
        this.nextAirDate = nextAirDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNextAirDate() {
        return nextAirDate;
    }

    public void setNextAirDate(String nextAirDate) {
        this.nextAirDate = nextAirDate;
    }
}
