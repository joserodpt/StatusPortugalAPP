package pt.josegamerpt.statusportugal.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pt.josegamerpt.statusportugal.R;
import pt.josegamerpt.statusportugal.StatusPortugal;

public class fragment_statistics extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private JSONObject latest;
    private JSONObject yesterday;
    private Context c;
    private View v;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_statistics, container, false);
        c = v.getContext();

        latest = StatusPortugal.latest;
        yesterday = StatusPortugal.yesterday;

        v.findViewById(R.id.clikData).setOnClickListener(v -> {
            SweetAlertDialog asd = new SweetAlertDialog(c);
            asd.setTitleText(getString(R.string.about_data));
            asd.setConfirmButton(getString(android.R.string.ok), sweetAlertDialog -> asd.dismissWithAnimation());
            asd.show();
        });

        try {
            refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private void refresh() throws JSONException {
        if (!latest.has("status") && !yesterday.has("status")) {
            ((TextView) v.findViewById(R.id.firstPost2)).setText(checkData(latest.getString("data_dados")).replace("00:00", ""));
            ((TextView) v.findViewById(R.id.latestUpdDay)).setText(checkData(latest.getString("data_dados")).replace("00:00", ""));

            String key = "";
            Iterator<String> keys = yesterday.getJSONObject("data").keys();
            while (keys.hasNext()) {
                key = keys.next();
            }

            ((TextView) v.findViewById(R.id.yesPostDate)).setText(checkData(yesterday.getJSONObject("data_dados").getString(key).replace("00:00", "")));
            //actual data

            int latest_ativos = checkDataInt(checkData(latest.getString("ativos")));
            int latest_recuperados = checkDataInt(checkData(latest.getString("recuperados")));
            int latest_obitos = checkDataInt(checkData(latest.getString("obitos")));
            int latest_vigilancia = checkDataInt(checkData(latest.getString("vigilancia")));
            int latest_infetados = checkDataInt(checkData(latest.getString("confirmados")));
            int latest_total_internados = checkDataInt(checkData(latest.getString("internados")));
            int latest_total_internados_enfermaria = checkDataInt(checkData(latest.getString("internados_enfermaria")));
            int latest_total_internados_uci = checkDataInt(checkData(latest.getString("internados_uci")));
            int latest_new_cases = checkDataInt(checkData(latest.getString("confirmados_novos")));

            Double latest_incid_nacional = checkDataDouble(checkData(latest.getString("incidencia_nacional")));
            Double latest_incid_continental = checkDataDouble(checkData(latest.getString("incidencia_continente")));
            Double latest_rt_nacional = checkDataDouble(checkData(latest.getString("rt_nacional")));
            Double latest_rt_continental = checkDataDouble(checkData(latest.getString("rt_continente")));

            int yesterday_ativos = checkDataInt(checkData(yesterday.getJSONObject("ativos").getString(key)));
            int yesterday_recuperados = checkDataInt(checkData(yesterday.getJSONObject("recuperados").getString(key)));
            int yesterday_obitos = checkDataInt(checkData(yesterday.getJSONObject("obitos").getString(key)));
            int yesterday_vigilancia = checkDataInt(checkData(yesterday.getJSONObject("vigilancia").getString(key)));
            int yesterday_infetados = checkDataInt(checkData(yesterday.getJSONObject("confirmados").getString(key)));
            int yesterday_total_internados = checkDataInt(checkData(yesterday.getJSONObject("internados").getString(key)));
            int yesterday_total_internados_enfermaria = checkDataInt(checkData(yesterday.getJSONObject("internados_enfermaria").getString(key)));
            int yesterday_total_internados_uci = checkDataInt(checkData(yesterday.getJSONObject("internados_uci").getString(key)));
            int yesterday_new_cases = checkDataInt(checkData(yesterday.getJSONObject("confirmados_novos").getString(key)));


            Double yesterday_incid_nacional = checkDataDouble(checkData(yesterday.getJSONObject("incidencia_nacional").getString(key)));
            Double yesterday_incid_continental = checkDataDouble(checkData(yesterday.getJSONObject("incidencia_continente").getString(key)));
            Double yesterday_rt_nacional = checkDataDouble(checkData(yesterday.getJSONObject("rt_nacional").getString(key)));
            Double yesterday_rt_continental = checkDataDouble(checkData(yesterday.getJSONObject("rt_continente").getString(key)));


            int varAtivos = latest_ativos - yesterday_ativos;
            int varRecuperados = latest_recuperados - yesterday_recuperados;
            int varObitos = latest_obitos - yesterday_obitos;
            int varVig = latest_vigilancia - yesterday_vigilancia;
            int varNovosInf = latest_infetados - yesterday_infetados;
            int inter = latest_total_internados - yesterday_total_internados;
            int interenf = latest_total_internados_enfermaria - yesterday_total_internados_enfermaria;
            int interuci = latest_total_internados_uci - yesterday_total_internados_uci;
            int newcasesvar = latest_new_cases - yesterday_new_cases;

            Double var_incid_nacional = latest_incid_nacional - yesterday_incid_nacional;
            Double var_incid_continent = latest_incid_continental - yesterday_incid_continental;
            Double var_rt_nacional = latest_rt_nacional - yesterday_rt_nacional;
            Double var_rt_continent = latest_rt_continental - yesterday_rt_continental;


            //latest
            //line1
            ((TextView) v.findViewById(R.id.latestLine1Info)).setText(varNovosInf + " (" + stringFormater(newcasesvar) + ")");
            ((ImageView) v.findViewById(R.id.line1Ind)).setImageDrawable(getIMG(newcasesvar, true));
            //line2
            ((TextView) v.findViewById(R.id.latestLine2Info)).setText(latest_recuperados + " (" + stringFormater(varRecuperados) + ")");
            ((ImageView) v.findViewById(R.id.line2Ind)).setImageDrawable(getIMG(varRecuperados, false));
            //line3
            ((TextView) v.findViewById(R.id.latestLine3Info)).setText(latest_vigilancia + " (" + stringFormater(varVig) + ")");
            ((ImageView) v.findViewById(R.id.line3Ind)).setImageDrawable(getIMG(varVig, true));
            //line4
            ((TextView) v.findViewById(R.id.latestLine4Info)).setText(latest_ativos + " (" + stringFormater(varAtivos) + ")");
            ((ImageView) v.findViewById(R.id.line4Ind)).setImageDrawable(getIMG(varAtivos, true));
            //line5
            ((TextView) v.findViewById(R.id.latestLine5Info)).setText(latest_total_internados_uci + " (" + stringFormater(interuci) + ")");
            ((ImageView) v.findViewById(R.id.line5Ind)).setImageDrawable(getIMG(interuci, true));
            //line6
            ((TextView) v.findViewById(R.id.latestLine6Info)).setText(latest_total_internados_enfermaria + " (" + stringFormater(interenf) + ")");
            ((ImageView) v.findViewById(R.id.line6Ind)).setImageDrawable(getIMG(interenf, true));
            //line7
            ((TextView) v.findViewById(R.id.latestLine7Info)).setText(latest_total_internados + " (" + stringFormater(inter) + ")");
            ((ImageView) v.findViewById(R.id.line7Ind)).setImageDrawable(getIMG(inter, true));
            //line8
            ((TextView) v.findViewById(R.id.latestLine8Info)).setText(latest_obitos + " (" + stringFormater(varObitos) + ")");
            ((ImageView) v.findViewById(R.id.line8Ind)).setImageDrawable(getIMG(varObitos, true));

            //new
            ((TextView) v.findViewById(R.id.incidnainfo)).setText(latest_incid_nacional + " (" + stringFormater((double) Math.round(var_incid_nacional)) + ")");
            ((ImageView) v.findViewById(R.id.incidnaind)).setImageDrawable(getIMG(var_incid_nacional.intValue(), true));

            ((TextView) v.findViewById(R.id.incidcontinfo)).setText(latest_incid_continental + " (" + stringFormater((double) Math.round(var_incid_continent)) + ")");
            ((ImageView) v.findViewById(R.id.incidcontind)).setImageDrawable(getIMG(var_incid_continent.intValue(), true));

            ((TextView) v.findViewById(R.id.rtnatinfo)).setText(latest_rt_nacional + " (" + stringFormater((double) Math.round(var_rt_nacional)) + ")");
            ((ImageView) v.findViewById(R.id.rtnatind)).setImageDrawable(getIMG(var_rt_nacional.intValue(), true));

            ((TextView) v.findViewById(R.id.rtcontinfo)).setText(latest_rt_continental + " (" + stringFormater((double) Math.round(var_rt_continent)) + ")");
            ((ImageView) v.findViewById(R.id.rtcontind)).setImageDrawable(getIMG(var_rt_continent.intValue(), true));

            //yesterday
            //line1
            ((TextView) v.findViewById(R.id.yesterdayLine1Info)).setText(String.valueOf(yesterday_new_cases));
            //line2
            ((TextView) v.findViewById(R.id.yesterdayLine2Info)).setText(String.valueOf(yesterday_recuperados));
            //line3
            ((TextView) v.findViewById(R.id.yesterdayLine3Info)).setText(String.valueOf(yesterday_vigilancia));
            //line4
            ((TextView) v.findViewById(R.id.yesterdayLine4Info)).setText(String.valueOf(yesterday_ativos));
            //line5
            ((TextView) v.findViewById(R.id.yesterdayLine5Info)).setText(String.valueOf(yesterday_total_internados_uci));
            //line6
            ((TextView) v.findViewById(R.id.yesterdayLine6Info)).setText(String.valueOf(yesterday_total_internados_enfermaria));
            //line7
            ((TextView) v.findViewById(R.id.yesterdayLine7Info)).setText(String.valueOf(yesterday_total_internados));
            //line8
            ((TextView) v.findViewById(R.id.yesterdayLine8Info)).setText(String.valueOf(yesterday_obitos));

            //new
            ((TextView) v.findViewById(R.id.yesterdayincidnainfo)).setText(String.valueOf(yesterday_incid_nacional));
            ((TextView) v.findViewById(R.id.yesterdayincidcontinfo)).setText(String.valueOf(yesterday_incid_continental));
            ((TextView) v.findViewById(R.id.yesterdayrtnatinfo)).setText(String.valueOf(yesterday_rt_nacional));
            ((TextView) v.findViewById(R.id.yesterdayrtcontinfo)).setText(String.valueOf(yesterday_rt_continental));

            //more data
            ((TextView) v.findViewById(R.id.moreData)).setText(formatMore(latest));

        }
    }

    private Double checkDataDouble(String i) {
        if (i.equals(c.getString(R.string.missing_data))) {
            return 0D;
        }
        if (TextUtils.isEmpty(i)) {
            return 0D;
        }
        return i.equals("null") ? 0D : Double.parseDouble(i);
    }

    private Drawable getIMG(int numero, boolean goodStat) {
        if (numero == 0) {
            return c.getDrawable(R.drawable.ic_arrow_0);
        }

        return goodStat ? numero > 0 ? c.getDrawable(R.drawable.ic_up_arrow) : c.getDrawable(R.drawable.ic_down_arrow) : numero >= 0 ? c.getDrawable(R.drawable.ic_up_arrow_inverted) : c.getDrawable(R.drawable.ic_down_arrow_inverted);
    }

    private String formatMore(JSONObject latest) throws JSONException {
        //confirmados ARS
        String confirmados_arsnorte = checkData(latest.getString("confirmados_arsnorte"));
        String confirmados_arscentro = checkData(latest.getString("confirmados_arscentro"));
        String confirmados_arslvt = checkData(latest.getString("confirmados_arslvt"));
        String confirmados_arsalentejo = checkData(latest.getString("confirmados_arsalentejo"));
        String confirmados_arsalgarve = checkData(latest.getString("confirmados_arsalgarve"));
        String confirmados_acores = checkData(latest.getString("confirmados_acores"));
        String confirmados_madeira = checkData(latest.getString("confirmados_madeira"));

        String confirmados_homem = checkData(latest.getString("confirmados_m"));
        String confirmados_mulher = checkData(latest.getString("confirmados_f"));


        //confirmados idades e sexo
        String confirmados_0_9_f = checkData(latest.getString("confirmados_0_9_f"));
        String confirmados_10_19_f = checkData(latest.getString("confirmados_10_19_f"));
        String confirmados_20_29_f = checkData(latest.getString("confirmados_20_29_f"));
        String confirmados_30_39_f = checkData(latest.getString("confirmados_30_39_f"));
        String confirmados_40_49_f = checkData(latest.getString("confirmados_40_49_f"));
        String confirmados_50_59_f = checkData(latest.getString("confirmados_50_59_f"));
        String confirmados_60_69_f = checkData(latest.getString("confirmados_60_69_f"));
        String confirmados_70_79_f = checkData(latest.getString("confirmados_70_79_f"));
        String confirmados_80_plus_f = checkData(latest.getString("confirmados_80_plus_f"));

        String confirmados_0_9_m = checkData(latest.getString("confirmados_0_9_m"));
        String confirmados_10_19_m = checkData(latest.getString("confirmados_10_19_m"));
        String confirmados_20_29_m = checkData(latest.getString("confirmados_20_29_m"));
        String confirmados_30_39_m = checkData(latest.getString("confirmados_30_39_m"));
        String confirmados_40_49_m = checkData(latest.getString("confirmados_40_49_m"));
        String confirmados_50_59_m = checkData(latest.getString("confirmados_50_59_m"));
        String confirmados_60_69_m = checkData(latest.getString("confirmados_60_69_m"));
        String confirmados_70_79_m = checkData(latest.getString("confirmados_70_79_m"));
        String confirmados_80_plus_m = checkData(latest.getString("confirmados_80_plus_m"));

        //obitos
        String obitos_f = checkData(latest.getString("obitos_f"));
        String obitos_m = checkData(latest.getString("obitos_m"));

        //obitos ARS
        String obitos_arsnorte = checkData(latest.getString("obitos_arsnorte"));
        String obitos_arscentro = checkData(latest.getString("obitos_arscentro"));
        String obitos_arslvt = checkData(latest.getString("obitos_arslvt"));
        String obitos_arsalentejo = checkData(latest.getString("obitos_arsalentejo"));
        String obitos_arsalgarve = checkData(latest.getString("obitos_arsalgarve"));
        String obitos_acores = checkData(latest.getString("obitos_acores"));
        String obitos_madeira = checkData(latest.getString("obitos_madeira"));

        //obitos sexo e idade
        String obitos_0_9_f = checkData(latest.getString("obitos_0_9_f"));
        String obitos_10_19_f = checkData(latest.getString("obitos_10_19_f"));
        String obitos_20_29_f = checkData(latest.getString("obitos_20_29_f"));
        String obitos_30_39_f = checkData(latest.getString("obitos_30_39_f"));
        String obitos_40_49_f = checkData(latest.getString("obitos_40_49_f"));
        String obitos_50_59_f = checkData(latest.getString("obitos_50_59_f"));
        String obitos_60_69_f = checkData(latest.getString("obitos_60_69_f"));
        String obitos_70_79_f = checkData(latest.getString("obitos_70_79_f"));
        String obitos_80_plus_f = checkData(latest.getString("obitos_80_plus_f"));

        String obitos_0_9_m = checkData(latest.getString("obitos_0_9_m"));
        String obitos_10_19_m = checkData(latest.getString("obitos_10_19_m"));
        String obitos_20_29_m = checkData(latest.getString("obitos_20_29_m"));
        String obitos_30_39_m = checkData(latest.getString("obitos_30_39_m"));
        String obitos_40_49_m = checkData(latest.getString("obitos_40_49_m"));
        String obitos_50_59_m = checkData(latest.getString("obitos_50_59_m"));
        String obitos_60_69_m = checkData(latest.getString("obitos_60_69_m"));
        String obitos_70_79_m = checkData(latest.getString("obitos_70_79_m"));
        String obitos_80_plus_m = checkData(latest.getString("obitos_80_plus_m"));

        return c.getString(R.string.confirmed_north) + confirmados_arsnorte + "\n" +
                c.getString(R.string.confirmed_center) + confirmados_arscentro + "\n" +
                c.getString(R.string.confirmed_lisbonvaledotejo) + confirmados_arslvt + "\n" +
                c.getString(R.string.confirmed_alentejo) + confirmados_arsalentejo + "\n" +
                c.getString(R.string.confirmed_algarve) + confirmados_arsalgarve + "\n" +
                c.getString(R.string.confirmed_açores) + confirmados_acores + "\n" +
                c.getString(R.string.confirmed_madeira) + confirmados_madeira + "\n\n" +
                c.getString(R.string.confirmados_f) + confirmados_mulher + "\n\n" +
                c.getString(R.string.confirmed_f_09) + confirmados_0_9_f + "\n" +
                c.getString(R.string.confirmed_f_1019) + confirmados_10_19_f + "\n" +
                c.getString(R.string.confirmed_f_2029) + confirmados_20_29_f + "\n" +
                c.getString(R.string.confirmed_f_3039) + confirmados_30_39_f + "\n" +
                c.getString(R.string.confirmed_f_4049) + confirmados_40_49_f + "\n" +
                c.getString(R.string.confirmed_f_4049) + confirmados_50_59_f + "\n" +
                c.getString(R.string.confirmed_f_5059) + confirmados_60_69_f + "\n" +
                c.getString(R.string.confirmed_f_6069) + confirmados_70_79_f + "\n" +
                c.getString(R.string.confirmed_f_80plus) + confirmados_80_plus_f + "\n" + "\n" +
                c.getString(R.string.confirmados_m) + confirmados_homem + "\n\n" +
                c.getString(R.string.confirmed_m_09) + confirmados_0_9_m + "\n" +
                c.getString(R.string.confirmed_m_1019) + confirmados_10_19_m + "\n" +
                c.getString(R.string.confirmed_m_2029) + confirmados_20_29_m + "\n" +
                c.getString(R.string.confirmed_m_3039) + confirmados_30_39_m + "\n" +
                c.getString(R.string.confirmed_m_4049) + confirmados_40_49_m + "\n" +
                c.getString(R.string.confirmed_m_5059) + confirmados_50_59_m + "\n" +
                c.getString(R.string.confirmed_m_6069) + confirmados_60_69_m + "\n" +
                c.getString(R.string.confirmed_m_7079) + confirmados_70_79_m + "\n" +
                c.getString(R.string.confirmed_m_80plus) + confirmados_80_plus_m + "\n" + "\n" +
                c.getString(R.string.deaths_north) + obitos_arsnorte + "\n" +
                c.getString(R.string.deaths_center) + obitos_arscentro + "\n" +
                c.getString(R.string.deaths_lisbonvaledotejo) + obitos_arslvt + "\n" +
                c.getString(R.string.deaths_alentejo) + obitos_arsalentejo + "\n" +
                c.getString(R.string.deaths_algarve) + obitos_arsalgarve + "\n" +
                c.getString(R.string.deaths_açores) + obitos_acores + "\n" +
                c.getString(R.string.deaths_madeira) + obitos_madeira + "\n" + "\n" +
                c.getString(R.string.deaths_f) + obitos_f + "\n\n" +
                c.getString(R.string.deaths_f_09) + obitos_0_9_f + "\n" +
                c.getString(R.string.deaths_f_1019) + obitos_10_19_f + "\n" +
                c.getString(R.string.deaths_f_2029) + obitos_20_29_f + "\n" +
                c.getString(R.string.deaths_f_3039) + obitos_30_39_f + "\n" +
                c.getString(R.string.deaths_f_4049) + obitos_40_49_f + "\n" +
                c.getString(R.string.deaths_f_5059) + obitos_50_59_f + "\n" +
                c.getString(R.string.deaths_f_6069) + obitos_60_69_f + "\n" +
                c.getString(R.string.deaths_f_7079) + obitos_70_79_f + "\n" +
                c.getString(R.string.deaths_f_80plus) + obitos_80_plus_f + "\n\n" +
                c.getString(R.string.deaths_m) + obitos_m + "\n\n" +
                c.getString(R.string.deaths_m_09) + obitos_0_9_m + "\n" +
                c.getString(R.string.deaths_m_1019) + obitos_10_19_m + "\n" +
                c.getString(R.string.deaths_m_2029) + obitos_20_29_m + "\n" +
                c.getString(R.string.deaths_m_3039) + obitos_30_39_m + "\n" +
                c.getString(R.string.deaths_m_4049) + obitos_40_49_m + "\n" +
                c.getString(R.string.deaths_m_5059) + obitos_50_59_m + "\n" +
                c.getString(R.string.deaths_m_6069) + obitos_60_69_m + "\n" +
                c.getString(R.string.deaths_m_7079) + obitos_70_79_m + "\n" +
                c.getString(R.string.deaths_m_80plus) + obitos_80_plus_m;
    }

    public String stringFormater(int i) {
        if (i >= 0) {
            return "+" + i;
        }
        return String.valueOf(i);
    }

    public String stringFormater(Double i) {
        if (i >= 0D) {
            return "+" + i;
        }
        return String.valueOf(i);
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