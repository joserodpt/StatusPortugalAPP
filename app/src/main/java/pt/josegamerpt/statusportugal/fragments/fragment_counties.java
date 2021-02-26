package pt.josegamerpt.statusportugal.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import needle.Needle;
import needle.UiRelatedTask;
import pl.utkala.searchablespinner.SearchableSpinner;
import pt.josegamerpt.statusportugal.AppUtils;
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
    JSONObject concelhoData;
    View v;
    Context c;
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
        c = v.getContext();

        SearchableSpinner sp = v.findViewById(R.id.spinnerConcelhos);
        sp.setDialogTitle(c.getString(R.string.concelho_select) + " (308)");
        sp.setDismissText(getString(android.R.string.ok));

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String concelho = sp.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(concelho)) {
                    refresh(concelho);
                } else {
                    String tmp = "- ";
                    ((TextView) v.findViewById(R.id.latestLine1Info)).setText(tmp);
                    ((TextView) v.findViewById(R.id.latestLine2Info)).setText(tmp);
                    ((TextView) v.findViewById(R.id.latestLine4Info)).setText(tmp);
                    ((TextView) v.findViewById(R.id.latestLine3Info)).setText(tmp);
                    ((TextView) v.findViewById(R.id.latestLine5Info)).setText(tmp);
                    ((TextView) v.findViewById(R.id.latestLine6Info)).setText(tmp);
                    ((TextView) v.findViewById(R.id.latestLine7Info)).setText(tmp);
                    ((TextView) v.findViewById(R.id.incidcat)).setText(tmp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    private void refresh(String con) {
        if (hasInternetAccess(c)) {
            SweetAlertDialog loadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            loadingDialog.getProgressHelper().setBarColor(Color.rgb(53, 106, 251));
            loadingDialog.setTitleText(c.getString(R.string.loading_info));
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            Needle.onBackgroundThread().execute(new UiRelatedTask() {
                @Override
                protected Object doWork() {
                    try {
                        return AppUtils.getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_last_update_specific_county/" + con);
                    } catch (IOException e) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(c, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        });
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void thenDoUiRelatedWork(Object o) {
                    try {
                        concelhoData = new JSONObject(o.toString().replaceAll("^.|.$", ""));

                        if (!concelhoData.has("status")) {
                            String date = checkData(concelhoData.getString("data"));
                            String distrito = checkData(concelhoData.getString("distrito"));
                            String ars = checkData(concelhoData.getString("ars"));

                            String incidencia = checkData(concelhoData.getString("incidencia"));
                            String incidcat = checkData(concelhoData.getString("incidencia_categoria"));
                            String incidencia_risco = checkData(concelhoData.getString("incidencia_risco"));
                            String population = checkData(concelhoData.getString("population"));
                            String densidade_pop = checkData(concelhoData.getString("densidade_populacional"));

                            ((TextView) v.findViewById(R.id.latestLine1Info)).setText(distrito);
                            ((TextView) v.findViewById(R.id.latestLine2Info)).setText(ars);
                            ((TextView) v.findViewById(R.id.latestLine3Info)).setText(incidencia_risco);
                            ((TextView) v.findViewById(R.id.latestLine4Info)).setText(incidencia.replaceAll("#\\[.*?]#", ""));
                            ((TextView) v.findViewById(R.id.latestLine5Info)).setText(population);
                            ((TextView) v.findViewById(R.id.latestLine6Info)).setText(densidade_pop);
                            ((TextView) v.findViewById(R.id.latestLine7Info)).setText(date);
                            ((TextView) v.findViewById(R.id.incidcat)).setText(incidcat);
                        } else {
                            String tmp = "- ";
                            ((TextView) v.findViewById(R.id.latestLine1Info)).setText(tmp);
                            ((TextView) v.findViewById(R.id.latestLine2Info)).setText(tmp);
                            ((TextView) v.findViewById(R.id.latestLine4Info)).setText(tmp);
                            ((TextView) v.findViewById(R.id.latestLine3Info)).setText(tmp);
                            ((TextView) v.findViewById(R.id.latestLine5Info)).setText(tmp);
                            ((TextView) v.findViewById(R.id.latestLine6Info)).setText(tmp);
                            ((TextView) v.findViewById(R.id.latestLine7Info)).setText(tmp);
                            ((TextView) v.findViewById(R.id.incidcat)).setText(tmp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    loadingDialog.dismissWithAnimation();
                }
            });
        } else {
            SweetAlertDialog asd = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
            asd.setTitleText(c.getString(R.string.no_internet));
            asd.setConfirmButton(c.getString(R.string.refresh_name), sweetAlertDialog -> {
                asd.dismissWithAnimation();
                refresh(con);
            });
            asd.setCancelButton(c.getString(R.string.cancel_name), sweetAlertDialog -> asd.dismissWithAnimation());
            asd.show();
        }
    }

    private String checkData(String s) {
        if (TextUtils.isEmpty(s)) {
            return getString(R.string.missing_data);
        }
        return s.equalsIgnoreCase("null") ? getString(R.string.missing_data) : s;
    }
}