package com.csovan.themoviedb.data.api;

import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;
import com.csovan.themoviedb.data.model.movie.MoviesTopRatedResponse;
import com.csovan.themoviedb.data.model.movie.MoviesUpcomingResponse;

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

}
