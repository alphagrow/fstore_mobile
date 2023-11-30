package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.databinding.FragmentCreateAttributeBinding;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CreateAttributeFragment extends Fragment {

  FragmentCreateAttributeBinding binding;
    boolean isAllFieldsChecked = false;
    public CreateAttributeFragment() {
        // Required empty public constructor
    }

    public static CreateAttributeFragment newInstance() {
        return new CreateAttributeFragment();
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
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_create_attribute, container, false);

        init();
        return binding.getRoot();
    }
    private void init(){
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked= CheckAllFields();
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }

                String str_attr_value = binding.etAttributeValue.getText().toString();
                String str_attr_name = binding.etAttributeName.getText().toString();
                if (isAllFieldsChecked) {
                    addAttribute(str_attr_name, str_attr_value);
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

    }
    private void addAttribute(String str_attr_name,String str_attr_value){
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID()+"");
        params.put("token", sm.getJWTToken());
        params.put("attribute_name", str_attr_name);
        params.put("attribute_values_string", str_attr_value);
        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CREATE_ATTRIBUTE_VALUE, new VolleyCallback() {
            private String message = " failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                String message = obj.optString("message");

                if (statusCode==200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    binding.etAttributeValue.setText("");
                    binding.etAttributeName.setText("");

                }
            }

            @Override
            public void onError(String result) throws Exception {
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private boolean CheckAllFields() {
        if (binding.etAttributeName.length()== 0) {
            binding.etAttributeName.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Attribute Name", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (binding.etAttributeValue.length() == 0) {
            binding.etAttributeValue.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Attribute Value Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}