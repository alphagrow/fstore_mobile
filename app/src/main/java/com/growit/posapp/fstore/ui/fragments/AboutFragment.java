package com.growit.posapp.fstore.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

ImageView backBtn;
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        WebView myWebView = view.findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        String html = "<br /><br />About Our Online Store\n\n" +
                "\n" +
                "Welcome To GROWiT\n" +
                "GROWiT India, is the agricultural arm of Alpha Plastomers Private Limited and was born with the intention to manufacture advanced and innovative products that ensure optimum quality and yield for the Indian Agricultural & Farming Industry while lowering its carbon footprint.\n" +
                "\n" +
                "Our goal is to be your go-to partners for major protective farming products, such as plastic mulch film, shade nets, crop/fruit covers, vermi beds, mulch laying/hole punching machines and agri wires, that will ensure sustainable, efficient and cost-effective agricultural practices.\n" +
                "\n" +
                "Critical solutions like quality food, climate smart agriculture, farmer empowerment and enhancing farmer productivity are major focus areas for GROWiT. With GROWiT as your end-to-end solution provider, we will ensure that you have maximum productivity, highest quality, and minimum wastage.<br /><br />";

        myWebView.loadData(html,mimeType,encoding);
        return view;
    }
}