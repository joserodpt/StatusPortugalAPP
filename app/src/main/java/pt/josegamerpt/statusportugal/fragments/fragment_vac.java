package pt.josegamerpt.statusportugal.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pt.josegamerpt.statusportugal.R;

import static pt.josegamerpt.statusportugal.StatusPortugal.vacList;

public class fragment_vac extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context c;
    private View v;

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

        try {
            refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        v.findViewById(R.id.clickData).setOnClickListener(v -> {
            SweetAlertDialog asd = new SweetAlertDialog(c);
            asd.setTitleText(getString(R.string.about_data));
            asd.setConfirmButton(getString(android.R.string.ok), sweetAlertDialog -> asd.dismissWithAnimation());
            asd.show();
        });

        v.findViewById(R.id.mordat1info).setOnLongClickListener(v -> {
            StringBuilder f = new StringBuilder("\n");
            try {
                for (JSONObject jsonObject : vacList) {
                    String title = "⎯⎯ " + convertEpochDate(jsonObject.getString("Data")) + "⎯⎯";

                    String vac = stringFormater(checkDataInt(checkData(jsonObject.getString("Vacinados"))), false);
                    String in1 = stringFormater(checkDataInt(checkData(jsonObject.getString("Inoculacao1"))), false);
                    String in2 = stringFormater(checkDataInt(checkData(jsonObject.getString("Inoculacao2"))), false);
                    String tot = stringFormater(checkDataInt(checkData(jsonObject.getString("Vacinados_Ac"))), false);
                    String tot1 = stringFormater(checkDataInt(checkData(jsonObject.getString("Inoculacao1_Ac"))), false);
                    String tot2 = stringFormater(checkDataInt(checkData(jsonObject.getString("Inoculacao2_Ac"))), false);

                    f.append(title).append("\n\n")
                            .append(c.getString(R.string.vac_total)).
                            append(": ").append(vac).append("\n> ").
                            append(c.getString(R.string.vac_first)).append(": ").append(in1).append("\n> ").
                            append(c.getString(R.string.vac_secnd)).append(": ").append(in2).append("\n\n").
                            append(c.getString(R.string.vac_fixed_title)).append(": ").append(tot).append("\n> ").
                            append(c.getString(R.string.vac_first)).append(": ").append(tot1).append("\n> ").
                            append(c.getString(R.string.vac_secnd)).append(": ").append(tot2).append("\n\n");
                }
                ((TextView) v.findViewById(R.id.mordat1info)).setText(f.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
            return false;
        });

        v.findViewById(R.id.webAcess).setOnClickListener(v1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vacinacaocovid19.pt/"));
            startActivity(browserIntent);
        });

        return v;
    }

    private String convertEpochDate(String s) {
        System.out.println(s);
        if (s != null && !s.equalsIgnoreCase("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
            sdf.format(new Date(Long.parseLong(s)));
            return sdf.format(new Date(Long.parseLong(s)));
        } else {
            return "?";
        }
    }

    private void refresh() throws JSONException {
        JSONObject current = vacList.get(0);

        ((TextView) v.findViewById(R.id.vacUpdDay)).setText(convertEpochDate(current.getString("Data")));
        ((TextView) v.findViewById(R.id.header2)).setText(c.getString(R.string.latest_post) + " | " + convertEpochDate(current.getString("Data")));

        int updVac1 = checkDataInt(current.getString("Inoculacao1")) - checkDataInt(vacList.get(1).getString("Inoculacao1"));
        int updVac2 = checkDataInt(current.getString("Inoculacao2")) - checkDataInt(vacList.get(1).getString("Inoculacao2"));
        int updVac = checkDataInt(current.getString("Vacinados")) - checkDataInt(vacList.get(1).getString("Vacinados"));

        ((TextView) v.findViewById(R.id.vac1info)).setText(checkData(stringFormater(current.getInt("Inoculacao1"), false)) + " (" + checkData(stringFormater(updVac1, true)) + ")");
        ((TextView) v.findViewById(R.id.vac2info)).setText(checkData(stringFormater(current.getInt("Inoculacao2"), false)) + " (" + checkData(stringFormater(updVac2, true)) + ")");
        ((TextView) v.findViewById(R.id.vactotalinfo)).setText(checkData(stringFormater(current.getInt("Vacinados"), false)) + " (" + checkData(stringFormater(updVac, true)) + ")");

        String s = "Total: "
                + stringFormater(current.getInt("Vacinados_Ac"), false)
                + "\n1º Dose: " + stringFormater(current.getInt("Inoculacao1_Ac"), false)
                + "\n2º Dose: " + stringFormater(current.getInt("Inoculacao2_Ac"), false);

        ((TextView) v.findViewById(R.id.fixeddatainfoaaa)).setText(s);

        current = vacList.get(1);

        ((TextView) v.findViewById(R.id.header1)).setText(c.getString(R.string.previous_day_name) + " | " + convertEpochDate(current.getString("Data")));

        ((TextView) v.findViewById(R.id.yes1info)).setText(checkData(stringFormater(current.getInt("Inoculacao1"), false)));
        ((TextView) v.findViewById(R.id.yes2info)).setText(checkData(stringFormater(current.getInt("Inoculacao2"), false)));
        ((TextView) v.findViewById(R.id.yestotalinfo)).setText(checkData(stringFormater(current.getInt("Vacinados"), false)));


        String s2 = "Total: "
                + stringFormater(current.getInt("Vacinados_Ac"), false)
                + "\n1º Dose: " + stringFormater(current.getInt("Inoculacao1_Ac"), false)
                + "\n2º Dose: " + stringFormater(current.getInt("Inoculacao2_Ac"), false);

        ((TextView) v.findViewById(R.id.yesterdayfixedtotalinfo)).setText(s2);

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