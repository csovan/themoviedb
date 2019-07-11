package com.csovan.themoviedb.data.model.tvshow;

import com.google.gson.annotations.SerializedName;

public class TVShowGenres {

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String genreName;

    public TVShowGenres(Integer id, String genreName) {
        this.id = id;
        this.genreName = genreName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

}

