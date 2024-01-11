package com.growit.posapp.fstore.ui.fragments.SaleManagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.databinding.FragmentUpdateWareHouseBinding;
import com.growit.posapp.fstore.model.WarehouseModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateWareHouseFragment extends Fragment {
    List<WarehouseModel> warehouse_model=null;
    private int position = 0;
    String type_of_vendor_warehouse;
   FragmentUpdateWareHouseBinding binding;
   String str_company_name="",str_code="";
    public UpdateWareHouseFragment() {
        // Required empty public constructor
    }


    public static UpdateWareHouseFragment newInstance() {
        return new UpdateWareHouseFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_ware_house, container, false);
   init();
        return binding.getRoot();
    }
    private void init() {
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            warehouse_model = (List<WarehouseModel>) getArguments().getSerializable("warehouse_list");
            binding.etCompanyName.setText(warehouse_model.get(position).getName());
            binding.etCode.setText(warehouse_model.get(position).getCode());
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(), MainActivity.class));
               getActivity().finish();
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_company_name = binding.etCompanyName.getText().toString();
                str_code = binding.etCode.getText().toString();

                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                updateWareHouse();


            }
        });

    }

    private void updateWareHouse() {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID()+"");
        params.put("token", sm.getJWTToken());
        params.put("name", str_company_name);
        params.put("code", str_code);
//        params.put("street", streetStr);
//        params.put("city", str_city);
//        params.put("state_id", stateStr);
//        params.put("country_id", ApiConstants.COUNTRY_ID);
//        params.put("zip", zipStr);
//        params.put("district_id", districtStr);
//        params.put("taluka_id", talukaStr);
        params.put("warehouse_id", warehouse_model.get(position).getId()+"");
//        params.put("company_id", "1");
        Utility.showDialoge("", getActivity());
        Log.v("create_ware_house", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.UPDATE_WAREHOUSE, new VolleyCallback() {
            private String message = "Update failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String error_message = obj.optString("error_message");
                String  str_message = obj.optString("message");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), "Ware House  Update successfully ", Toast.LENGTH_SHORT).show();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("type_of_vendor_warehouse", "warehouse");
//                    Fragment fragment = VendorListAndWareHouseListFragment.newInstance();
//                    fragment.setArguments(bundle);
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Log.v("Response", result);
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });
    }


}