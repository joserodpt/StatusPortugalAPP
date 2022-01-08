package pt.josegamerpt.statusportugal;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import nl.joery.animatedbottombar.AnimatedBottomBar;
import pt.josegamerpt.statusportugal.fragments.fragment_counties;
import pt.josegamerpt.statusportugal.fragments.fragment_info;
import pt.josegamerpt.statusportugal.fragments.fragment_recomendations;
import pt.josegamerpt.statusportugal.fragments.fragment_statistics;

public class HomeScreen extends AppCompatActivity {

    private AnimatedBottomBar abb;

    public void setWhite(Activity a) {
        a.findViewById(R.id.main).setBackgroundColor(Color.WHITE);
        AppUtils.setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        a.getWindow().setStatusBarColor(Color.parseColor("#79FFFFFF"));
        a.getWindow().setNavigationBarColor(Color.WHITE);

    }

    public void setDark(Activity a) {
        a.findViewById(R.id.main).setBackgroundColor(Color.BLACK);
        AppUtils.setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        a.getWindow().setStatusBarColor(Color.BLACK);
        a.getWindow().setNavigationBarColor(Color.BLACK);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        AnimatedBottomBar abb = findViewById(R.id.bottom_bar);
        savedInstanceState.putInt("selected", abb.getSelectedTab().getId());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        AnimatedBottomBar abb = findViewById(R.id.bottom_bar);
        abb.selectTabById(savedInstanceState.getInt("selected"), false);
    }

    public void checkDark(Configuration config) {
        int currentNightMode = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                setWhite(this);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                setDark(this);
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        abb = findViewById(R.id.bottom_bar);

        checkDark(getResources().getConfiguration());

        abb.clearAnimation();
        abb.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                switchTab(i1);
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });

        switchTab(0);

    }

    private void switchTab(int i1) {
        Fragment f = null;
        final AnimatedBottomBar abb = findViewById(R.id.bottom_bar);
        switch (i1) {
            case 0:
                f = new fragment_statistics();
                break;
            case 1:
                f = new fragment_counties();
                break;
            case 2:
                f = new fragment_recomendations();
                break;
            case 3:
                f = new fragment_info();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
        abb.selectTabAt(i1, true);
    }

    //dark mode support
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        checkDark(config);
    }
}