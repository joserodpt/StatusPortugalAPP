package pt.josegamerpt.statusportugal.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import pt.josegamerpt.statusportugal.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_recomendations#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_recomendations extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_recomendations() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_recomendations.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_recomendations newInstance(String param1, String param2) {
        fragment_recomendations fragment = new fragment_recomendations();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendations, container, false);

        ((TextView) v.findViewById(R.id.infoText1)).setText(HtmlCompat.fromHtml(getContext().getString(R.string.recomend_string_1), HtmlCompat.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        ((TextView) v.findViewById(R.id.infoText4)).setText(HtmlCompat.fromHtml(getContext().getString(R.string.recomend_string_4), HtmlCompat.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        ((TextView) v.findViewById(R.id.infoText2)).setText(HtmlCompat.fromHtml(getContext().getString(R.string.recomend_string_2), HtmlCompat.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE);
        ((TextView) v.findViewById(R.id.infoText3)).setText(HtmlCompat.fromHtml(getContext().getString(R.string.recomend_string_3), HtmlCompat.FROM_HTML_MODE_LEGACY), TextView.BufferType.NORMAL);

        v.findViewById(R.id.webText).setOnClickListener(v1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pandemiaclara.sapo.pt/"));
            startActivity(browserIntent);
        });

        v.findViewById(R.id.callbutton).setOnClickListener(v12 -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+351808242424"));
            startActivity(intent);
        });

        //showwithanimation
        v.findViewById(R.id.dicamain).setVisibility(View.VISIBLE);
        v.findViewById(R.id.webText).setVisibility(View.VISIBLE);

        YoYo.with(Techniques.SlideInUp)
                .duration(700)
                .playOn(v.findViewById(R.id.dicamain));
        YoYo.with(Techniques.FadeInUp)
                .duration(700)
                .playOn(v.findViewById(R.id.webText));

        return v;
    }

}