package com.example.geoxplore;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.geoxplore.map.MapFragment;
import com.example.geoxplore.utils.SavedData;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String CURRENT_TAG = null;
    Handler mHandler = null;

    Bundle fragmentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentBundle = new Bundle();
        fragmentBundle.putString(Intent.EXTRA_USER, getIntent().getExtras().getString(Intent.EXTRA_USER));

        mHandler = new Handler();
        Mapbox.getInstance(this, getString(R.string.token_mb));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerlayout = navigationView.getHeaderView(0);
        headerlayout.findViewById(R.id.nav_header_image).
                setOnClickListener(listener -> {
                    Fragment fragment = new UserProfileFragment();
                    fragment.setArguments(fragmentBundle);
                    loadFragment(fragment, UserProfileFragment.TAG);
                    drawer.closeDrawer(GravityCompat.START);
                });

        //set main fragment
        MapFragment fragment = new MapFragment();
        fragment.setArguments(fragmentBundle);
        loadFragment(fragment, MapFragment.TAG);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // TODO ogarnac, zeby w menu podswiatlene był ładnie aktualny fragment
        // TODO dodac profil tez do menu?
        // TODO ogarnac jakos lepiej ladowanie fragmentow i przekazywanie do nich tokena

        int id = item.getItemId();
    //TODO dodac lepsze ikonki
        switch (id) {
            case R.id.nav_map:
                fragmentBundle.putBoolean(MapFragment.RESET_HOME, false);
                MapFragment fragment = new MapFragment();
                fragment.setArguments(fragmentBundle);
                loadFragment(fragment, MapFragment.TAG);
                break;
            case R.id.nav_ranking:
                RankingFragment fragment1 = new RankingFragment();
                fragment1.setArguments(fragmentBundle);
                loadFragment(fragment1, RankingFragment.TAG);
                break;
            case R.id.nav_logout:
                SavedData.clear(getApplicationContext());
                super.onBackPressed();
                break;
            case R.id.nav_settings:
                fragmentBundle.putBoolean(MapFragment.RESET_HOME, true);
                fragment = new MapFragment();
                fragment.setArguments(fragmentBundle);
                loadFragment(fragment, MapFragment.TAG);
                break;
            default:
                Toast.makeText(getApplicationContext(), "No action yet! " + id, Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFragment(final Fragment fragment, final String tag) {
//        if (Objects.equals(CURRENT_TAG, tag)) return;

        Runnable pendingRunnable = () -> {
            // update the main content by replacing fragments
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
//                fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.replace(R.id.frame, fragment, tag);
            fragmentTransaction.commit();
        };
        mHandler.postDelayed(pendingRunnable, 250);
        CURRENT_TAG = tag;
    }

}
