package com.growit.posapp.fstore.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacyFragment extends Fragment {

    ImageView backBtn;
    TextView headerTxt;
    public static PrivacyFragment newInstance() {
        PrivacyFragment fragment = new PrivacyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        headerTxt = view.findViewById(R.id.titleTxt);
        backBtn =view.findViewById(R.id.backBtn);
        WebView myWebView = view.findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        headerTxt.setText("Privacy Policy");
        myWebView.loadUrl("https://thegrowit.com/privacy-policy");
        backBtn.setVisibility(View.GONE);
        return view;
    }
}