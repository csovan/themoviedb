package com.csovan.themoviedb.data.api;

import com.csovan.themoviedb.data.model.movie.Movie;
import com.csovan.themoviedb.data.model.movie.MovieCastsOfPersonResponse;
import com.csovan.themoviedb.data.model.movie.MovieCreditsResponse;
import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;
import com.csovan.themoviedb.data.model.movie.MoviesTopRatedResponse;
import com.csovan.themoviedb.data.model.movie.MoviesUpcomingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesSimilarResponse;
import com.csovan.themoviedb.data.model.people.Person;
import com.csovan.themoviedb.data.model.review.ReviewsResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsSimilarResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShow;
import com.csovan.themoviedb.data.model.tvshow.TVShowCastsOfPersonResponse;
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
    Call<MoviesNowPlayingResponse> getMoviesNowPlaying(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("movie/popular")
    Call<MoviesPopularResponse> getMoviesPopular(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("movie/upcoming")
    Call<MoviesUpcomingResponse> getMoviesUpcoming(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("movie/top_rated")
    Call<MoviesTopRatedResponse> getMoviesTopRated(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("region") String region);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> getMovieReviews(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideosResponse> getMovieVideos(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey);

    @GET("movie/{id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey);

    @GET("movie/{id}/similar")
    Call<MoviesSimilarResponse> getMoviesSimilar(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("page") Integer page);


    // TV Show services
    @GET("tv/on_the_air")
    Call<TVShowsOnTheAirResponse> getTVShowsOnTheAir(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("tv/popular")
    Call<TVShowsPopularResponse> getTVShowsPopular(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("tv/airing_today")
    Call<TVShowsAiringTodayResponse> getTVShowsAiringToday(
            @Query("api_key") String apiKey,
            @Query("page") Integer page,
            @Query("region") String region);

    @GET("tv/top_rated")
    Call<TVShowsTopRatedResponse> getTVShowsTopRated(
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
            @Query("api_key") String apiKey);

    @GET("tv/{id}/credits")
    Call<TVShowCreditsResponse> getTVShowCredits(
            @Path("id") Integer tvshowId,
            @Query("api_key") String apiKey);

    @GET("tv/{id}/similar")
    Call<TVShowsSimilarResponse> getTVShowsSimilar(
            @Path("id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("page") Integer page);

    // Person services
    @GET("person/{id}")
    Call<Person> getPersonDetails(
            @Path("id") Integer personId,
            @Query("api_key") String apiKey);

    @GET("person/{id}/movie_credits")
    Call<MovieCastsOfPersonResponse> getMovieCastsOfPerson(
            @Path("id") Integer personId,
            @Query("api_key") String apiKey);

    @GET("person/{id}/tv_credits")
    Call<TVShowCastsOfPersonResponse> getTVShowCastsOfPerson(
            @Path("id") Integer personId,
            @Query("api_key") String apiKey);
}
