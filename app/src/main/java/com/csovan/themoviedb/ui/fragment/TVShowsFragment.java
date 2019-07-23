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
import com.csovan.themoviedb.data.model.tvshow.TVShowBrief;
import com.csovan.themoviedb.data.model.tvshow.TVShowsAiringTodayResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsOnTheAirResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsPopularResponse;
import com.csovan.themoviedb.data.model.tvshow.TVShowsTopRatedResponse;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.ui.activity.TVShowsViewAllActivity;
import com.csovan.themoviedb.ui.adapter.TVShowCardLargeAdapter;
import com.csovan.themoviedb.ui.adapter.TVShowCardSmallAdapter;
import com.csovan.themoviedb.data.network.NetworkConnection;

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
import static com.csovan.themoviedb.util.Constant.REGION;
import static com.csovan.themoviedb.util.Constant.TOP_RATED_TV_SHOWS_TYPE;
import static com.csovan.themoviedb.util.Constant.VIEW_ALL_TV_SHOWS_TYPE;

public class TVShowsFragment extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout linearLayoutTVShows;
    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar connectivitySnackbar;
    private boolean isTVShowsFragmentLoaded;
    private boolean isBroadcastReceiverRegistered;

    // TV shows airing today
    private List<TVShowBrief> tvshowAiringTodayList;
    private Call<TVShowsAiringTodayResponse> tvshowsAiringTodayResponseCall;
    private TextView textViewTVShowsAiringTodayViewAll;
    private RecyclerView recyclerViewTVShowsAiringToday;
    private TVShowCardLargeAdapter tvshowsAiringTodayAdapter;
    private boolean tvshowsAiringTodaySectionLoaded;

    // TV shows popular
    private List<TVShowBrief> tvshowPopularList;
    private Call<TVShowsPopularResponse> tvshowsPopularResponseCall;
    private TextView textViewTVShowsPopularViewAll;
    private RecyclerView recyclerViewTVShowsPopular;
    private TVShowCardSmallAdapter tvshowsPopularAdapter;
    private boolean tvshowsPopularSectionLoaded;

    // TV shows on the air
    private List<TVShowBrief> tvshowOnTheAirList;
    private Call<TVShowsOnTheAirResponse> tvshowsOnTheAirResponseCall;
    private TextView textViewTVShowsOnTheAirViewAll;
    private RecyclerView recyclerViewTVShowsOnTheAir;
    private TVShowCardSmallAdapter tvshowsOnTheAirAdapter;
    private boolean tvshowsOnTheAirSectionLoaded;

    // TV shows top rated
    private List<TVShowBrief> tvshowTopRatedList;
    private Call<TVShowsTopRatedResponse> tvshowsTopRatedResponseCall;
    private TextView textViewTVShowsTopRatedViewAll;
    private RecyclerView recyclerViewTVShowsTopRated;
    private TVShowCardSmallAdapter tvshowsTopRatedAdapter;
    private boolean tvshowsTopRatedSectionLoaded;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tv_shows, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        linearLayoutTVShows = view.findViewById(R.id.linear_layout_tv_shows);
        linearLayoutTVShows.setVisibility(View.GONE);

        // Setup TV shows airing today section adapter
        textViewTVShowsAiringTodayViewAll = view.findViewById(R.id.text_view_tv_shows_airing_today_view_all);
        recyclerViewTVShowsAiringToday = view.findViewById(R.id.recycler_view_tv_shows_airing_today);

        tvshowAiringTodayList = new ArrayList<>();
        tvshowsAiringTodayAdapter = new TVShowCardLargeAdapter(getContext(), tvshowAiringTodayList);
        recyclerViewTVShowsAiringToday.setAdapter(tvshowsAiringTodayAdapter);
        recyclerViewTVShowsAiringToday.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        (new LinearSnapHelper()).attachToRecyclerView(recyclerViewTVShowsAiringToday);

        tvshowsAiringTodaySectionLoaded = false;

        // Set up TV shows popular section adapter
        textViewTVShowsPopularViewAll = view.findViewById(R.id.text_view_tv_shows_popular_view_all);
        recyclerViewTVShowsPopular = view.findViewById(R.id.recycler_view_tv_shows_popular);

        tvshowPopularList = new ArrayList<>();
        tvshowsPopularAdapter = new TVShowCardSmallAdapter(getContext(), tvshowPopularList);
        recyclerViewTVShowsPopular.setAdapter(tvshowsPopularAdapter);
        recyclerViewTVShowsPopular.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsPopularSectionLoaded = false;

        // Setup TV shows on the air section adapter
        textViewTVShowsOnTheAirViewAll = view.findViewById(R.id.text_view_tv_shows_on_the_air_view_all);
        recyclerViewTVShowsOnTheAir = view.findViewById(R.id.recycler_view_tv_shows_on_the_air);

        tvshowOnTheAirList = new ArrayList<>();
        tvshowsOnTheAirAdapter = new TVShowCardSmallAdapter(getContext(), tvshowOnTheAirList);
        recyclerViewTVShowsOnTheAir.setAdapter(tvshowsOnTheAirAdapter);
        recyclerViewTVShowsOnTheAir.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsOnTheAirSectionLoaded = false;

        // Setup TV shows top rated section adapter
        textViewTVShowsTopRatedViewAll = view.findViewById(R.id.text_view_tv_shows_top_rated_view_all);
        recyclerViewTVShowsTopRated = view.findViewById(R.id.recycler_view_tv_shows_top_rated);

        tvshowTopRatedList = new ArrayList<>();
        tvshowsTopRatedAdapter = new TVShowCardSmallAdapter(getContext(), tvshowTopRatedList);
        recyclerViewTVShowsTopRated.setAdapter(tvshowsTopRatedAdapter);
        recyclerViewTVShowsTopRated.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        tvshowsTopRatedSectionLoaded = false;

        // Set on click listener for view all
        textViewTVShowsOnTheAirViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (!NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
                    Toast.makeText(getContext(), R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, ON_THE_AIR_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        textViewTVShowsPopularViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (!NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
                    Toast.makeText(getContext(), R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, POPULAR_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        textViewTVShowsAiringTodayViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (!NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
                    Toast.makeText(getContext(), R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, AIRING_TODAY_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        textViewTVShowsTopRatedViewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if (!NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
                    Toast.makeText(getContext(), R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), TVShowsViewAllActivity.class);
                intent.putExtra(VIEW_ALL_TV_SHOWS_TYPE, TOP_RATED_TV_SHOWS_TYPE);
                startActivity(intent);
            }
        });

        if (NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            isTVShowsFragmentLoaded = true;
            loadTVShowsFragment();
        }

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        tvshowsAiringTodayAdapter.notifyDataSetChanged();
        tvshowsPopularAdapter.notifyDataSetChanged();
        tvshowsOnTheAirAdapter.notifyDataSetChanged();
        tvshowsTopRatedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!isTVShowsFragmentLoaded && !NetworkConnection.isConnected(Objects.requireNonNull(getContext()))){
            connectivitySnackbar = Snackbar.make(Objects.requireNonNull(getActivity())
                            .findViewById(R.id.fragment_container_main),
                    R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            connectivitySnackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(
                    new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    connectivitySnackbar.dismiss();
                    isTVShowsFragmentLoaded = true;
                    loadTVShowsFragment();
                    isBroadcastReceiverRegistered = false;
                    Objects.requireNonNull(getActivity()).unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            getActivity().registerReceiver(connectivityBroadcastReceiver, intentFilter);
        }else if (!isTVShowsFragmentLoaded && NetworkConnection.isConnected(getContext())){
            isTVShowsFragmentLoaded = true;
            loadTVShowsFragment();
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

        if (tvshowsAiringTodayResponseCall != null) tvshowsAiringTodayResponseCall.cancel();
        if (tvshowsPopularResponseCall != null) tvshowsPopularResponseCall.cancel();
        if (tvshowsOnTheAirResponseCall != null) tvshowsOnTheAirResponseCall.cancel();
        if (tvshowsTopRatedResponseCall != null) tvshowsTopRatedResponseCall.cancel();
    }

    private void loadTVShowsFragment(){
        loadTVShowsAiringToday();
        loadTVShowsPopular();
        loadTVShowsOnTheAir();
        loadTVShowsTopRated();
    }

    private void loadTVShowsAiringToday(){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvshowsAiringTodayResponseCall = apiService.getTVShowsAiringToday(TMDB_API_KEY,1,REGION);
        tvshowsAiringTodayResponseCall.enqueue(new Callback<TVShowsAiringTodayResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsAiringTodayResponse> call, @NonNull Response<TVShowsAiringTodayResponse> response) {
                if (!response.isSuccessful()){
                    tvshowsAiringTodayResponseCall = call.clone();
                    tvshowsAiringTodayResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsAiringTodaySectionLoaded = true;
                checkAllSectionsLoaded();

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
        tvshowsPopularResponseCall = apiService.getTVShowsPopular(TMDB_API_KEY,1,REGION);
        tvshowsPopularResponseCall.enqueue(new Callback<TVShowsPopularResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsPopularResponse> call, @NonNull Response<TVShowsPopularResponse> response) {
                if (!response.isSuccessful()){
                    tvshowsPopularResponseCall = call.clone();
                    tvshowsPopularResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsPopularSectionLoaded = true;
                checkAllSectionsLoaded();

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
        tvshowsOnTheAirResponseCall = apiService.getTVShowsOnTheAir(TMDB_API_KEY,1,REGION);
        tvshowsOnTheAirResponseCall.enqueue(new Callback<TVShowsOnTheAirResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsOnTheAirResponse> call, @NonNull Response<TVShowsOnTheAirResponse> response) {
                if (!response.isSuccessful()){
                    tvshowsOnTheAirResponseCall = call.clone();
                    tvshowsOnTheAirResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsOnTheAirSectionLoaded = true;
                checkAllSectionsLoaded();

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
        tvshowsTopRatedResponseCall = apiService.getTVShowsTopRated(TMDB_API_KEY,1,REGION);
        tvshowsTopRatedResponseCall.enqueue(new Callback<TVShowsTopRatedResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsTopRatedResponse> call, @NonNull Response<TVShowsTopRatedResponse> response) {
                if (!response.isSuccessful()){
                    tvshowsTopRatedResponseCall = call.clone();
                    tvshowsTopRatedResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                tvshowsTopRatedSectionLoaded = true;
                checkAllSectionsLoaded();

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

    private void checkAllSectionsLoaded() {
        if (tvshowsOnTheAirSectionLoaded && tvshowsPopularSectionLoaded
                && tvshowsAiringTodaySectionLoaded && tvshowsTopRatedSectionLoaded){

            progressBar.setVisibility(View.GONE);
            linearLayoutTVShows.setVisibility(View.VISIBLE);
        }
    }

}
