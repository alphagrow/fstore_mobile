package com.growit.posapp.fstore.ui.fragments.SaleManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.AddProductListAdapter;
import com.growit.posapp.fstore.adapters.POSAdapter;
import com.growit.posapp.fstore.adapters.VendorListAdapter;
import com.growit.posapp.fstore.databinding.FragmentPOSCategoryListBinding;
import com.growit.posapp.fstore.databinding.FragmentVendorBinding;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.model.Transaction;
import com.growit.posapp.fstore.model.VendorModel;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.ui.fragments.POSCategory.AddPOSCategoryFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.POSCategoryListFragment;
import com.growit.posapp.fstore.ui.fragments.ProductListFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class VendorListFragment extends Fragment {
    FragmentVendorBinding binding;
    VendorModel vendormodel;
    VendorListAdapter adapter;
    String status="true";
    public VendorListFragment() {
        // Required empty public constructor
    }
    public static VendorListFragment newInstance() {
        return new VendorListFragment();
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
        binding= FragmentVendorBinding.inflate(inflater, container, false);
        init();

        return binding.getRoot();
    }

    private void init(){
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVendor.setLayoutManager(layoutManager);
        if (Utility.isNetworkAvailable(getContext())) {
            getVendorList();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    getVendorList();
                } else {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                }

            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
        binding.addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AddVendorFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });
        binding.seacrEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        binding.rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkedId)
//            {
//                // This will get the radiobutton that has changed in its check state
//                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
//                // This puts the value (true/false) into the variable
//                boolean isChecked = checkedRadioButton.isChecked();
//                // If the radiobutton that has changed in check state is now checked...
//                if (checkedRadioButton.getText().toString().equalsIgnoreCase("Active Vendor")) {
//                    status="true";
//                }else if (checkedRadioButton.getText().toString().equalsIgnoreCase("Inactive Vendor")) {
//                    status="false";
//                }
//
//                if (Utility.isNetworkAvailable(getActivity())) {
//                    getVendorList();
//                } else {
//                    Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


    }
    private void getVendorList(){
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

        // String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
        Utility.showDialoge("Please wait while a moment...", getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<VendorModel>() {
                        }.getType();

                        vendormodel = gson.fromJson(response.toString(), listType);
                        if (vendormodel.getVendors() == null || vendormodel.getVendors().size() == 0) {
                            binding.noItem.setVisibility(View.VISIBLE);
                            binding.recyclerVendor.setVisibility(View.GONE);
                        } else {
                            binding.noItem.setVisibility(View.GONE);
                            binding.recyclerVendor.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            adapter = new VendorListAdapter(getActivity(), vendormodel.getVendors());
                            binding.recyclerVendor.setAdapter(adapter);
                            binding.recyclerVendor.setLayoutManager(layoutManager);

                        }
                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            binding.noItem.setVisibility(View.VISIBLE);
            binding.recyclerVendor.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }
    private void filterList(String text){
        ArrayList<VendorModelList> model = new ArrayList<>();

        for (VendorModelList detail : vendormodel.getVendors()){
            if (detail.getName().toLowerCase().contains(text.toLowerCase()) || detail.getMobile().toLowerCase().contains(text.toLowerCase())){
                model.add(detail);
            }
        }

        adapter.updateList(model);
    }


}