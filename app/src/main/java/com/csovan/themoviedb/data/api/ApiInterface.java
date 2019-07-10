package com.csovan.themoviedb.data.api;

import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;

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

}
