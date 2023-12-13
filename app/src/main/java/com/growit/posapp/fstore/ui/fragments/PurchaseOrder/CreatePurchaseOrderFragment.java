package com.growit.posapp.fstore.ui.fragments.PurchaseOrder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.growit.posapp.fstore.adapters.AttributeSpinnerAdapter;
import com.growit.posapp.fstore.adapters.CropAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.ProductListAdapter;
import com.growit.posapp.fstore.adapters.PurchaseProductListAdapter;
import com.growit.posapp.fstore.adapters.VendorListAdapter;
import com.growit.posapp.fstore.databinding.FragmentCreatePurchaseOrderBinding;
import com.growit.posapp.fstore.model.Attribute;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.Purchase.PurchaseProductModel;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.model.VendorModel;
import com.growit.posapp.fstore.ui.fragments.ProductDetailFragment;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.VendorListFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreatePurchaseOrderFragment extends Fragment {

    FragmentCreatePurchaseOrderBinding binding;
    Activity contexts;
    List<Value> cropList = new ArrayList<>();
    ProductListAdapter productListAdapter;
    protected List<Product> productList = new ArrayList<>();
    private String cropID = "";
    private String cropName = "";
    CropAdapter cropAdapter = null;
    String vendor_id;
    List<StateModel> vendorNames = new ArrayList<>();
    String crop_id, crop_name;

    PurchaseModel model;
    List<PurchaseProductModel> purchaseProductModel;
    PurchaseProductListAdapter adapter;
    private LinearLayout containerLL;
    List<Attribute> attributes;
    List<Value> value;
    Spinner spinner;
    String str_variant;
    String product_name;
    int patternType = -1;
    String[] variantArray = null;
    StringBuilder stringBuilder;

    public CreatePurchaseOrderFragment() {
        // Required empty public constructor
    }

    public static CreatePurchaseOrderFragment newInstance() {
        return new CreatePurchaseOrderFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_create_purchase_order, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_purchase_order, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {


        if (Utility.isNetworkAvailable(getActivity())) {
            getVendorList();
            getCropRequest();
        } else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        binding.croplistView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), binding.croplistView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        cropID = cropList.get(position).getValueId() + "";
                        cropName = cropList.get(position).getValueName();
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        cropAdapter = new CropAdapter(getActivity(), cropList, position);
                        binding.croplistView.setAdapter(cropAdapter);
                        binding.croplistView.setLayoutManager(layoutManager);
                        getProductList(cropID);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        binding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), binding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        product_name = purchaseProductModel.get(position).getProductName();
                        attributes = purchaseProductModel.get(position).getAttributes();
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        createDynamicSpinner(attributes.size(),product_name);


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



        binding.vendorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    vendor_id = vendorNames.get(position).getId() + "";


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(vendor_id !=null) {
                    CreatePurchaseOrder();
                }else {
                    Toast.makeText(getContext(), "Select Vendor", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void val(int position) {
                                variantArray[patternType] = purchaseProductModel.get(0).getAttributes().get(patternType).getValues().get(position).getValueName();

                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < variantArray.length; i++) {
                            stringBuilder.append(variantArray[i]).append(",");
                        }


        binding.setPatternsTxt.setText(stringBuilder);
    }

    private void createDynamicSpinner(int n,String product_name) {
        variantArray = new String[n];
        for (int j = 0; j < n; j++) {
            LinearLayout.LayoutParams txtLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtLayoutParam.gravity = Gravity.CENTER;
            LinearLayout.LayoutParams spinnerLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            spinnerLayoutParam.gravity = Gravity.CENTER;
            TextView headingTV = new TextView(getActivity());
            headingTV.setText(attributes.get(j).getAttributeName());

            value = new ArrayList<>();
            String value_name = null;
            for (int i = 0; i < attributes.get(j).getValues().size(); i++) {
                Value value1 = new Value();
                value1.setValueId(attributes.get(j).getValues().get(i).getValueId());
                value1.setValueName(attributes.get(j).getValues().get(i).getValueName());
                //  value_name= attributes.get(j).getValues().get(i).getValueName();
                value.add(value1);
            }
            headingTV.setTextSize(20f);
            headingTV.setTextColor(getResources().getColor(R.color.black));
            headingTV.setTypeface(Typeface.DEFAULT_BOLD);
            headingTV.setPadding(20, 20, 20, 20);
            headingTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            headingTV.setLayoutParams(txtLayoutParam);
            spinner = new Spinner(getActivity());
            spinner.setPadding(20, 20, 20, 20);
            spinner.setLayoutParams(spinnerLayoutParam);
            spinner.setId(j);
            binding.idLLContainer.addView(headingTV);
            binding.idLLContainer.addView(spinner);

            getSpinner(spinner, spinner.getId(), value,product_name);
        }

    }

    private void getSpinner(Spinner spinner, int spinner_id, List<Value> value,String product_name ) {
        AttributeSpinnerAdapter adapter = new AttributeSpinnerAdapter(getActivity(), value);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
//                    String variant_Array  = value.get(position).getValueName();
                    Toast.makeText(contexts, ""+value.get(position).getValueName(), Toast.LENGTH_SHORT).show();
                 //   variantArray.add(value.get(position).getValueName());
                    val(position);
                }
//                stringBuilder = new StringBuilder();
//                for (int i = 0; i < variantArray.size(); i++) {
//                    stringBuilder.append(variantArray.get(i)).append(",");
//                }
//                binding.setPatternsTxt.setText(product_name+"("+ stringBuilder +")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    private void getProductList(String pos_category_id) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + pos_category_id + "&" + "token=" + sm.getJWTToken();
        Log.d("product_list", url);
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
                        Type listType = new TypeToken<PurchaseModel>() {
                        }.getType();
                        model = gson.fromJson(response.toString(), listType);

                        for (int i = 0; i < model.getData().size(); i++) {
                            crop_id = String.valueOf(model.getData().get(i).getCategoryId());
                            crop_name = model.getData().get(i).getCategoryName();
                            purchaseProductModel = model.getData().get(i).getProducts();
                        }
                        if (purchaseProductModel == null || purchaseProductModel.size() == 0) {
                            Toast.makeText(contexts, "Data not Found", Toast.LENGTH_SHORT).show();
                        } else {
                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            adapter = new PurchaseProductListAdapter(getActivity(), purchaseProductModel);
                            binding.recyclerView.setAdapter(adapter);
                            binding.recyclerView.setLayoutManager(layoutManager);

                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }

    private void getCropRequest() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_CROPS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
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
                        JSONArray jsonArray = obj.getJSONArray("data");
                        cropList = new ArrayList<>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Value cropPattern = new Value();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer cropID = data.optInt("category_id");
                                String name = data.optString("name");
                                String image_url = data.optString("image_url");
                                cropPattern.setValueId(Integer.valueOf(cropID + ""));
                                cropPattern.setValueName(name);
                                cropPattern.setImage_url(image_url);
                                cropPattern.setSelectedPosition(0);
                                cropList.add(cropPattern);
                            }
                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            cropAdapter = new CropAdapter(getActivity(), cropList, -1);
                            binding.croplistView.setAdapter(cropAdapter);
                            binding.croplistView.setLayoutManager(layoutManager);
                            cropID = cropList.get(0).getValueId() + "";
                            cropName = cropList.get(0).getValueName();
                            getProductList(cropID);
                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    private void getVendorList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken() + "&" + "active=" + "true";

        // String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
//        Utility.showDialoge("Please wait while a moment...", getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                vendorNames = new ArrayList<>();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("vendors");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("--Select Vendor--");
                        vendorNames.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            int id = data.optInt("vendor_id");
                            String name = data.optString("name");
                            stateModel.setId(id);
                            stateModel.setName(name);
                            vendorNames.add(stateModel);
                        }
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), vendorNames);
                            binding.vendorSpinner.setAdapter(adapter);
                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }
    private void CreatePurchaseOrder(){
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID()+ "");
        params.put("token", sm.getJWTToken());
        params.put("vendor_id", vendor_id);
        params.put("products", "[]");
        params.put("picking_type_id", "8");
        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("Pur_create_order", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CREATE_POS_CATEGORY, new VolleyCallback() {


            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
              String  status = obj.optString("status");
                String  message = obj.optString("message");

                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();


                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
