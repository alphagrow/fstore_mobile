package com.growit.posapp.fstore.ui.fragments.Inventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.databinding.FragmentCreateWarehouseBinding;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.VendorListFragment;


public class CreateWarehouseFragment extends Fragment {

FragmentCreateWarehouseBinding binding;

    public CreateWarehouseFragment() {
        // Required empty public constructor
    }

    public static CreateWarehouseFragment newInstance() {
        return new CreateWarehouseFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_warehouse, container, false);
       init();
        return binding.getRoot();
    }

    private void init(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }
}