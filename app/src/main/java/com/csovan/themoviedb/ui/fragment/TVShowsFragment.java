package com.csovan.themoviedb.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.api.ApiClient;
import com.csovan.themoviedb.data.api.ApiInterface;
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowsAiringTodayResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsOnTheAirResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsPopularResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsTopRatedResponse;
import com.csovan.themoviedb.ui.activity.TVShowsViewAllActivity;
import com.csovan.themoviedb.ui.adapter.TVShowCardLargeAdapter;
import com.csovan.themoviedb.ui.adapter.TVShowCardSmallAdapter;
import com.csovan.themoviedb.util.NetworkConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;
import static com.csovan.themoviedb.util.Constant.AIRING_TODAY_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.ON_THE_AIR_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.POPULAR_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.TOP_RATED_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.VIEW_ALL_TV_SHOWS_TYPE;

public class TVShowsFragment extends Fragment {
    private ProgressBar progressBar;
    private LinearLayout tvshowsLayout;

    // TV shows airing today
    private List<TVShowBrief> tvshowAiringTodayList;
    private Call<TVShowsAiringTodayResponse> tvShowsAiringTodayResponseCall;
    private TextView tvTVShowsAiringTodayViewAll;
    private RecyclerView rvTVShowsAiringToday;
    private TVShowCardLargeAdapter tvshowsAiringTodayAdapter;
    private boolean tvshowsAiringTodaySectionLoaded;

    // TV shows popular
    private List<TVShowBrief> tvshowPopularList;
    private Call<TVShowsPopularResponse> tvShowsPopularResponseCall;
    private TextView tvTVShowsPopularViewAll;
    private RecyclerView rvTVShowsPopular;
    private TVShowCardSmallAdapter tvshowsPopularAdapter;
    private boolean tvshowsPopularSectionLoaded;

    // TV shows on the air
    private List<TVShowBrief> tvshowOnTheAirList;
    private Call<TVShowsOnTheAirResponse> tvShowsOnTheAirResponseCall;
    private TextView tvTVShowsOnTheAirViewAll;
    private RecyclerView rvTVShowsOnTheAir;
    private TVShowCardSmallAdapter tvshowsOnTheAirAdapter;
    private boolean tvshowsOnTheAirSectionLoaded;

    // TV shows top rated
    private List<TVShowBrief> tvshowTopRatedList;
    private Call<TVShowsTopRatedResponse> tvShowsTopRatedResponseCall;
    private TextView tvTVShowsTopRatedViewAll;
    private RecyclerView rvTVShowsTopRated;
    private TVShowCardSmallAdapter tvshowsTopRatedAdapter;
    private boolean tvshowsTopRatedSectionLoaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tv_shows, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        tvshowsLayout = view.findViewById(R.id.linear_layout_tv_shows);
        tvshowsLayout.setVisibility(View.GONE);

        // TV shows airing today section
        tvTVShowsAiringTodayViewAll = view.findViewById(R.id.text_view_tv_shows_airing_today_view_all);
        rvTVShowsAiringToday = view.findViewById(R.id.recycler_view_tv_shows_airing_today);

        tvshowAiringTodayList = new ArrayList<>();
        tvshowsAiringTodayAdapter = new TVShowCardLargeAdapter(getContext(), tvshowAiringTodayList);
        rvTVShowsAiringToday.setAdapter(tvshowsAiringTodayAdapter);
        rvTVShowsAiringToday.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        (new LinearSnapHelper()).attachToRecyclerView(rvTVShowsAiringToday);

        tvshowsAiringTodaySectionLoaded = false;

        // TV shows popular section
        tvTVShowsPopularViewAll = view.findViewById(R.id.text_view_tv_shows_popular_view_all);
        rvTVShowsPopular = view.findViewById(R.id.recycler_view_tv_shows_popular);

        tvshowPopularList = new ArrayList<>();
        tvshowsPopularAdapter = new TVShowCardSmallAdapter(getContext(), tvshowPopularList);
        rvTVShowsPopular.setAdapter(tvshowsPopularAdapter);
        rvTVShowsPopular.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsPopularSectionLoaded = false;

        // TV shows on the air section
        tvTVShowsOnTheAirViewAll = view.findViewById(R.id.text_view_tv_shows_on_the_air_view_all);
        rvTVShowsOnTheAir = view.findViewById(R.id.recycler_view_tv_shows_on_the_air);

        tvshowOnTheAirList = new ArrayList<>();
        tvshowsOnTheAirAdapter = new TVShowCardSmallAdapter(getContext(), tvshowOnTheAirList);
        rvTVShowsOnTheAir.setAdapter(tvshowsOnTheAirAdapter);
        rvTVShowsOnTheAir.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsOnTheAirSectionLoaded = false;

        // TV shows top rated section
        tvTVShowsTopRatedViewAll = view.findViewById(R.id.text_view_tv_shows_top_rated_view_all);
        rvTVShowsTopRated = view.findViewById(R.id.recycler_view_tv_shows_top_rated);

        tvshowTopRatedList = new ArrayList<>();
        tvshowsTopRatedAdapter = new TVShowCardSmallAdapter(getContext(), tvshowTopRatedList);
        rvTVShowsTopRated.setAdapter(tvshowsTopRatedAdapter);
        rvTVShowsTopRated.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsTopRatedSectionLoaded = false;

        // Set on click listener for view all
        tvTVShowsOnTheAirViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, ON_THE_AIR_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        tvTVShowsPopularViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, POPULAR_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        tvTVShowsAiringTodayViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, AIRING_TODAY_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        tvTVShowsTopRatedViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, TOP_RATED_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        if (NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            loadTVShowsFragment();
        }

        return view;
    }

    private void loadTVShowsFragment(){
        loadTVShowsAiringToday();
        loadTVShowsPopular();
        loadTVShowsOnTheAir();
        loadTVShowsTopRated();
    }

    private void loadTVShowsAiringToday(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsAiringTodayResponseCall = apiService.getTVShowsAiringToday(TMDB_API_KEY,1,"US");
        tvShowsAiringTodayResponseCall.enqueue(new Callback<TVShowsAiringTodayResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsAiringTodayResponse> call, @NonNull Response<TVShowsAiringTodayResponse> response) {
                if (!response.isSuccessful()){
                    tvShowsAiringTodayResponseCall = call.clone();
                    tvShowsAiringTodayResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsAiringTodaySectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()){
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowAiringTodayList.add(tvshow);
                }
                tvshowsAiringTodayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsAiringTodayResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void loadTVShowsPopular(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsPopularResponseCall = apiService.getTVShowsPopular(TMDB_API_KEY,1,"US");
        tvShowsPopularResponseCall.enqueue(new Callback<TVShowsPopularResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsPopularResponse> call, @NonNull Response<TVShowsPopularResponse> response) {
                if (!response.isSuccessful()){
                    tvShowsPopularResponseCall = call.clone();
                    tvShowsPopularResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsPopularSectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()){
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowPopularList.add(tvshow);
                }
                tvshowsPopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsPopularResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void loadTVShowsOnTheAir(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsOnTheAirResponseCall = apiService.getTVShowsOnTheAir(TMDB_API_KEY,1,"US");
        tvShowsOnTheAirResponseCall.enqueue(new Callback<TVShowsOnTheAirResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsOnTheAirResponse> call, @NonNull Response<TVShowsOnTheAirResponse> response) {
                if (!response.isSuccessful()){
                    tvShowsOnTheAirResponseCall = call.clone();
                    tvShowsOnTheAirResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsOnTheAirSectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()){
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowOnTheAirList.add(tvshow);
                }
                tvshowsOnTheAirAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsOnTheAirResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void loadTVShowsTopRated(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvShowsTopRatedResponseCall = apiService.getTVShowsTopRated(TMDB_API_KEY,1,"US");
        tvShowsTopRatedResponseCall.enqueue(new Callback<TVShowsTopRatedResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsTopRatedResponse> call, @NonNull Response<TVShowsTopRatedResponse> response) {
                if (!response.isSuccessful()){
                    tvShowsTopRatedResponseCall = call.clone();
                    tvShowsTopRatedResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsTopRatedSectionLoaded = true;
                checkAllSectionLoaded();

                for (TVShowBrief tvshow : response.body().getResults()){
                    if (tvshow != null && tvshow.getPosterPath() != null)
                        tvshowTopRatedList.add(tvshow);
                }
                tvshowsTopRatedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsTopRatedResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void checkAllSectionLoaded() {
        if (tvshowsOnTheAirSectionLoaded && tvshowsPopularSectionLoaded
                && tvshowsAiringTodaySectionLoaded && tvshowsTopRatedSectionLoaded){

            progressBar.setVisibility(View.GONE);
            tvshowsLayout.setVisibility(View.VISIBLE);
        }
    }

}
