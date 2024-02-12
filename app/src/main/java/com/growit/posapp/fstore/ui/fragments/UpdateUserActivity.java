package com.growit.posapp.fstore.ui.fragments;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.WareHouseAdapter;
import com.growit.posapp.fstore.databinding.ActivityUpdateUserBinding;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.model.VendorModel;
import com.growit.posapp.fstore.model.WarehouseModel;
import com.growit.posapp.fstore.ui.UserRegistrationActivity;
import com.growit.posapp.fstore.ui.fragments.AddProduct.AddProductListFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateUserActivity extends AppCompatActivity {
    ActivityUpdateUserBinding binding;
    VendorModel vendormodel;
    String str_comp_id,str_shop_id;
//    List<ConfigurationModel> list;
    List<ConfigurationModel> list = new ArrayList<>();
    String str_email="", str_name="", str_user_id="",str_password="";
    List<StateModel> company_list = new ArrayList<>();
    List<StateModel> ware_houseNames = new ArrayList<>();
    String ware_house_id = "";
    String default_company_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      setContentView(R.layout.activity_update_user);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        binding = DataBindingUtil.setContentView(UpdateUserActivity.this, R.layout.activity_update_user);

        init();
    }
    private void init(){
        Intent getIntent = getIntent();
        String profile_detail=getIntent.getStringExtra("profile_detail");
        JSONObject obj = null;
        try {
            obj = new JSONObject(profile_detail.toString());
            int statusCode = obj.optInt("statuscode");
            String status = obj.optString("status");

            if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                JSONObject jsonArray = obj.getJSONObject("user_profile");

                if (jsonArray.length() > 0) {
                    String name = jsonArray.optString("name");
                    String email = jsonArray.optString("email");
                    String login = jsonArray.optString("login");
                     ware_house_id = String.valueOf(jsonArray.optInt("Default Warehouse Id"));
                    JSONArray companyAccess = jsonArray.getJSONArray("Company Access");
                    JSONArray pos_access = jsonArray.getJSONArray("Pos Access");

//                    for (int i = 0; i < companyAccess.length(); i++) {
//                        StateModel   stateModel = new StateModel();
//                        JSONObject data = companyAccess.getJSONObject(i);
//                        int id = data.optInt("id");
//                        String company_name = data.optString("Company Name");
//                        stateModel.setId(id);
//                        stateModel.setName(company_name);
//                        company_list.add(stateModel);
//                    }

                    StringBuilder stringBuilder_com_name = new StringBuilder();
                    StringBuilder builder_comp_id = new StringBuilder();

                    for (int i = 0; i < companyAccess.length(); i++) {
                        StateModel   stateModel = new StateModel();
                        JSONObject data = companyAccess.getJSONObject(i);
                        int id = data.optInt("id");
                        String name_comp = data.optString("Company Name");
                        stateModel.setId(id);
                        stateModel.setName(name_comp);
                        company_list.add(stateModel);
                        builder_comp_id.append(company_list.get(i).getId());
                        stringBuilder_com_name.append(company_list.get(i).getName());
                        if (i != companyAccess.length() - 1) {
                            stringBuilder_com_name.append(", ");
                            builder_comp_id.append(",");
                        }

                    }
                    str_comp_id = builder_comp_id.toString();
                    Log.d("str_comp_id", str_comp_id);
                    binding.textView.setText(stringBuilder_com_name.toString());





                    StringBuilder stringBuilder_pos_name = new StringBuilder();
                    StringBuilder builder_pos_id = new StringBuilder();

                    for (int i = 0; i < pos_access.length(); i++) {
                        ConfigurationModel   stateModel = new ConfigurationModel();
                        JSONObject data = pos_access.getJSONObject(i);
                        int id = data.optInt("id");
                        String name_comp = data.optString("Allowed Pos");
                        stateModel.setId(id);
                        stateModel.setName(name_comp);
                        list.add(stateModel);
                        builder_pos_id.append(list.get(i).getId());
                        stringBuilder_pos_name.append(list.get(i).getName());
                        if (i != pos_access.length() - 1) {
                            stringBuilder_pos_name.append(", ");
                            builder_pos_id.append(",");
                        }

                    }
                    str_shop_id = builder_pos_id.toString();
                    Log.d("str_comp_id", str_comp_id);
                    binding.textViewShop.setText(stringBuilder_pos_name.toString());


                    default_company_id = String.valueOf(jsonArray.optInt("default_company"));
                    binding.etUseremail.setText(email);
                    binding.etUsername.setText(name);
                    binding.edUserId.setText(login);




                }
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if(Utility.isNetworkAvailable(UpdateUserActivity.this)) {
            getWareHouseList();
            getShopList();
            getCompanyList();
        }else {
            Toast.makeText(UpdateUserActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        binding.warehousesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ware_house_id = ware_houseNames.get(position).getId() + "";
                    //  binding.customerTxt.setText(vendorNames.get(position).getName());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.defCompSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    default_company_id = company_list.get(position).getId() + "";
                    //  binding.customerTxt.setText(vendorNames.get(position).getName());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_email = binding.etUseremail.getText().toString();
                str_name = binding.etUsername.getText().toString();
                 str_user_id = binding.edUserId.getText().toString();


                if (str_comp_id != null) {
                    if (str_shop_id != null) {
                        UserUpdate(str_comp_id,str_shop_id);
                    }else {
                        Toast.makeText(UpdateUserActivity.this, "Select shop", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(UpdateUserActivity.this, "Select company", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getWareHouseList(){
        SessionManagement sm = new SessionManagement(UpdateUserActivity.this);
        RequestQueue queue = Volley.newRequestQueue(UpdateUserActivity.this);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_WareHouses + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

        //   String url = ApiConstants.BASE_URL + ApiConstants.GET_WareHouses;
        Log.v("url", url);
        Utility.showDialoge("Please wait while a moment...", UpdateUserActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    String error_message = obj.optString("error_message");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("warehouses");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("Select warehouse");
                        ware_houseNames.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            String name = data.optString("name");
//                            if (name.equalsIgnoreCase("Receipts")) {
                                int id = data.optInt("id");
                                String warehouse_name = data.optString("name");
                                stateModel.setId(id);
                                stateModel.setName(warehouse_name);
                                ware_houseNames.add(stateModel);
                          //  }
                        }
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UpdateUserActivity.this, ware_houseNames);
                            binding.warehousesSpinner.setAdapter(adapter);
                        }
                    }else {
                        Toast.makeText(UpdateUserActivity.this, error_message, Toast.LENGTH_SHORT).show();

                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {

        });
        queue.add(jsonObjectRequest);
    }

    private void getShopList() {
        SessionManagement sm = new SessionManagement(UpdateUserActivity.this);
        RequestQueue queue = Volley.newRequestQueue(UpdateUserActivity.this);
        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_CUSTOMER_DISCOUNT_LIST;

        String url = ApiConstants.BASE_URL + ApiConstants.GET_LIST_SHOPS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        //    Utility.showDialoge("Please wait while a moment...", getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (status.equalsIgnoreCase("success")) {
                        // Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("shops");
                        list.clear();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ConfigurationModel model = new ConfigurationModel();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer id = data.optInt("id");
                                String name = data.optString("name");
                                model.setId(id);
                                model.setName(name);
                                list.add(model);
                            }
                            if (list == null || list.size() == 0) {

                            } else {
                                setShopList(list);
//                                binding.totalCustomerText.setText("Total : "+list.size()+" Shops ");
//                                binding.noDataFound.setVisibility(View.GONE);
//                                adapter = new ConfigurationAdapter(getActivity(), list);
//                                binding.recyclerVendor.setAdapter(adapter);

                            }

                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(UpdateUserActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    private void setCompanyList(List<StateModel> warehouses) {
        ArrayList<String> comp_name = new ArrayList<>();
        ArrayList<String> comp_id = new ArrayList<>();
        ArrayList<Integer> langList = new ArrayList<>();
        for (int i = 0; i < warehouses.size(); i++) {
            comp_name.add(warehouses.get(i).getName());
            comp_id.add(String.valueOf(warehouses.get(i).getId()));
        }
        final CharSequence[] items = comp_name.toArray(new CharSequence[comp_name.size()]);
        boolean[] selected_company = new boolean[comp_name.size()];


        binding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserActivity.this);
                builder.setTitle("Select Company Name");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(items, selected_company, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {

                            langList.add(i);
                            Collections.sort(langList);
                        } else {

                            langList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        StringBuilder builder_ware_id = new StringBuilder();
                        for (int j = 0; j < langList.size(); j++) {
                            stringBuilder.append(items[langList.get(j)]);
                            builder_ware_id.append(comp_id.get(langList.get(j)));
                            if (j != langList.size() - 1) {
                                stringBuilder.append(", ");
                                builder_ware_id.append(",");
                            }
                        }

                        str_comp_id = builder_ware_id.toString();
                        Log.d("str_warehouse_id", str_comp_id);
                        binding.textView.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// use for loop
                        for (int j = 0; j < selected_company.length; j++) {
                            selected_company[j] = false;
                            langList.clear();
                            binding.textView.setText("");
                        }
                    }
                });

                builder.show();
            }
        });
    }
    private void setShopList( List<ConfigurationModel> list) {
        ArrayList<String> shop_name = new ArrayList<>();
        ArrayList<String> shop_id = new ArrayList<>();
        ArrayList<Integer> langList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            shop_name.add(list.get(i).getName());
            shop_id.add(String.valueOf(list.get(i).getId()));
        }
        final CharSequence[] items = shop_name.toArray(new CharSequence[shop_name.size()]);
        boolean[] selected_shop = new boolean[shop_name.size()];


        binding.textViewShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserActivity.this);
                builder.setTitle("Select Shop Name");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(items, selected_shop, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {

                            langList.add(i);
                            Collections.sort(langList);
                        } else {

                            langList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        StringBuilder builder_ware_id = new StringBuilder();
                        for (int j = 0; j < langList.size(); j++) {
                            stringBuilder.append(items[langList.get(j)]);
                            builder_ware_id.append(shop_id.get(langList.get(j)));
                            if (j != langList.size() - 1) {
                                stringBuilder.append(", ");
                                builder_ware_id.append(",");
                            }
                        }

                        str_shop_id = builder_ware_id.toString();
                        Log.d("str_warehouse_id", str_shop_id);
                        binding.textViewShop.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// use for loop
                        for (int j = 0; j < selected_shop.length; j++) {
                            selected_shop[j] = false;
                            langList.clear();
                            binding.textView.setText("");
                        }
                    }
                });

                builder.show();
            }
        });
    }
    private void UserUpdate(String str_comp_id, String str_shop_id) {
        SessionManagement sm = new SessionManagement(UpdateUserActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("token", sm.getJWTToken());
        params.put("property_warehouse_id", ware_house_id);
        params.put("name", str_name);
        params.put("login", str_user_id);
        params.put("allowed_pos", str_shop_id);
        params.put("company_id", default_company_id+"");
        params.put("company_ids", str_comp_id);


            Utility.showDialoge("Please wait while a moment...",UpdateUserActivity.this);


        Log.d("url_addproduct", params.toString());
        new VolleyRequestHandler(UpdateUserActivity.this, params).createRequest(ApiConstants.POST_USER_UPDATE, new VolleyCallback() {
            private String message = " failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String message_success = obj.optString("message");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                     Utility.dismissDialoge();

                    Toast.makeText(UpdateUserActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                 Utility.dismissDialoge();

                Log.v("Response", result);
                Toast.makeText(UpdateUserActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getCompanyList() {
        SessionManagement sm = new SessionManagement(UpdateUserActivity.this);
        RequestQueue queue = Volley.newRequestQueue(UpdateUserActivity.this);
        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_CUSTOMER_DISCOUNT_LIST;

        String url = ApiConstants.BASE_URL + ApiConstants.GET_COMPANIES + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        //    Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.d("ALL_CROPS_url",url);
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
                        // dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("companies");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            StateModel   stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            int id = data.optInt("id");
                            String name = data.optString("name");
                            stateModel.setId(id);
                            stateModel.setName(name);
                            company_list.add(stateModel);
                        }
                        setCompanyList(company_list);
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UpdateUserActivity.this, company_list);
                            binding.defCompSpinner.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(UpdateUserActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

}