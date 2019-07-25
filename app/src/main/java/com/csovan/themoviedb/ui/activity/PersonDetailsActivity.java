package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.csovan.themoviedb.R;
import com.csovan.themoviedb.data.api.ApiClient;
import com.csovan.themoviedb.data.api.ApiInterface;
import com.csovan.themoviedb.data.model.movie.MovieCastOfPerson;
import com.csovan.themoviedb.data.model.movie.MovieCastsOfPersonResponse;
import com.csovan.themoviedb.data.model.people.Person;
import com.csovan.themoviedb.data.model.tvshow.TVShowCastOfPerson;
import com.csovan.themoviedb.data.model.tvshow.TVShowCastsOfPersonResponse;
import com.csovan.themoviedb.data.network.ConnectivityBroadcastReceiver;
import com.csovan.themoviedb.data.network.NetworkConnection;
import com.csovan.themoviedb.ui.adapter.MovieCastOfPersonAdapter;
import com.csovan.themoviedb.ui.adapter.TVShowCastOfPersonAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.csovan.themoviedb.BuildConfig.TMDB_API_KEY;
import static com.csovan.themoviedb.util.Constant.IMAGE_LOADING_BASE_URL_1280;
import static com.csovan.themoviedb.util.Constant.PERSON_ID;

public class PersonDetailsActivity extends AppCompatActivity {

    private int personId;

    private boolean personDetailsLoaded;
    private boolean moviesCastInLoaded;
    private boolean tvshowsCastInLoaded;
    private boolean isActivityLoaded;
    private boolean isBroadcastReceiverRegistered;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NestedScrollView nestedScrollView;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;
    private Snackbar connectivitySnackbar;

    // ImageView
    private ImageView imageViewBackdrop;
    private ImageView imageViewPoster;

    // TextView
    private TextView textViewPersonName;
    private TextView textViewPersonAge;
    private TextView textViewBirthDate;
    private TextView textViewBirthPlace;
    private TextView textViewKnownFor;
    private TextView textViewBiography;

    // No data available
    private TextView textViewNoDataAvailableBiography;
    private TextView textViewNoDataAvailableMoviesCastIn;
    private TextView textViewNoDataAvailableTVShowsCastIn;

    // Movies cast in
    private RecyclerView moviesCastInRecyclerView;
    private MovieCastOfPersonAdapter movieCastOfPersonAdapter;
    private List<MovieCastOfPerson> movieCastOfPersonList;

    // TVShows cast in
    private RecyclerView tvshowsCastInRecyclerView;
    private TVShowCastOfPersonAdapter tvshowCastOfPersonAdapter;
    private List<TVShowCastOfPerson> tvshowCastOfPersonList;

    private Call<Person> personDetailsCall;
    private Call<TVShowCastsOfPersonResponse> tvshowCastOfPersonCall;
    private Call<MovieCastsOfPersonResponse> movieCastOfPersonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_person_details);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("");

        // Receive intent person id
        Intent receivedIntent = getIntent();
        personId = receivedIntent.getIntExtra(PERSON_ID, -1);

        if (personId == -1) finish();

        personDetailsLoaded = false;

        // Set findViewByIds
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_person_details);
        nestedScrollView = findViewById(R.id.nested_scroll_view_person_details);
        appBarLayout = findViewById(R.id.app_bar_person_details);
        progressBar = findViewById(R.id.progress_bar);

        collapsingToolbarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);

        imageViewBackdrop = findViewById(R.id.image_view_backdrop);
        imageViewPoster = findViewById(R.id.image_view_person_details_poster);

        textViewPersonName = findViewById(R.id.text_view_person_name);
        textViewPersonAge = findViewById(R.id.text_view_age);
        textViewBirthDate = findViewById(R.id.text_view_birth_date);
        textViewBirthPlace = findViewById(R.id.text_view_birth_place);
        textViewKnownFor = findViewById(R.id.text_view_known_for);
        textViewBiography = findViewById(R.id.text_view_biography_content);

        textViewNoDataAvailableBiography = findViewById(R.id.text_view_biography_no_data);
        textViewNoDataAvailableMoviesCastIn = findViewById(R.id.text_view_movies_cast_in_no_data);
        textViewNoDataAvailableTVShowsCastIn = findViewById(R.id.text_view_tv_shows_cast_in_no_data);

        // Set adapter movies cast in
        moviesCastInRecyclerView = findViewById(R.id.recycler_view_movies_cast_of_person);
        movieCastOfPersonList = new ArrayList<>();
        movieCastOfPersonAdapter = new MovieCastOfPersonAdapter(PersonDetailsActivity.this, movieCastOfPersonList);
        moviesCastInRecyclerView.setAdapter(movieCastOfPersonAdapter);
        moviesCastInRecyclerView.setLayoutManager(new LinearLayoutManager(PersonDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        moviesCastInLoaded = false;

        // Set adapter tvshows cast in
        tvshowsCastInRecyclerView = findViewById(R.id.recycler_view_tv_shows_cast_of_person);
        tvshowCastOfPersonList = new ArrayList<>();
        tvshowCastOfPersonAdapter = new TVShowCastOfPersonAdapter(PersonDetailsActivity.this, tvshowCastOfPersonList);
        tvshowsCastInRecyclerView.setAdapter(tvshowCastOfPersonAdapter);
        tvshowsCastInRecyclerView.setLayoutManager(new LinearLayoutManager(PersonDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        tvshowsCastInLoaded = false;

        if (NetworkConnection.isConnected(PersonDetailsActivity.this)){
            isActivityLoaded = true;
            loadActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        movieCastOfPersonAdapter.notifyDataSetChanged();
        tvshowCastOfPersonAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isActivityLoaded && !NetworkConnection.isConnected(PersonDetailsActivity.this)) {
            connectivitySnackbar = Snackbar.make(collapsingToolbarLayout, R.string.no_network_connection, Snackbar.LENGTH_INDEFINITE);
            connectivitySnackbar.show();
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver(new ConnectivityBroadcastReceiver.ConnectivityReceiverListener() {
                @Override
                public void onNetworkConnectionConnected() {
                    connectivitySnackbar.dismiss();
                    isActivityLoaded = true;
                    loadActivity();
                    isBroadcastReceiverRegistered = false;
                    unregisterReceiver(connectivityBroadcastReceiver);
                }
            });
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            isBroadcastReceiverRegistered = true;
            registerReceiver(connectivityBroadcastReceiver, intentFilter);
        } else if (!isActivityLoaded && NetworkConnection.isConnected(PersonDetailsActivity.this)) {
            isActivityLoaded = true;
            loadActivity();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if (isBroadcastReceiverRegistered){
            connectivitySnackbar.dismiss();
            isBroadcastReceiverRegistered = false;
            unregisterReceiver(connectivityBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (personDetailsCall != null) personDetailsCall.cancel();
        if (movieCastOfPersonCall != null) movieCastOfPersonCall.cancel();
        if (tvshowCastOfPersonCall != null) tvshowCastOfPersonCall.cancel();
    }

    private void loadActivity() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        personDetailsCall = apiService.getPersonDetails(personId, TMDB_API_KEY);
        personDetailsCall.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(@NonNull Call<Person> call, @NonNull final Response<Person> response) {
                if (!response.isSuccessful()) {
                    personDetailsCall = call.clone();
                    personDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                // Get person name
                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (response.body().getName() != null) {
                            if (appBarLayout.getTotalScrollRange() + verticalOffset == 0){
                                collapsingToolbarLayout.setTitle(response.body().getName());
                                textViewPersonName.setText("");
                            } else{
                                collapsingToolbarLayout.setTitle("");
                                textViewPersonName.setText(response.body().getName());
                            }
                        } else {
                            textViewPersonName.setText(getString(R.string.no_title_available));
                        }
                    }
                });

                // Get person profile picture for backdrop
                assert response.body() != null;
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getProfilePath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_person)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewBackdrop);

                // Get person profile picture for poster
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getProfilePath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_person)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageViewPoster);

                if (response.body().getPlaceOfBirth() != null
                        && !response.body().getPlaceOfBirth().trim().isEmpty()){
                    textViewBirthPlace.setText(response.body().getPlaceOfBirth());
                }
                else {
                    textViewBirthPlace.setText("N/A");
                }

                if (response.body().getKnownFor() != null && !response.body().getKnownFor().trim().isEmpty()){
                    textViewKnownFor.setText(response.body().getKnownFor());
                }else {
                    textViewKnownFor.setText("N/A");
                }

                if (response.body().getBiography() != null
                        && !response.body().getBiography().trim().isEmpty()){
                    textViewNoDataAvailableBiography.setVisibility(View.GONE);
                    textViewBiography.setText(response.body().getBiography());
                }
                else {
                    textViewBiography.setText("");
                }

                setDateOfBirthAndAge(response.body().getDateOfBirth());
                setMoviesCastIn(response.body().getId());
                setTVShowsCastIn(response.body().getId());

                personDetailsLoaded = true;
                checkPersonDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<Person> call, @NonNull Throwable t) {

            }
        });
    }

    // Get birth date and age
    private void setDateOfBirthAndAge(String dateOfBirthString){
        if (dateOfBirthString != null && !dateOfBirthString.trim().isEmpty()){
            SimpleDateFormat sdf1 = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat(
                    "MMMM d, yyyy", Locale.getDefault());
            SimpleDateFormat sdf3 = new SimpleDateFormat(
                    "yyyy", Locale.getDefault());
            try {
                Date dateOfBirth = sdf1.parse(dateOfBirthString);
                textViewBirthDate.setText(sdf2.format(dateOfBirth));
                Integer personAge = (Calendar.getInstance()
                        .get(Calendar.YEAR) - Integer.parseInt(sdf3.format(dateOfBirth)));
                textViewPersonAge.setText(String.valueOf(personAge));
            }catch (ParseException e){
                e.printStackTrace();
            }
        }else {
            textViewPersonAge.setText("N/A");
            textViewBirthDate.setText("N/A");
        }
    }

    // Get movies person cast in
    private void setMoviesCastIn(Integer personId){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        movieCastOfPersonCall = apiService.getMovieCastsOfPerson(personId, TMDB_API_KEY);
        movieCastOfPersonCall.enqueue(new Callback<MovieCastsOfPersonResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieCastsOfPersonResponse> call, @NonNull Response<MovieCastsOfPersonResponse> response) {
                if (!response.isSuccessful()){
                    movieCastOfPersonCall = call.clone();
                    movieCastOfPersonCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                for (MovieCastOfPerson movieCastOfPerson : response.body().getCasts()){
                    if (movieCastOfPerson == null) return;
                    if (movieCastOfPerson.getTitle() != null && movieCastOfPerson.getPosterPath() != null){
                        textViewNoDataAvailableMoviesCastIn.setVisibility(View.GONE);
                        movieCastOfPersonList.add(movieCastOfPerson);
                    }
                }

                if (!movieCastOfPersonList.isEmpty()){
                    movieCastOfPersonAdapter.notifyDataSetChanged();
                }

                moviesCastInLoaded = true;
                checkPersonDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<MovieCastsOfPersonResponse> call, @NonNull Throwable t) {

            }
        });

    }

    // Set tv shows cast in
    private void setTVShowsCastIn(Integer personId){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        tvshowCastOfPersonCall = apiService.getTVShowCastsOfPerson(personId, TMDB_API_KEY);
        tvshowCastOfPersonCall.enqueue(new Callback<TVShowCastsOfPersonResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowCastsOfPersonResponse> call, @NonNull Response<TVShowCastsOfPersonResponse> response) {
                if (!response.isSuccessful()){
                    tvshowCastOfPersonCall = call.clone();
                    tvshowCastOfPersonCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                for (TVShowCastOfPerson tvshowCastOfPerson : response.body().getCasts()){
                    if (tvshowCastOfPerson == null) return;
                    if (tvshowCastOfPerson.getName() != null && tvshowCastOfPerson.getPosterPath() != null){
                        textViewNoDataAvailableTVShowsCastIn.setVisibility(View.GONE);
                        tvshowCastOfPersonList.add(tvshowCastOfPerson);
                    }
                }

                if (!movieCastOfPersonList.isEmpty()){
                    tvshowCastOfPersonAdapter.notifyDataSetChanged();
                }

                tvshowsCastInLoaded = true;
                checkPersonDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<TVShowCastsOfPersonResponse> call, @NonNull Throwable t) {

            }
        });
    }

    // Check if details loaded
    private void checkPersonDetailsLoaded(){
        if (personDetailsLoaded && moviesCastInLoaded && tvshowsCastInLoaded ){
            progressBar.setVisibility(View.GONE);
            collapsingToolbarLayout.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
