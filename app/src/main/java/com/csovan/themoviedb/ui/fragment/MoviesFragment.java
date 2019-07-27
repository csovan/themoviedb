package com.csovan.themoviedb.ui.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.api.ApiClient;
import com.csovan.themoviedb.data.api.ApiInterface;
import com.csovan.themoviedb.data.model.movie.MovieBrief;
import com.csovan.themoviedb.data.model.movie.MoviesNowPlayingResponse;
import com.csovan.themoviedb.data.model.movie.MoviesPopularResponse;
import com.csovan.themoviedb.data.model.movie.MoviesTopRatedResponse;
import com.csovan.themoviedb.data.model.movie.MoviesUpcomingResponse;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.ui.activity.MoviesViewAllActivity;
import com.csovan.themoviedb.ui.adapter.MovieCardLargeAdapter;
import com.csovan.themoviedb.ui.adapter.MovieCardSmallAdapter;
import com.csovan.themoviedb.data.network.NetworkConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;
import static com.csovan.themoviedb.util.Constant.NOW_PLAYING_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.POPULAR_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.REGION;
import static com.csovan.themoviedb.util.Constant.TOP_RATED_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.UPCOMING_MOVIES_TYPE;
import static com.csovan.themoviedb.util.Constant.VIEW_ALL_MOVIES_TYPE;

public class MoviesFragment extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout linearLayoutMovies;
    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar connectivitySnackbar;
    private boolean isBroadcastReceiverRegistered;
    private boolean isMoviesFragmentLoaded;

    // Movies now playing
    private List<MovieBrief> movieNowPlayingList;
    private Call<MoviesNowPlayingResponse> moviesNowPlayingResponseCall;
    private TextView textViewMoviesNowPlayingViewAll;
    private RecyclerView recyclerViewMoviesNowPlaying;
    private MovieCardLargeAdapter moviesNowPlayingAdapter;
    private boolean moviesNowPlayingSectionLoaded;

    // Movies popular
    private List<MovieBrief> moviePopularList;
    private Call<MoviesPopularResponse> moviesPopularResponseCall;
    private TextView textViewMoviesPopularViewAll;
    private RecyclerView recyclerViewMoviesPopular;
    private MovieCardSmallAdapter moviesPopularAdapter;
    private boolean moviesPopularSectionLoaded;

    // Movies upcoming
    private List<MovieBrief> movieUpcomingList;
    private Call<MoviesUpcomingResponse> moviesUpcomingResponseCall;
    private TextView textViewMoviesUpcomingViewAll;
    private RecyclerView recyclerViewMoviesUpcoming;
    private MovieCardSmallAdapter moviesUpcomingAdapter;
    private boolean moviesUpcomingSectionLoaded;

    // Movies top rated
    private List<MovieBrief> movieTopRatedList;
    private Call<MoviesTopRatedResponse> moviesTopRatedResponseCall;
    private TextView textViewMoviesTopRatedViewAll;
    private RecyclerView recyclerViewMoviesTopRated;
    private MovieCardSmallAdapter moviesTopRatedAdapter;
    private boolean moviesTopRatedSectionLoaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        linearLayoutMovies = view.findViewById(R.id.linear_layout_movies);
        linearLayoutMovies.setVisibility(View.GONE);

        // Set Movies now playing section adapter
        textViewMoviesNowPlayingViewAll = view.findViewById(R.id.text_view_movies_now_playing_view_all);
        recyclerViewMoviesNowPlaying = view.findViewById(R.id.recycler_view_movies_now_playing);

        movieNowPlayingList = new ArrayList<>();
        moviesNowPlayingAdapter = new MovieCardLargeAdapter(getContext(), movieNowPlayingList);
        recyclerViewMoviesNowPlaying.setAdapter(moviesNowPlayingAdapter);
        recyclerViewMoviesNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        (new LinearSnapHelper()).attachToRecyclerView(recyclerViewMoviesNowPlaying);

        moviesNowPlayingSectionLoaded = false;

        // Set Movies popular section adapter
        textViewMoviesPopularViewAll = view.findViewById(R.id.text_view_movies_popular_view_all);
        recyclerViewMoviesPopular = view.findViewById(R.id.recycler_view_movies_popular);

        moviePopularList = new ArrayList<>();
        moviesPopularAdapter = new MovieCardSmallAdapter(getContext(), moviePopularList);
        recyclerViewMoviesPopular.setAdapter(moviesPopularAdapter);
        recyclerViewMoviesPopular.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesPopularSectionLoaded = false;

        // Set Movies upcoming section adapter
        textViewMoviesUpcomingViewAll = view.findViewById(R.id.text_view_movies_upcoming_view_all);
        recyclerViewMoviesUpcoming = view.findViewById(R.id.recycler_view_movies_upcoming);

        movieUpcomingList = new ArrayList<>();
        moviesUpcomingAdapter = new MovieCardSmallAdapter(getContext(), movieUpcomingList);
        recyclerViewMoviesUpcoming.setAdapter(moviesUpcomingAdapter);
        recyclerViewMoviesUpcoming.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesUpcomingSectionLoaded = false;

        // Set Movies top rated section adapter
        textViewMoviesTopRatedViewAll = view.findViewById(R.id.text_view_movies_top_rated_view_all);
        recyclerViewMoviesTopRated = view.findViewById(R.id.recycler_view_movies_top_rated);

        movieTopRatedList = new ArrayList<>();
        moviesTopRatedAdapter = new MovieCardSmallAdapter(getContext(), movieTopRatedList);
        recyclerViewMoviesTopRated.setAdapter(moviesTopRatedAdapter);
        recyclerViewMoviesTopRated.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        moviesTopRatedSectionLoaded = false;

        // Set on click listener for view all
        textViewMoviesNowPlayingViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, NOW_PLAYING_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        textViewMoviesPopularViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, POPULAR_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        textViewMoviesUpcomingViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, UPCOMING_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        textViewMoviesTopRatedViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), MoviesViewAllActivity.class);
                intent.putExtra(VIEW_ALL_MOVIES_TYPE, TOP_RATED_MOVIES_TYPE);
                startActivity(intent);
            }
        });

        if (NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            isMoviesFragmentLoaded = true;
            loadMoviesFragment();
        }

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        moviesNowPlayingAdapter.notifyDataSetChanged();
        moviesPopularAdapter.notifyDataSetChanged();
        moviesUpcomingAdapter.notifyDataSetChanged();
        moviesTopRatedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!isMoviesFragmentLoaded && !NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            connectivitySnackbar = Snackbar.make(Objects.requireNonNull(getActivity())
                            .findViewById(R.id.fragment_container_main),
                    R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            connectivitySnackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(
                    new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    connectivitySnackbar.dismiss();
                    isMoviesFragmentLoaded = true;
                    loadMoviesFragment();
                    isBroadcastReceiverRegistered = false;
                    Objects.requireNonNull(getActivity()).unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            getActivity().registerReceiver(connectivityBroadcastReceiver, intentFilter);
        }else if (!isMoviesFragmentLoaded && NetworkConnection.isConnected(getContext())){
            isMoviesFragmentLoaded = true;
            loadMoviesFragment();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if (isBroadcastReceiverRegistered){
            connectivitySnackbar.dismiss();
            isBroadcastReceiverRegistered = false;
            Objects.requireNonNull(getActivity()).unregisterReceiver(connectivityBroadcastReceiver);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        if (moviesNowPlayingResponseCall != null) moviesNowPlayingResponseCall.cancel();
        if (moviesPopularResponseCall != null) moviesPopularResponseCall.cancel();
        if (moviesUpcomingResponseCall != null) moviesUpcomingResponseCall.cancel();
        if (moviesTopRatedResponseCall != null) moviesTopRatedResponseCall.cancel();
    }

    private void loadMoviesFragment() {

        loadMoviesNowPlaying();
        loadMoviesPopular();
        loadMoviesUpcoming();
        loadMoviesTopRated();
    }

    private void loadMoviesNowPlaying() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesNowPlayingResponseCall = apiService.getMoviesNowPlaying(TMDB_API_KEY, 1, REGION);
        moviesNowPlayingResponseCall.enqueue(new Callback<MoviesNowPlayingResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesNowPlayingResponse> call, @NonNull Response<MoviesNowPlayingResponse> response) {
                if (!response.isSuccessful()) {
                    moviesNowPlayingResponseCall = call.clone();
                    moviesNowPlayingResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesNowPlayingSectionLoaded = true;
                checkAllSectionsLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getBackdropPath() != null)
                        movieNowPlayingList.add(movie);
                }
                moviesNowPlayingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesNowPlayingResponse> call, @NonNull Throwable t) {

            }

        });
    }

    private void loadMoviesPopular() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesPopularResponseCall = apiService.getMoviesPopular(TMDB_API_KEY, 1, REGION);
        moviesPopularResponseCall.enqueue(new Callback<MoviesPopularResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesPopularResponse> call, @NonNull Response<MoviesPopularResponse> response) {
                if (!response.isSuccessful()) {
                    moviesPopularResponseCall = call.clone();
                    moviesPopularResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesPopularSectionLoaded = true;
                checkAllSectionsLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getPosterPath() != null)
                        moviePopularList.add(movie);
                }
                moviesPopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesPopularResponse> call, @NonNull Throwable t) {

            }

        });

    }

    private void loadMoviesUpcoming(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesUpcomingResponseCall = apiService.getMoviesUpcoming(TMDB_API_KEY, 1, REGION);
        moviesUpcomingResponseCall.enqueue(new Callback<MoviesUpcomingResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesUpcomingResponse> call, @NonNull Response<MoviesUpcomingResponse> response) {
                if (!response.isSuccessful()) {
                    moviesUpcomingResponseCall = call.clone();
                    moviesUpcomingResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesUpcomingSectionLoaded = true;
                checkAllSectionsLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getPosterPath() != null)
                        movieUpcomingList.add(movie);
                }
                moviesUpcomingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesUpcomingResponse> call, @NonNull Throwable t) {

            }

        });

    }

    private void loadMoviesTopRated(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        moviesTopRatedResponseCall = apiService.getMoviesTopRated(TMDB_API_KEY, 1, REGION);
        moviesTopRatedResponseCall.enqueue(new Callback<MoviesTopRatedResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesTopRatedResponse> call, @NonNull Response<MoviesTopRatedResponse> response) {
                if (!response.isSuccessful()) {
                    moviesTopRatedResponseCall = call.clone();
                    moviesTopRatedResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                moviesTopRatedSectionLoaded = true;
                checkAllSectionsLoaded();

                for (MovieBrief movie : response.body().getResults()) {
                    if (movie != null && movie.getPosterPath() != null)
                        movieTopRatedList.add(movie);
                }
                moviesTopRatedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesTopRatedResponse> call, @NonNull Throwable t) {

            }

        });

    }

    private void checkAllSectionsLoaded() {
        if (moviesNowPlayingSectionLoaded && moviesPopularSectionLoaded
                && moviesUpcomingSectionLoaded && moviesTopRatedSectionLoaded){

            progressBar.setVisibility(View.GONE);
            linearLayoutMovies.setVisibility(View.VISIBLE);
        }
    }
}
