package pt.josegamerpt.statusportugal.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import needle.Needle;
import needle.UiRelatedTask;
import pl.utkala.searchablespinner.SearchableSpinner;
import pt.josegamerpt.statusportugal.AppUtils;
import pt.josegamerpt.statusportugal.MainActivity;
import pt.josegamerpt.statusportugal.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_counties#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_counties extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SweetAlertDialog loadingDialog;
    JSONObject concelhoData;
    View v;
    private String mParam1;
    private String mParam2;

    public fragment_counties() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_counties.
     */
    public static fragment_counties newInstance(String param1, String param2) {
        fragment_counties fragment = new fragment_counties();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static boolean hasInternetAccess(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_counties, container, false);

        SearchableSpinner sp = v.findViewById(R.id.spinnerConcelhos);
        sp.setDialogTitle(MainActivity.c.getString(R.string.concelho_select) + " (308)");
        sp.setDismissText(getString(android.R.string.ok));

        TextView tv = v.findViewById(R.id.conc);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String concelho = sp.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(concelho)) {
                    tv.setGravity(Gravity.NO_GRAVITY);
                    refresh(concelho);
                } else {
                    tv.setGravity(Gravity.CENTER);
                    tv.setText(MainActivity.c.getString(R.string.concelho_tip));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    private void refresh(String con) {
        loadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.rgb(51, 51, 255));
        loadingDialog.setTitleText(MainActivity.c.getString(R.string.loading_info));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        Needle.onBackgroundThread().execute(new UiRelatedTask() {
            @Override
            protected Object doWork() {
                return hasInternetAccess(getContext());
            }

            @Override
            protected void thenDoUiRelatedWork(Object o) {
                if ((boolean) o) {
                    getData(con);
                } else {
                    loadingDialog.dismissWithAnimation();
                    SweetAlertDialog asd = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                    asd.setTitleText(MainActivity.c.getString(R.string.no_internet));
                    asd.setConfirmButton(MainActivity.c.getString(R.string.refresh_name), sweetAlertDialog -> {
                        asd.dismissWithAnimation();
                        refresh(con);
                    });
                    asd.setCancelButton(MainActivity.c.getString(R.string.cancel_name), sweetAlertDialog -> asd.dismissWithAnimation());
                    asd.show();
                }
            }
        });
    }

    public void getData(String con) {
        Needle.onBackgroundThread().execute(new UiRelatedTask() {
            @Override
            protected Object doWork() {
                return AppUtils.getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_last_update_specific_county/" + con);
            }

            @Override
            protected void thenDoUiRelatedWork(Object o) {
                try {
                    concelhoData = new JSONObject(o.toString().replaceAll("^.|.$", ""));

                    TextView tlatest = v.findViewById(R.id.conc);
                    if (concelhoData.has("status")) {
                        tlatest.setText(getText(R.string.concelho_none));
                    } else {
                        tlatest.setText(formatLatest(concelhoData));
                    }

                    loadingDialog.dismissWithAnimation();
                } catch (JSONException e) {
                    e.printStackTrace();

                    TextView tlatest = v.findViewById(R.id.conc);
                    tlatest.setText(e.getMessage());
                }
            }
        });
    }

    private String formatLatest(JSONObject inf) throws JSONException {
        String date = inf.getString("data");
        String distrito = inf.getString("distrito");
        String ars = inf.getString("ars");

        Double incidencia = inf.getDouble("incidencia");
        String incidcat = inf.getString("incidencia_categoria");
        String incidencia_risco = inf.getString("incidencia_risco");
        int population = inf.getInt("population");
        Double densidade_pop = inf.getDouble("densidade_populacional");


        return MainActivity.c.getString(R.string.district_name) + ": " + distrito + "\n"
                + MainActivity.c.getString(R.string.ars_name) + ": " + ars + "\n\n"
                + MainActivity.c.getString(R.string.incidence_risk) + incidencia_risco + "\n"
                + MainActivity.c.getString(R.string.incidence_cat) + incidcat + "\n"
                + MainActivity.c.getString(R.string.incidence_name) + incidencia + "\n\n"
                + MainActivity.c.getString(R.string.population_name) + population + "\n"
                + MainActivity.c.getString(R.string.population_density) + densidade_pop + "\n\n" +
                MainActivity.c.getString(R.string.latest_post) + ": " + date;
    }

}