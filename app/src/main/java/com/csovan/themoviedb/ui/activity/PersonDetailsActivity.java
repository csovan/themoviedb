package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.csovan.themoviedb.data.model.people.Person;

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

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;

    // ImageView
    private ImageView backdropImageView;
    private ImageView posterImageView;

    // TextView
    private TextView textViewPersonName;
    private TextView textViewBirthPlace;
    private TextView textViewBiography;

    // Calls
    private Call<Person> personDetailsCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_person_details);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setTitle("");

        Intent receivedIntent = getIntent();
        personId = receivedIntent.getIntExtra(PERSON_ID, -1);

        if (personId == -1) finish();

        personDetailsLoaded = false;

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_person_details);
        appBarLayout = findViewById(R.id.app_bar_person_details);
        nestedScrollView = findViewById(R.id.nested_scroll_view_person_details);
        progressBar = findViewById(R.id.progress_bar);

        collapsingToolbarLayout.setVisibility(View.INVISIBLE);
        nestedScrollView.setVisibility(View.INVISIBLE);

        backdropImageView = findViewById(R.id.image_view_backdrop);
        posterImageView = findViewById(R.id.image_view_poster);

        textViewPersonName = findViewById(R.id.text_view_person_name);
        textViewBirthPlace = findViewById(R.id.text_view_birth_place);
        textViewBiography = findViewById(R.id.text_view_biography_content);

        loadActivity();
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
                            textViewPersonName.setText("");
                        }
                    }
                });

                assert response.body() != null;
                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getProfilePath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_person)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(backdropImageView);

                Glide.with(getApplicationContext())
                        .load(IMAGE_LOADING_BASE_URL_1280 + response.body().getProfilePath())
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.ic_person)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(posterImageView);

                if (response.body().getPlaceOfBirth() != null
                        && !response.body().getPlaceOfBirth().trim().isEmpty()){
                    textViewBirthPlace.setText(response.body().getPlaceOfBirth());
                }
                else {
                    textViewBirthPlace.setText("");
                }

                if (response.body().getBiography() != null
                        && !response.body().getBiography().trim().isEmpty()){
                    textViewBiography.setText(response.body().getBiography());
                }
                else {
                    textViewBiography.setText("");
                }

                personDetailsLoaded = true;
                checkPersonDetailsLoaded();
            }

            @Override
            public void onFailure(@NonNull Call<Person> call, @NonNull Throwable t) {

            }
        });
    }

    private void checkPersonDetailsLoaded(){
        if (personDetailsLoaded ){
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
