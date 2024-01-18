package com.example.readtrack.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.readtrack.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/

        Toolbar toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottom_navigation);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.books_fragment, R.id.search_fragment,
                R.id.profile_fragment).build();

        // For the Toolbar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // For the BottomNavigationView
        NavigationUI.setupWithNavController(bottomNav, navController);
        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNav.setOnItemReselectedListener(item -> {
            if(item.getItemId()!=navController.getCurrentDestination().getId()) {
                navController.navigate(R.id.action_pop_back);
            }
        });

        if(navController.getCurrentDestination().getId()==R.id.bookFragment) {
            bottomNav.setOnItemSelectedListener(item -> {
                navController.navigate(R.id.action_pop_back);
                return true;
            });
        }
      /*OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showBottomNavigation();
            }
        });*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        //TODO Gestire il caso della freccia per tornare indietro dopo aver guardato due book fragment di fila
         showBottomNavigation();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void showBottomNavigation() {
        bottomNav.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigation() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        bottomNav.startAnimation(fadeOut);
        bottomNav.setVisibility(View.GONE);
    }

    }


