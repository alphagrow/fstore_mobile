package com.growit.posapp.gstore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.databinding.CustomerRecyclerviewBinding;
import com.growit.posapp.gstore.ui.fragments.AddCustomerFragment;
import com.growit.posapp.gstore.ui.fragments.CustomerRecyclerViewFragment;

public class AddCustomerActivity extends AppCompatActivity {
// Testing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_customer);
        Intent intent = getIntent();
        int value = intent.getIntExtra("Screen", 0);
        if (value == 1) {
            callAddCustomerFragment();
        } else if (value == 2) {
            callGetCustomerListFragment();
        }

    }

    private void callAddCustomerFragment() {
        AddCustomerFragment fragment = AddCustomerFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.customerFragment, fragment).commit();
    }

    private void callGetCustomerListFragment() {
        CustomerRecyclerViewFragment fragment = CustomerRecyclerViewFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.customerFragment, fragment).commit();
    }
}