package com.csovan.themoviedb.data.model.tvshow;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShow {

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("episode_run_time")
    private List<Integer> episodeRunTime;

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("last_air_date")
    private String lastAirDate;

    @SerializedName("number_of_episodes")
    private Integer numberOfEpisodes;

    @SerializedName("number_of_seasons")
    private Integer numberOfSeasons;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName("status")
    private String status;

    @SerializedName("vote_average")
    private Double voteAverage;

    @SerializedName("genres")
    private List<TVShowGenres> genres;

    public TVShow(Integer id, String name, List<Integer> episodeRunTime, String firstAirDate,
                  String lastAirDate, Integer numberOfEpisodes, Integer numberOfSeasons,
                  String posterPath, String backdropPath, String overview, String status,
                  Double voteAverage, List<TVShowGenres> genres) {
        this.id = id;
        this.name = name;
        this.episodeRunTime = episodeRunTime;
        this.firstAirDate = firstAirDate;
        this.lastAirDate = lastAirDate;
        this.numberOfEpisodes = numberOfEpisodes;
        this.numberOfSeasons = numberOfSeasons;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.status = status;
        this.voteAverage = voteAverage;
        this.genres = genres;
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

    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public void setEpisodeRunTime(List<Integer> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public List<TVShowGenres> getGenres() {
        return genres;
    }

    public void setGenres(List<TVShowGenres> genres) {
        this.genres = genres;
    }
}
