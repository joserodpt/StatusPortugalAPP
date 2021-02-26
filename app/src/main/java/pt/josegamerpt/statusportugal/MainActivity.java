package pt.josegamerpt.statusportugal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import needle.Needle;
import needle.UiRelatedTask;


public class MainActivity extends AppCompatActivity {

    public static void setWhite(Activity a) {
        a.findViewById(R.id.main).setBackgroundColor(Color.WHITE);
        AppUtils.setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        a.getWindow().setStatusBarColor(Color.WHITE);
        a.getWindow().setNavigationBarColor(Color.WHITE);
    }

    public static void setDark(Activity a) {
        a.findViewById(R.id.main).setBackgroundColor(Color.BLACK);
        AppUtils.setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        a.getWindow().setStatusBarColor(Color.BLACK);
        a.getWindow().setNavigationBarColor(Color.BLACK);
    }

    public static boolean hasInternetAccess(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
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
        setContentView(R.layout.activity_main);
        checkDark(getResources().getConfiguration());

        getData();
    }

    private void getData() {
        if (hasInternetAccess(this)) {
            //get data
            Needle.onBackgroundThread().execute(new UiRelatedTask() {
                @Override
                protected Object doWork() {
                    try {
                        getLatest();
                        getSecond();
                        getVacData();
                        return true;
                    } catch (Exception e) {
                        MainActivity.this.runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        });
                        return false;

                    }
                }

                @Override
                protected void thenDoUiRelatedWork(Object o) {
                    if ((Boolean) o)
                        go();
                }
            });
        } else {
            SweetAlertDialog asd = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            asd.setTitleText(getString(R.string.no_internet));
            asd.setConfirmButton(getString(R.string.refresh_name), sweetAlertDialog -> {
                asd.dismissWithAnimation();
                getData();
            });
            asd.setCancelButton(getString(R.string.cancel_name), sweetAlertDialog -> asd.dismissWithAnimation());
            asd.show();
        }
    }

    private void go() {
        Intent i = new Intent(this, HomeScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void getLatest() throws Exception {
        StatusPortugal.latest = new JSONObject(AppUtils.getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_last_update"));
    }

    public void getSecond() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        Date d = sdf.parse(StatusPortugal.latest.getString("data"));
        Calendar yst = Calendar.getInstance();
        yst.setTime(d);
        yst.add(Calendar.DATE, -1);

        Date date = yst.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        String stringYesterday = format1.format(date);

        StatusPortugal.yesterday = new JSONObject(AppUtils.getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_entry/" + stringYesterday));
    }

    public void getVacData() throws Exception {
        StatusPortugal.vacList.clear();
        JSONArray jsonArray = new JSONArray(AppUtils.getInfoFromAPI("https://vacinacaocovid19.pt/api/vaccines"));
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                StatusPortugal.vacList.add(jsonArray.getJSONObject(i));
            }
        }

        Collections.reverse(StatusPortugal.vacList);
    }

    //dark mode support
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        checkDark(config);
    }
}