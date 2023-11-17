package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growit.posapp.fstore.R;


public class AddProductListFragment extends Fragment {
    public AddProductListFragment() {
        // Required empty public constructor
    }

    public static AddProductListFragment newInstance() {
        return new AddProductListFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product_list, container, false);
    }
}