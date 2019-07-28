package com.csovan.themoviedb.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;

import java.util.ArrayList;
import java.util.List;

public class Favorites {

    // Movies

    // Add movie to favorites database
    public static void addMovieToFavorites(Context context, Integer movieId, String posterPath, String name){
        if (movieId == null) return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (!isMovieFavorite(context, movieId)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.MOVIE_ID, movieId);
            contentValues.put(DatabaseHelper.POSTER_PATH, posterPath);
            contentValues.put(DatabaseHelper.NAME, name);
            database.insert(DatabaseHelper.FAV_MOVIES_TABLE_NAME, null, contentValues);
        }
        database.close();
    }

    // Remove movie from favorites database
    public static void removeMovieFromFavorites(Context context, Integer movieId){
        if (movieId == null) return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        if (isMovieFavorite(context, movieId)) {
            database.delete(DatabaseHelper.FAV_MOVIES_TABLE_NAME,
                    DatabaseHelper.MOVIE_ID + " = " + movieId, null);
        }
        database.close();
    }

    // Check if movie is favorite
    public static boolean isMovieFavorite(Context context, Integer movieId) {

        if (movieId == null) return false;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        boolean isMovieFavorite;
        Cursor cursor = database.query(DatabaseHelper.FAV_MOVIES_TABLE_NAME, null,
                DatabaseHelper.MOVIE_ID + " = " + movieId, null, null, null, null);
        if (cursor.getCount() == 1)
            isMovieFavorite = true;
        else
            isMovieFavorite = false;

        cursor.close();
        database.close();
        return isMovieFavorite;
    }

    // Get favorite movies
    public static List<MovieBrief> getFavoriteMovieBriefs(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        List<MovieBrief> favoriteMovies = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.FAV_MOVIES_TABLE_NAME, null,
                null, null, null, null,
                DatabaseHelper.ID + " DESC");
        while (cursor.moveToNext()) {
            int movieId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MOVIE_ID));
            String posterPath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.POSTER_PATH));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            favoriteMovies.add(new MovieBrief(movieId, name,posterPath, null));
        }
        cursor.close();
        database.close();
        return favoriteMovies;
    }

    // TV Shows

    // Add tv show to favorites
    public static void addTVShowToFav(Context context, Integer tvShowId, String posterPath, String name) {
        if (tvShowId == null) return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (!isTVShowFav(context, tvShowId)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.TV_SHOW_ID, tvShowId);
            contentValues.put(DatabaseHelper.POSTER_PATH, posterPath);
            contentValues.put(DatabaseHelper.NAME, name);
            database.insert(DatabaseHelper.FAV_TV_SHOWS_TABLE_NAME, null, contentValues);
        }
        database.close();
    }

    // Remove tv show from favorites
    public static void removeTVShowFromFav(Context context, Integer tvShowId) {
        if (tvShowId == null) return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        if (isTVShowFav(context, tvShowId)) {
            database.delete(DatabaseHelper.FAV_TV_SHOWS_TABLE_NAME, DatabaseHelper.TV_SHOW_ID + " = " + tvShowId, null);
        }
        database.close();
    }

    // Check if tv show is favorite
    public static boolean isTVShowFav(Context context, Integer tvShowId) {
        if (tvShowId == null) return false;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        boolean isTVShowFav;
        Cursor cursor = database.query(DatabaseHelper.FAV_TV_SHOWS_TABLE_NAME, null, DatabaseHelper.TV_SHOW_ID + " = " + tvShowId, null, null, null, null);
        if (cursor.getCount() == 1)
            isTVShowFav = true;
        else
            isTVShowFav = false;

        cursor.close();
        database.close();
        return isTVShowFav;
    }

    // Get tv show favorites
    public static List<TVShowBrief> getFavTVShowBriefs(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        List<TVShowBrief> favTVShows = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.FAV_TV_SHOWS_TABLE_NAME, null,
                null, null, null, null,
                DatabaseHelper.ID + " DESC");
        while (cursor.moveToNext()) {
            int tvShowId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TV_SHOW_ID));
            String posterPath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.POSTER_PATH));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME));
            favTVShows.add(new TVShowBrief(tvShowId, name, posterPath, null));
        }
        cursor.close();
        database.close();
        return favTVShows;
    }
}
