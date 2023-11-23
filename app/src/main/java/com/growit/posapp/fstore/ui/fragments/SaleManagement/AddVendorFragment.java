package com.growit.posapp.fstore.ui.fragments.SaleManagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growit.posapp.fstore.R;


public class AddVendorFragment extends Fragment {



    public AddVendorFragment() {
        // Required empty public constructor
    }
    public static AddVendorFragment newInstance() {
        return new AddVendorFragment();
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
        return inflater.inflate(R.layout.fragment_add_vendor, container, false);
    }
}