package com.csovan.themoviedb.data.model.tvshow;

import com.google.gson.annotations.SerializedName;

public class TVShowCreator {

    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;

    public TVShowCreator(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
