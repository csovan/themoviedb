package com.csovan.themoviedb.data.api;

import com.csovan.themoviedb.data.model.movie.Movie;
import com.csovan.themoviedb.data.model.movie.MovieCreditsResponse;
import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;
import com.csovan.themoviedb.data.model.movie.MoviesTopRatedResponse;
import com.csovan.themoviedb.data.model.movie.MoviesUpcomingResponse;
import com.csovan.themoviedb.data.model.movie.SimilarMoviesResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShow;
import com.csovan.themoviedb.data.model.tvshow.TVShowCreditsResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsAiringTodayResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsOnTheAirResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsPopularResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsTopRatedResponse;
import com.csovan.themoviedb.data.model.video.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("region") String region);

    @GET("movie/{id}/videos")
    Call<VideosResponse> getMovieVideos(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("region") String region);

    @GET("movie/{id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey);

    @GET("movie/{id}/similar")
    Call<SimilarMoviesResponse> getSimilarMovies(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("page") Integer page);


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

    @GET("tv/{id}")
    Call<TVShow> getTVShowDetails(
            @Path("id") Integer tvshowId,
            @Query("api_key") String apiKey,
            @Query("region") String region);

    @GET("tv/{id}/videos")
    Call<VideosResponse> getTVShowVideos(
            @Path("id") Integer tvshowId,
            @Query("api_key") String apiKey,
            @Query("region") String region);

    @GET("tv/{id}/credits")
    Call<TVShowCreditsResponse> getTVShowCredits(
            @Path("id") Integer tvshowId,
            @Query("api_key") String apiKey);

}
