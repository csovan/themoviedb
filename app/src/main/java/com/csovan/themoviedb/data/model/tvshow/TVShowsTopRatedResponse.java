package com.csovan.themoviedb.data.model.tvshow;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowsTopRatedResponse {

    @SerializedName("results")
    private List<TVShowBrief> results;

    @SerializedName("page")
    private Integer page;

    @SerializedName("total_results")
    private Integer totalResults;

    @SerializedName("total_pages")
    private Integer totalPages;

    public TVShowsTopRatedResponse(List<TVShowBrief> results, Integer page, Integer totalResults, Integer totalPages) {
        this.results = results;
        this.page = page;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public List<TVShowBrief> getResults() {
        return results;
    }

    public void setResults(List<TVShowBrief> results) {
        this.results = results;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}