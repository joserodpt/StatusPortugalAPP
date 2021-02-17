package pt.josegamerpt.statusportugal.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import needle.Needle;
import needle.UiRelatedTask;
import pt.josegamerpt.statusportugal.AppUtils;
import pt.josegamerpt.statusportugal.R;

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
        v = inflater.inflate(R.layout.fragment_statistics, container, false);
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

        //var
        TextView varheader2 = v.findViewById(R.id.variationdataheader2);
        TextView varinfo = v.findViewById(R.id.variationdatainfo);

        v.findViewById(R.id.variationdata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = c.getString(R.string.variation_name) + " " + varheader2.getText() + "\n\n" + varinfo.getText();
            ClipData clip = ClipData.newPlainText("varData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), c.getString(R.string.copied_to_clipboard).replace("%name%", c.getString(R.string.variation_name)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });

        //yesterday
        TextView yesterdayheader2 = v.findViewById(R.id.yesterdayHeader2);
        TextView yesinfo = v.findViewById(R.id.yestedayInfo);

        v.findViewById(R.id.yesterdaydata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = c.getString(R.string.previous_day_name) + " " + yesterdayheader2.getText() + "\n\n" + yesinfo.getText();
            ClipData clip = ClipData.newPlainText("yesterdayData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), c.getString(R.string.copied_to_clipboard).replace("%name%", c.getString(R.string.previous_day_name)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });

        //more
        TextView morehe2 = v.findViewById(R.id.moredataheader2);
        TextView morinfo = v.findViewById(R.id.totaldatainfo);

        v.findViewById(R.id.totaldata).setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String copy = c.getString(R.string.more_info_name) + " " + morehe2.getText() + "\n\n" + morinfo.getText();
            ClipData clip = ClipData.newPlainText("moreInfoData", copy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), c.getString(R.string.copied_to_clipboard).replace("%name%", c.getString(R.string.more_info_name)),
                    Toast.LENGTH_SHORT).show();
            return false;
        });


        return v;
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
                        getLatest();
                        getSecond();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                    return null;
                }

                @Override
                protected void thenDoUiRelatedWork(Object o) {
                    //first
                    try {
                        if (!latest.has("status")) {
                            TextView t = v.findViewById(R.id.latestheader2);
                            t.setText(latest.getString("data_dados"));
                            TextView tlatest = v.findViewById(R.id.latestinfo);
                            tlatest.setText(formatLatest());
                        }

                        //Second
                        if (!yesterday.has("status")) {
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
                        }

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
        v.findViewById(R.id.main).setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInUp)
                .duration(700)
                .playOn(v.findViewById(R.id.main));
    }

    public void getLatest() throws JSONException {
        latest = new JSONObject(AppUtils.getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_last_update"));
    }

    public void getSecond() throws JSONException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        Date d = sdf.parse(latest.getString("data"));
        Calendar yst = Calendar.getInstance();
        yst.setTime(d);
        yst.add(Calendar.DATE, -1);

        Date date = yst.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        String stringYesterday = format1.format(date);

        yesterday = new JSONObject(AppUtils.getInfoFromAPI("https://covid19-api.vost.pt/Requests/get_entry/" + stringYesterday));
    }

    private String formatLatest() throws JSONException {
        int ativos = checkDataInt(checkData(latest.getString("ativos")));
        int recuperados = checkDataInt(checkData(latest.getString("recuperados")));
        int obitos = checkDataInt(checkData(latest.getString("obitos")));
        int vigilancia = checkDataInt(checkData(latest.getString("vigilancia")));
        int novos_infetados = checkDataInt(checkData(latest.getString("confirmados_novos")));
        int total_infetados = checkDataInt(checkData(latest.getString("confirmados")));
        //internados
        int internados = checkDataInt(checkData(latest.getString("internados")));
        int internados_uci = checkDataInt(checkData(latest.getString("internados_uci")));
        int internados_enfermaria = checkDataInt(checkData(latest.getString("internados_enfermaria")));

        return c.getString(R.string.confirmed_name) + stringFormater(novos_infetados) + "\n\n"
                + c.getString(R.string.confirmed_name) + total_infetados + "\n"
                + c.getString(R.string.recovered_cases) + recuperados + "\n"
                + c.getString(R.string.surveilance_name) + vigilancia + "\n"
                + c.getString(R.string.active_cases_name) + ativos + "\n\n"
                + c.getString(R.string.interned_uci) + internados_uci + "\n"
                + c.getString(R.string.interned_enfermaria) + internados_enfermaria + "\n"
                + c.getString(R.string.internados_total_name) + internados + "\n\n"
                + c.getString(R.string.deaths_name) + obitos;
    }

    private String formatVariation(JSONObject inf, JSONObject yesterday, String key) throws JSONException {
        int latest_ativos = checkDataInt(checkData(inf.getString("ativos")));
        int latest_recuperados = checkDataInt(checkData(inf.getString("recuperados")));
        int latest_obitos = checkDataInt(checkData(inf.getString("obitos")));
        int latest_vigilancia = checkDataInt(checkData(inf.getString("vigilancia")));
        int latest_infetados = checkDataInt(checkData(inf.getString("confirmados")));
        int latest_total_internados = checkDataInt(checkData(inf.getString("internados")));
        int latest_total_internados_enfermaria = checkDataInt(checkData(inf.getString("internados_enfermaria")));
        int latest_total_internados_uci = checkDataInt(checkData(inf.getString("internados_uci")));

        int latest_new_cases = checkDataInt(checkData(inf.getString("confirmados_novos")));

        int yesterday_ativos = checkDataInt(checkData(yesterday.getJSONObject("ativos").getString(key)));
        int yesterday_recuperados = checkDataInt(checkData(yesterday.getJSONObject("recuperados").getString(key)));
        int yesterday_obitos = checkDataInt(checkData(yesterday.getJSONObject("obitos").getString(key)));
        int yesterday_vigilancia = checkDataInt(checkData(yesterday.getJSONObject("vigilancia").getString(key)));
        int yesterday_infetados = checkDataInt(checkData(yesterday.getJSONObject("confirmados").getString(key)));
        int yesterday_total_internados = checkDataInt(checkData(yesterday.getJSONObject("internados").getString(key)));
        int yesterday_total_internados_enfermaria = checkDataInt(checkData(yesterday.getJSONObject("internados_enfermaria").getString(key)));
        int yesterday_total_internados_uci = checkDataInt(checkData(yesterday.getJSONObject("internados_uci").getString(key)));

        int yesterday_new_cases = checkDataInt(checkData(yesterday.getJSONObject("confirmados_novos").getString(key)));

        int varAtivos = latest_ativos - yesterday_ativos;
        int varRecuperados = latest_recuperados - yesterday_recuperados;
        int varObitos = latest_obitos - yesterday_obitos;
        int varVig = latest_vigilancia - yesterday_vigilancia;
        int varNovosInf = latest_infetados - yesterday_infetados;
        int inter = latest_total_internados - yesterday_total_internados;
        int interenf = latest_total_internados_enfermaria - yesterday_total_internados_enfermaria;
        int interuci = latest_total_internados_uci - yesterday_total_internados_uci;

        int newcasesvar = latest_new_cases - yesterday_new_cases;

        return c.getString(R.string.confirmed_name) + stringFormater(varNovosInf) + "\n"
                + "(" + stringFormater(newcasesvar) + " " + c.getString(R.string.variation_new_cases) + "\n\n"
                + c.getString(R.string.interned_uci) + stringFormater(interuci) + "\n"
                + c.getString(R.string.interned_enfermaria) + stringFormater(interenf) + "\n"
                + c.getString(R.string.internados_new_name) + stringFormater(inter) + "\n\n"
                + c.getString(R.string.recovered_cases) + stringFormater(varRecuperados) + "\n"
                + c.getString(R.string.surveilance_name) + stringFormater(varVig) + "\n"
                + c.getString(R.string.active_cases_name) + stringFormater(varAtivos) + "\n\n"
                + c.getString(R.string.deaths_name) + stringFormater(varObitos);

    }

    private String formatYesterday(JSONObject inf, String key) throws JSONException {
        String ativos = checkData(inf.getJSONObject("ativos").getString(key));
        String recuperados = checkData(inf.getJSONObject("recuperados").getString(key));
        String obitos = checkData(inf.getJSONObject("obitos").getString(key));
        String vigilancia = checkData(inf.getJSONObject("vigilancia").getString(key));
        String novos_infetados = checkData(inf.getJSONObject("confirmados_novos").getString(key));
        String total_infetados = checkData(inf.getJSONObject("confirmados").getString(key));

        //internados
        String internados = checkData(inf.getJSONObject("internados").getString(key));
        String internados_uci = checkData(inf.getJSONObject("internados_uci").getString(key));
        String internados_enfermaria = checkData(inf.getJSONObject("internados_enfermaria").getString(key));

        return c.getString(R.string.confirmed_name) + stringFormater(novos_infetados) + "\n\n"
                + c.getString(R.string.confirmed_name) + total_infetados + "\n"
                + c.getString(R.string.recovered_cases) + recuperados + "\n"
                + c.getString(R.string.surveilance_name) + vigilancia + "\n"
                + c.getString(R.string.active_cases_name) + ativos + "\n\n"
                + c.getString(R.string.interned_uci) + internados_uci + "\n"
                + c.getString(R.string.interned_enfermaria) + internados_enfermaria + "\n"
                + c.getString(R.string.internados_total_name) + internados + "\n\n"
                + c.getString(R.string.deaths_name) + obitos;
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

    public String numFormatter(int i) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(i);
    }

    public String stringFormater(String i) {
        return i;
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