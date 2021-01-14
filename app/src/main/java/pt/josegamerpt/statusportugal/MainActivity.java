package pt.josegamerpt.statusportugal;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import nl.joery.animatedbottombar.AnimatedBottomBar;
import pt.josegamerpt.statusportugal.fragments.fragment_info;
import pt.josegamerpt.statusportugal.fragments.fragment_recomendations;
import pt.josegamerpt.statusportugal.fragments.fragment_statistics;

public class MainActivity extends AppCompatActivity {

    public static Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = this;

        checkDark(getResources().getConfiguration());

        final AnimatedBottomBar abb = findViewById(R.id.bottom_bar);
        abb.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {
                Fragment f = null;
                TextView t = findViewById(R.id.header);
                switch (i1) {
                    case 0:
                        t.setText(getResources().getString(R.string.stats_name));
                        f = new fragment_statistics();
                        break;
                    case 1:
                        t.setText(getResources().getString(R.string.recomendations_name));
                        f = new fragment_recomendations();
                        break;
                    case 2:
                        t.setText(getResources().getString(R.string.settings_name));
                        f = new fragment_info();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.container, new fragment_statistics()).commit();
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

    public static void setWhite(Activity a)
    {
        a.findViewById(R.id.main).setBackgroundColor(Color.WHITE);
        AppUtils.setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        a.getWindow().setStatusBarColor(Color.WHITE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setNavigationBarColor(Color.WHITE);
        }
    }

    public static void setDark(Activity a)
    {
        a.findViewById(R.id.main).setBackgroundColor(Color.BLACK);
        AppUtils.setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        a.getWindow().setStatusBarColor(Color.BLACK);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setNavigationBarColor(Color.BLACK);
        }
    }

    //dark mode support
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        checkDark(config);
    }
}