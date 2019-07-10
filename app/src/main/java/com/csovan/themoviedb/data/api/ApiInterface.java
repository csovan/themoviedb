package com.csovan.themoviedb.data.api;

import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;
import com.csovan.themoviedb.data.model.movie.MoviesTopRatedResponse;
import com.csovan.themoviedb.data.model.movie.MoviesUpcomingResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsAiringTodayResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsOnTheAirResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsPopularResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsTopRatedResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    // Movie services
    @GET("movie/now_playing")
    Call<MoviesNowPlayingResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("movie/popular")
    Call<MoviesPopularResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("movie/upcoming")
    Call<MoviesUpcomingResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("movie/top_rated")
    Call<MoviesTopRatedResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    // TV Show services
    @GET("tv/on_the_air")
    Call<TVShowsOnTheAirResponse> getOnTheAirTVShows(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("tv/popular")
    Call<TVShowsPopularResponse> getPopularTVShows(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("tv/airing_today")
    Call<TVShowsAiringTodayResponse> getAiringTodayTVShows(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("tv/top_rated")
    Call<TVShowsTopRatedResponse> getTopRatedTVShows(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

}