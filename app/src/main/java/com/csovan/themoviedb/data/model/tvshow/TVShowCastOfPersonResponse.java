package com.csovan.themoviedb.data.model.tvshow;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowCastOfPersonResponse {

    @SerializedName("cast")
    private List<TVShowCastOfPerson> casts;
    @SerializedName("id")
    private Integer id;

    public TVShowCastOfPersonResponse(List<TVShowCastOfPerson> casts, Integer id) {
        this.casts = casts;
        this.id = id;
    }

    public List<TVShowCastOfPerson> getCasts() {
        return casts;
    }

    public void setCasts(List<TVShowCastOfPerson> casts) {
        this.casts = casts;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
