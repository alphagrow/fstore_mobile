package com.growit.posapp.fstore.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growit.posapp.fstore.R;


public class AddPOSCategoryFragment extends Fragment {



    public AddPOSCategoryFragment() {
        // Required empty public constructor
    }
    public static AddPOSCategoryFragment newInstance() {
        return new AddPOSCategoryFragment();
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
        return inflater.inflate(R.layout.fragment_p_o_s_category, container, false);
    }
}