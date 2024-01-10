package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growit.posapp.fstore.R;


public class UOMFragment extends Fragment {
    public UOMFragment() {
        // Required empty public constructor
    }


    public static UOMFragment newInstance(String param1, String param2) {
        UOMFragment fragment = new UOMFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u_o_m, container, false);
    }
}