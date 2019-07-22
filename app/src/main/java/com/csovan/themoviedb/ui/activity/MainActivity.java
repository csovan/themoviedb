package com.csovan.themoviedb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.csovan.themoviedb.R;
import com.csovan.themoviedb.ui.fragment.HomeFragment;
import com.csovan.themoviedb.ui.fragment.MoviesFragment;
import com.csovan.themoviedb.ui.fragment.TVShowsFragment;
import com.csovan.themoviedb.util.NetworkConnection;

import static com.csovan.themoviedb.util.Constant.QUERY;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.menu_home));

        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = findViewById(R.id.nav_view_main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        setFragment(new HomeFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!NetworkConnection.isConnected(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, R.string.no_network_connection,
                            Toast.LENGTH_LONG).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(QUERY, query);
                startActivity(intent);
                searchMenuItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);

        switch (id) {
            case R.id.nav_home:
                setFragment(new HomeFragment());
                setTitle(getString(R.string.menu_home));
                return true;
            case R.id.nav_movies:
                setFragment(new MoviesFragment());
                setTitle(getString(R.string.menu_movies));
                return true;
            case R.id.nav_tv_shows:
                setFragment(new TVShowsFragment());
                setTitle(getString(R.string.menu_tv_shows));
                return true;
            case R.id.nav_favorites:
                setTitle(getString(R.string.menu_favorites));
                return true;
        }
        return false;
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_main, fragment);
        fragmentTransaction.commit();
    }
}
