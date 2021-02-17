package pt.josegamerpt.statusportugal.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import needle.Needle;
import needle.UiRelatedTask;
import pt.josegamerpt.statusportugal.AppUtils;
import pt.josegamerpt.statusportugal.R;

public class fragment_vac extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context c;
    private View v;
    private List<JSONObject> vacList = new ArrayList<>();

    public fragment_vac() {
    }

    public static fragment_vac newInstance(String param1, String param2) {
        fragment_vac fragment = new fragment_vac();
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
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_vac, container, false);
        c = v.getContext();
        refresh();

        //latest
        TextView latestDataHeader2 = v.findViewById(R.id.latestheader2);
        TextView latestData = v.findViewById(R.id.latestinfo);

        v.findViewById(R.id.latestdata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = c.getString(R.string.latest_post) + " " + latestDataHeader2.getText() + "\n\n" + latestData.getText();
            ClipData clip = ClipData.newPlainText("latestData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), c.getString(R.string.copied_to_clipboard).replace("%name%", c.getString(R.string.latest_post)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });

        v.findViewById(R.id.mordata).setOnLongClickListener(v -> {
            StringBuilder f = new StringBuilder("\n");
            try {
                for (JSONObject jsonObject : vacList) {
                    String title = "⎯⎯ " + convertEpochDate(jsonObject.getString("Data")) + "⎯⎯";

                    String vac = stringFormater(checkDataInt(checkData(jsonObject.getString("Vacinados"))), false);
                    String in1 = stringFormater(checkDataInt(checkData(jsonObject.getString("Inoculacao1"))), false);
                    String in2 = stringFormater(checkDataInt(checkData(jsonObject.getString("Inoculacao2"))), false);

                    f.append(title).append("\n\n").append(c.getString(R.string.vac_total)).append(": ").append(vac).append("\n").append(c.getString(R.string.vac_first)).append(": ").append(in1).append("\n").append(c.getString(R.string.vac_secnd)).append(": ").append(in2).append("\n\n");
                }
                ((TextView) v.findViewById(R.id.mordat1info)).setText(f.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
            return false;
        });

        v.findViewById(R.id.siteva).setOnClickListener(v1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vacinacaocovid19.pt/"));
            startActivity(browserIntent);
        });

        return v;
    }

    private String convertEpochDate(String s) {
        System.out.println(s);
        if (s != null && !s.equalsIgnoreCase("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
            sdf.format(new Date(Long.parseLong(s)));
            return sdf.format(new Date(Long.parseLong(s)));
        } else {
            return "?";
        }
    }

    private void refresh() {
        if (hasInternetAccess(c)) {
            SweetAlertDialog loadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            loadingDialog.getProgressHelper().setBarColor(Color.rgb(102, 178, 255));
            loadingDialog.setTitleText(c.getString(R.string.loading_info));
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            //get data
            Needle.onBackgroundThread().execute(new UiRelatedTask() {
                @Override
                protected Object doWork() {
                    try {
                        getVacData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    return null;
                }

                @Override
                protected void thenDoUiRelatedWork(Object o) {
                    try {
                        //latest
                        JSONObject current = vacList.get(0);

                        int updVac1 = checkDataInt(current.getString("Inoculacao1")) - checkDataInt(vacList.get(1).getString("Inoculacao1"));
                        int updVac2 = checkDataInt(current.getString("Inoculacao2")) - checkDataInt(vacList.get(1).getString("Inoculacao2"));
                        int updVac = checkDataInt(current.getString("Vacinados")) - checkDataInt(vacList.get(1).getString("Vacinados"));

                        ((TextView) v.findViewById(R.id.header2)).setText(c.getString(R.string.latest_post) + " " + convertEpochDate(current.getString("Data")));

                        ((TextView) v.findViewById(R.id.vac1info)).setText(checkData(stringFormater(current.getInt("Inoculacao1"), false)) + " (" + checkData(stringFormater(updVac1, true)) + ")");
                        ((TextView) v.findViewById(R.id.vac2info)).setText(checkData(stringFormater(current.getInt("Inoculacao2"), false)) + " (" + checkData(stringFormater(updVac2, true)) + ")");
                        ((TextView) v.findViewById(R.id.vactotalinfo)).setText(checkData(stringFormater(current.getInt("Vacinados"), false)) + " (" + checkData(stringFormater(updVac, true)) + ")");

                        current = vacList.get(1);
                        ((TextView) v.findViewById(R.id.header1)).setText(c.getString(R.string.previous_day_name) + " " + convertEpochDate(current.getString("Data")));

                        ((TextView) v.findViewById(R.id.yes1info)).setText(checkData(stringFormater(current.getInt("Inoculacao1"), false)));
                        ((TextView) v.findViewById(R.id.yes2info)).setText(checkData(stringFormater(current.getInt("Inoculacao2"), false)));
                        ((TextView) v.findViewById(R.id.yestotalinfo)).setText(checkData(stringFormater(current.getInt("Vacinados"), false)));

                        loadingDialog.dismissWithAnimation();

                        showWithAnimation();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        } else {
            SweetAlertDialog asd = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
            asd.setTitleText(c.getString(R.string.no_internet));
            asd.setConfirmButton(c.getString(R.string.refresh_name), sweetAlertDialog -> {
                asd.dismissWithAnimation();
                refresh();
            });
            asd.setCancelButton(c.getString(R.string.cancel_name), sweetAlertDialog -> asd.dismissWithAnimation());
            asd.show();
        }
    }

    private void showWithAnimation() {
        v.findViewById(R.id.mainvac1).setVisibility(View.VISIBLE);
        v.findViewById(R.id.siteva).setVisibility(View.VISIBLE);

        YoYo.with(Techniques.FadeIn)
                .duration(700)
                .playOn(v.findViewById(R.id.siteva));
        YoYo.with(Techniques.FadeInUp)
                .duration(700)
                .playOn(v.findViewById(R.id.mainvac1));
    }


    public void getVacData() throws JSONException {
        vacList.clear();
        JSONArray jsonArray = new JSONArray(AppUtils.getInfoFromAPI("https://vacinacaocovid19.pt/api/vaccines"));
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                vacList.add(jsonArray.getJSONObject(i));
            }
        }

        Collections.reverse(vacList);
    }

    public String stringFormater(int i, Boolean positivformat) {
        if (positivformat) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            if (i >= 0) {
                return "+" + formatter.format(i);
            } else {
                return formatter.format(i);
            }
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(i);
    }

    private String checkData(String s) {
        return s.equalsIgnoreCase("null") ? getString(R.string.missing_data) : s.replace(".0", "");
    }

    private int checkDataInt(String i) {
        if (i.equals(c.getString(R.string.missing_data))) {
            return 0;
        }
        if (TextUtils.isEmpty(i)) {
            return 0;
        }
        return i.equals("null") ? 0 : Integer.parseInt(String.valueOf(Math.round(Double.parseDouble(i))));
    }

}