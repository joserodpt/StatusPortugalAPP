package pt.josegamerpt.covid19hubportugal.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import needle.Needle;
import needle.UiRelatedTask;
import pt.josegamerpt.covid19hubportugal.MainActivity;
import pt.josegamerpt.covid19hubportugal.R;

public class fragment_statistics extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SweetAlertDialog loadingDialog;
    JSONObject latest;
    JSONObject yesterday;

    View v;

    public fragment_statistics() {
    }

    public static fragment_statistics newInstance(String param1, String param2) {
        fragment_statistics fragment = new fragment_statistics();
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

        v = inflater.inflate(R.layout.fragment_statistics, container, false);

        getFirst();

        //latest
        TextView latestDataHeader2 = v.findViewById(R.id.latestheader2);
        TextView latestData = v.findViewById(R.id.latestinfo);

        v.findViewById(R.id.latestdata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = MainActivity.c.getString(R.string.latest_post) + " " + latestDataHeader2.getText() + "\n\n" + latestData.getText();
            ClipData clip = ClipData.newPlainText("latestData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), MainActivity.c.getString(R.string.copied_to_clipboard).replace("%name%", MainActivity.c.getString(R.string.latest_post)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });

        //var
        TextView varheader2 = v.findViewById(R.id.variationdataheader2);
        TextView varinfo = v.findViewById(R.id.variationdatainfo);

        v.findViewById(R.id.variationdata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = MainActivity.c.getString(R.string.variation_name) + " " + varheader2.getText() + "\n\n" + varinfo.getText();
            ClipData clip = ClipData.newPlainText("varData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), MainActivity.c.getString(R.string.copied_to_clipboard).replace("%name%", MainActivity.c.getString(R.string.variation_name)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });

        //yesterday
        TextView yesterdayheader2 = v.findViewById(R.id.yesterdayHeader2);
        TextView yesinfo = v.findViewById(R.id.yestedayInfo);

        v.findViewById(R.id.yesterdaydata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = MainActivity.c.getString(R.string.previous_day_name) + " " + yesterdayheader2.getText() + "\n\n" + yesinfo.getText();
            ClipData clip = ClipData.newPlainText("yesterdayData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), MainActivity.c.getString(R.string.copied_to_clipboard).replace("%name%", MainActivity.c.getString(R.string.previous_day_name)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });

        //more
        TextView morehe2 = v.findViewById(R.id.moredataheader2);
        TextView morinfo = v.findViewById(R.id.totaldatainfo);

        v.findViewById(R.id.totaldata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = MainActivity.c.getString(R.string.more_info_name) + " " + morehe2.getText() + "\n\n" + morinfo.getText();
            ClipData clip = ClipData.newPlainText("moreInfoData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), MainActivity.c.getString(R.string.copied_to_clipboard).replace("%name%", MainActivity.c.getString(R.string.more_info_name)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });


        return v;
    }

    public void getFirst() {

        loadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.rgb(59, 130, 245));
        loadingDialog.setTitleText(MainActivity.c.getString(R.string.loading_info));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        Needle.onBackgroundThread().execute(new UiRelatedTask() {
            @Override
            protected Object doWork() {
                return getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_last_update");
            }

            @Override
            protected void thenDoUiRelatedWork(Object o) {
                try {
                    latest = new JSONObject(o.toString());

                    TextView t = v.findViewById(R.id.latestheader2);

                    t.setText(latest.getString("data_dados"));


                    TextView tlatest = v.findViewById(R.id.latestinfo);
                    tlatest.setText(formatLatest(latest));

                    //update data
                    getSecond();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String formatLatest(JSONObject inf) throws JSONException {
        int ativos = inf.getInt("ativos");
        int recuperados = inf.getInt("recuperados");
        int obitos = inf.getInt("obitos");
        int vigilancia = inf.getInt("vigilancia");
        int novos_infetados = inf.getInt("confirmados_novos");
        int total_infetados = inf.getInt("confirmados");
        //internados
        int internados = latest.getInt("internados");
        int internados_uci = latest.getInt("internados_uci");
        int internados_enfermaria = latest.getInt("internados_enfermaria");

        return MainActivity.c.getString(R.string.confirmed_name) + stringFormater(novos_infetados) + "\n\n"
                + MainActivity.c.getString(R.string.confirmed_name) + total_infetados + "\n"
                + MainActivity.c.getString(R.string.recovered_cases) + recuperados + "\n"
                + MainActivity.c.getString(R.string.surveilance_name) + vigilancia + "\n"
                + MainActivity.c.getString(R.string.active_cases_name) + ativos + "\n\n"
                + MainActivity.c.getString(R.string.internados_name) + internados + "\n"
                + MainActivity.c.getString(R.string.interned_enfermaria) + internados_enfermaria + "\n"
                + MainActivity.c.getString(R.string.interned_uci) + internados_uci + "\n\n"
                + MainActivity.c.getString(R.string.deaths_name) + obitos;
    }

    public String stringFormater(int i) {
        if (i >= 0) {
            return "+" + i;
        }
        return String.valueOf(i);
    }

    public void getSecond() {
        Needle.onBackgroundThread().execute(new UiRelatedTask() {
            @Override
            protected Object doWork() {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = sdf.parse(latest.getString("data"));
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.setTime(d);
                    yesterday.add(Calendar.DATE, -1);

                    Date date = yesterday.getTime();
                    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                    String stringYesterday = format1.format(date);

                    String get = getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_entry/" + stringYesterday);
                    return get;

                } catch (ParseException | JSONException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void thenDoUiRelatedWork(Object o) {
                try {
                    yesterday = new JSONObject(o.toString());

                    JSONObject datakey = yesterday.getJSONObject("data");
                    String key = "";

                    Iterator<String> keys = datakey.keys();
                    while (keys.hasNext()) {
                        key = keys.next();
                    }

                    TextView t = v.findViewById(R.id.yesterdayHeader2);
                    t.setText(yesterday.getJSONObject("data_dados").getString(key));
                    TextView tinfo = v.findViewById(R.id.yestedayInfo);
                    tinfo.setText(formatYesterday(yesterday, key));

                    //variation updates
                    TextView var = v.findViewById(R.id.variationdataheader2);
                    var.setText(yesterday.getJSONObject("data").getString(key) + " → " + latest.getString("data"));

                    TextView vartext = v.findViewById(R.id.variationdatainfo);
                    vartext.setText(formatVariation(latest, yesterday, key));


                    //total updates
                    TextView t2 = v.findViewById(R.id.moredataheader2);
                    t2.setText(latest.getString("data"));

                    TextView totinfo = v.findViewById(R.id.totaldatainfo);
                    totinfo.setText(formatMore(latest));

                    loadingDialog.dismissWithAnimation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private String formatMore(JSONObject latest) throws JSONException {
        //confirmados ARS
        int confirmados_arsnorte = latest.getInt("confirmados_arsnorte");
        int confirmados_arscentro = latest.getInt("confirmados_arscentro");
        int confirmados_arslvt = latest.getInt("confirmados_arslvt");
        int confirmados_arsalentejo = latest.getInt("confirmados_arsalentejo");
        int confirmados_arsalgarve = latest.getInt("confirmados_arsalgarve");
        int confirmados_acores = latest.getInt("confirmados_acores");
        int confirmados_madeira = latest.getInt("confirmados_madeira");

        int confirmados_homem = latest.getInt("confirmados_m");
        int confirmados_mulher = latest.getInt("confirmados_f");


        //confirmados idades e sexo
        int confirmados_0_9_f = latest.getInt("confirmados_0_9_f");
        int confirmados_10_19_f = latest.getInt("confirmados_10_19_f");
        int confirmados_20_29_f = latest.getInt("confirmados_20_29_f");
        int confirmados_30_39_f = latest.getInt("confirmados_30_39_f");
        int confirmados_40_49_f = latest.getInt("confirmados_40_49_f");
        int confirmados_50_59_f = latest.getInt("confirmados_50_59_f");
        int confirmados_60_69_f = latest.getInt("confirmados_60_69_f");
        int confirmados_70_79_f = latest.getInt("confirmados_70_79_f");
        int confirmados_80_plus_f = latest.getInt("confirmados_80_plus_f");

        int confirmados_0_9_m = latest.getInt("confirmados_0_9_m");
        int confirmados_10_19_m = latest.getInt("confirmados_10_19_m");
        int confirmados_20_29_m = latest.getInt("confirmados_20_29_m");
        int confirmados_30_39_m = latest.getInt("confirmados_30_39_m");
        int confirmados_40_49_m = latest.getInt("confirmados_40_49_m");
        int confirmados_50_59_m = latest.getInt("confirmados_50_59_m");
        int confirmados_60_69_m = latest.getInt("confirmados_60_69_m");
        int confirmados_70_79_m = latest.getInt("confirmados_70_79_m");
        int confirmados_80_plus_m = latest.getInt("confirmados_80_plus_m");

        //obitos
        int obitos_f = latest.getInt("obitos_f");
        int obitos_m = latest.getInt("obitos_m");

        //obitos ARS
        int obitos_arsnorte = latest.getInt("obitos_arsnorte");
        int obitos_arscentro = latest.getInt("obitos_arscentro");
        int obitos_arslvt = latest.getInt("obitos_arslvt");
        int obitos_arsalentejo = latest.getInt("obitos_arsalentejo");
        int obitos_arsalgarve = latest.getInt("obitos_arsalgarve");
        int obitos_acores = latest.getInt("obitos_acores");
        int obitos_madeira = latest.getInt("obitos_madeira");

        //obitos sexo e idade

        int obitos_0_9_f = latest.getInt("obitos_0_9_f");
        int obitos_10_19_f = latest.getInt("obitos_10_19_f");
        int obitos_20_29_f = latest.getInt("obitos_20_29_f");
        int obitos_30_39_f = latest.getInt("obitos_30_39_f");
        int obitos_40_49_f = latest.getInt("obitos_40_49_f");
        int obitos_50_59_f = latest.getInt("obitos_50_59_f");
        int obitos_60_69_f = latest.getInt("obitos_60_69_f");
        int obitos_70_79_f = latest.getInt("obitos_70_79_f");
        int obitos_80_plus_f = latest.getInt("obitos_80_plus_f");

        int obitos_0_9_m = latest.getInt("obitos_0_9_m");
        int obitos_10_19_m = latest.getInt("obitos_10_19_m");
        int obitos_20_29_m = latest.getInt("obitos_20_29_m");
        int obitos_30_39_m = latest.getInt("obitos_30_39_m");
        int obitos_40_49_m = latest.getInt("obitos_40_49_m");
        int obitos_50_59_m = latest.getInt("obitos_50_59_m");
        int obitos_60_69_m = latest.getInt("obitos_60_69_m");
        int obitos_70_79_m = latest.getInt("obitos_70_79_m");
        int obitos_80_plus_m = latest.getInt("obitos_80_plus_m");

        return MainActivity.c.getString(R.string.confirmed_north) + confirmados_arsnorte + "\n" +
                MainActivity.c.getString(R.string.confirmed_center) + confirmados_arscentro + "\n" +
                MainActivity.c.getString(R.string.confirmed_lisbonvaledotejo) + confirmados_arslvt + "\n" +
                MainActivity.c.getString(R.string.confirmed_alentejo) + confirmados_arsalentejo + "\n" +
                MainActivity.c.getString(R.string.confirmed_algarve) + confirmados_arsalgarve + "\n" +
                MainActivity.c.getString(R.string.confirmed_açores) + confirmados_acores + "\n" +
                MainActivity.c.getString(R.string.confirmed_madeira) + confirmados_madeira + "\n\n" +
                MainActivity.c.getString(R.string.confirmados_f) + confirmados_mulher + "\n\n" +
                MainActivity.c.getString(R.string.confirmed_f_09) + confirmados_0_9_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_1019) + confirmados_10_19_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_2029) + confirmados_20_29_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_3039) + confirmados_30_39_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_4049) + confirmados_40_49_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_4049) + confirmados_50_59_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_5059) + confirmados_60_69_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_6069) + confirmados_70_79_f + "\n" +
                MainActivity.c.getString(R.string.confirmed_f_80plus) + confirmados_80_plus_f + "\n" + "\n" +
                MainActivity.c.getString(R.string.confirmados_m) + confirmados_homem + "\n\n" +
                MainActivity.c.getString(R.string.confirmed_m_09) + confirmados_0_9_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_1019) + confirmados_10_19_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_2029) + confirmados_20_29_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_3039) + confirmados_30_39_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_4049) + confirmados_40_49_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_5059) + confirmados_50_59_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_6069) + confirmados_60_69_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_7079) + confirmados_70_79_m + "\n" +
                MainActivity.c.getString(R.string.confirmed_m_80plus) + confirmados_80_plus_m + "\n" + "\n" +
                MainActivity.c.getString(R.string.deaths_north) + obitos_arsnorte + "\n" +
                MainActivity.c.getString(R.string.deaths_center) + obitos_arscentro + "\n" +
                MainActivity.c.getString(R.string.deaths_lisbonvaledotejo) + obitos_arslvt + "\n" +
                MainActivity.c.getString(R.string.deaths_alentejo) + obitos_arsalentejo + "\n" +
                MainActivity.c.getString(R.string.deaths_algarve) + obitos_arsalgarve + "\n" +
                MainActivity.c.getString(R.string.deaths_açores) + obitos_acores + "\n" +
                MainActivity.c.getString(R.string.deaths_madeira) + obitos_madeira + "\n" + "\n" +
                MainActivity.c.getString(R.string.deaths_f) + obitos_f + "\n\n" +
                MainActivity.c.getString(R.string.deaths_f_09) + obitos_0_9_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_1019) + obitos_10_19_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_2029) + obitos_20_29_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_3039) + obitos_30_39_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_4049) + obitos_40_49_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_5059) + obitos_50_59_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_6069) + obitos_60_69_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_7079) + obitos_70_79_f + "\n" +
                MainActivity.c.getString(R.string.deaths_f_80plus) + obitos_80_plus_f + "\n\n" +
                MainActivity.c.getString(R.string.deaths_m) + obitos_m + "\n\n" +
                MainActivity.c.getString(R.string.deaths_m_09) + obitos_0_9_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_1019) + obitos_10_19_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_2029) + obitos_20_29_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_3039) + obitos_30_39_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_4049) + obitos_40_49_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_5059) + obitos_50_59_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_6069) + obitos_60_69_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_7079) + obitos_70_79_m + "\n" +
                MainActivity.c.getString(R.string.deaths_m_80plus) + obitos_80_plus_m;
    }

    private String formatVariation(JSONObject inf, JSONObject yesterday, String key) throws JSONException {
        int latest_ativos = inf.getInt("ativos");
        int latest_recuperados = inf.getInt("recuperados");
        int latest_obitos = inf.getInt("obitos");
        int latest_vigilancia = inf.getInt("vigilancia");
        int latest_infetados = inf.getInt("confirmados");
        int latest_total_internados = inf.getInt("internados");
        int latest_total_internados_enfermaria = inf.getInt("internados_enfermaria");
        int latest_total_internados_uci = inf.getInt("internados_uci");

        int yesterday_ativos = yesterday.getJSONObject("ativos").getInt(key);
        int yesterday_recuperados = yesterday.getJSONObject("recuperados").getInt(key);
        int yesterday_obitos = yesterday.getJSONObject("obitos").getInt(key);
        int yesterday_vigilancia = yesterday.getJSONObject("vigilancia").getInt(key);
        int yesterday_infetados = yesterday.getJSONObject("confirmados").getInt(key);
        int yesterday_total_internados = yesterday.getJSONObject("internados").getInt(key);
        int yesterday_total_internados_enfermaria = yesterday.getJSONObject("internados_enfermaria").getInt(key);
        int yesterday_total_internados_uci = yesterday.getJSONObject("internados_uci").getInt(key);

        int varAtivos = latest_ativos - yesterday_ativos;
        int varRecuperados = latest_recuperados - yesterday_recuperados;
        int varObitos = latest_obitos - yesterday_obitos;
        int varVig = latest_vigilancia - yesterday_vigilancia;
        int varNovosInf = latest_infetados - yesterday_infetados;
        int inter = latest_total_internados - yesterday_total_internados;
        int interenf = latest_total_internados_enfermaria - yesterday_total_internados_enfermaria;
        int interuci = latest_total_internados_uci - yesterday_total_internados_uci;


        //TODO: percentagens
        return MainActivity.c.getString(R.string.confirmed_name) + stringFormater(varNovosInf) + "\n"
                + MainActivity.c.getString(R.string.deaths_name) + stringFormater(varObitos) + "\n\n"
                + MainActivity.c.getString(R.string.internados_name) + stringFormater(inter) + "\n"
                + MainActivity.c.getString(R.string.interned_enfermaria) + stringFormater(interenf) + "\n"
                + MainActivity.c.getString(R.string.interned_uci) + stringFormater(interuci) + "\n\n"
                + MainActivity.c.getString(R.string.recovered_cases) + stringFormater(varRecuperados) + "\n"
                + MainActivity.c.getString(R.string.surveilance_name) + stringFormater(varVig) + "\n"
                + MainActivity.c.getString(R.string.active_cases_name) + stringFormater(varAtivos);

    }

    private String formatYesterday(JSONObject inf, String key) throws JSONException {
        int ativos = inf.getJSONObject("ativos").getInt(key);
        int recuperados = inf.getJSONObject("recuperados").getInt(key);
        int obitos = inf.getJSONObject("obitos").getInt(key);
        int vigilancia = inf.getJSONObject("vigilancia").getInt(key);
        int novos_infetados = inf.getJSONObject("confirmados_novos").getInt(key);
        int total_infetados = inf.getJSONObject("confirmados").getInt(key);

        return MainActivity.c.getString(R.string.confirmed_name) + stringFormater(novos_infetados) + "\n\n"
                + MainActivity.c.getString(R.string.confirmed_name) + total_infetados + "\n"
                + MainActivity.c.getString(R.string.recovered_cases) + recuperados + "\n"
                + MainActivity.c.getString(R.string.surveilance_name) + vigilancia + "\n"
                + MainActivity.c.getString(R.string.active_cases_name) + ativos + "\n"
                + MainActivity.c.getString(R.string.deaths_name) + obitos;
    }

    //retrieve info from api
    public String getInfoFromAPI(String link) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}