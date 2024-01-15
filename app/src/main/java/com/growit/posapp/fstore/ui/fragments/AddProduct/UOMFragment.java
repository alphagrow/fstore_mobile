package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.adapters.UOMAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddShopAndShopListBinding;
import com.growit.posapp.fstore.databinding.FragmentUOMBinding;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.UomCategoryModel;
import com.growit.posapp.fstore.ui.fragments.Setting.AddShopAndShopListFragment;
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


public class UOMFragment extends Fragment {

    FragmentUOMBinding binding;
    List<ConfigurationModel> list;
    Activity contexts;
    UOMAdapter adapter;
    EditText uom_cat_ed,ed_name,ed_code;
    boolean isAllFieldsChecked = false;
    ProductDetail productDetail;
    Dialog dialog;
    JSONArray  prjsonArray;
    public UOMFragment() {
        // Required empty public constructor
    }
    public static UOMFragment newInstance() {
        return new UOMFragment();
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
        //   return inflater.inflate(R.layout.fragment_add_shop_and_shop_list, container, false);
        binding = FragmentUOMBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }



    private void init () {
        list = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVendor.setLayoutManager(layoutManager);
        if (Utility.isNetworkAvailable(getContext())) {
            getUOMList();

        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refreshLayout.setRefreshing(false);
                if (Utility.isNetworkAvailable(getContext())) {
                    getUOMList();
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

                if (Utility.isNetworkAvailable(getContext())) {
                    showDialogeReceiveProduct();
                } else {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                }
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

//        binding.recyclerVendor.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(),  binding.recyclerVendor, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        String  cust_name = list.get(position).getName();
//                        int  _id = list.get(position).getId();
//                      //  showDialogeUpdateReceiveProduct(cust_name,_id);
//                    }
//
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }

    private  void showDialogeReceiveProduct() {

         dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.uom_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        uom_cat_ed = dialog.findViewById(R.id.uom_cat_ed);
        ed_name = dialog.findViewById(R.id.ed_name);
        ed_code = dialog.findViewById(R.id.ed_code);
        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);


        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_cat_name = uom_cat_ed.getText().toString();
                String str_name = ed_name.getText().toString();
                String str_code = ed_code.getText().toString();

                isAllFieldsChecked= CheckAllFields();
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isAllFieldsChecked) {
                      prjsonArray = new JSONArray();

                    try {
                        JSONObject productOBJ = new JSONObject();
                        productOBJ.putOpt("name", str_name);
                        productOBJ.putOpt("l10n_in_code",str_code);
                        prjsonArray.put(productOBJ);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    addDiscount(str_cat_name,prjsonArray.toString());
                }
             //   dialog.dismiss();

            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private  void showDialogeUpdateReceiveProduct(String name,int id) {

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.uom_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        EditText  uom_cat_ed = dialog.findViewById(R.id.uom_cat_ed);
        EditText  uom_ed_name = dialog.findViewById(R.id.ed_name);
        EditText  uom_ed_code = dialog.findViewById(R.id.ed_code);

        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);
        uom_cat_ed.setText(name);
        uom_ed_name.setText(name);
        uom_ed_code.setText(name);


        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_cate = uom_cat_ed.getText().toString();
                String str_name = uom_ed_name.getText().toString();
                String str_code = uom_ed_code.getText().toString();

                isAllFieldsChecked= CheckAllFields();
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isAllFieldsChecked) {
                    DiscountUpdate(str_name,id);
                }
                dialog.dismiss();

            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean CheckAllFields() {
        if (uom_cat_ed.length()== 0) {
            uom_cat_ed.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Category Name", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (ed_name.length()== 0) {
            ed_name.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Name", Toast.LENGTH_SHORT).show();

            return false;
        }
        if (ed_code.length()== 0) {
            ed_code.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Name", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private void addDiscount(String str_name,String uom_lines) {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID()+"");
        params.put("token", sm.getJWTToken());
        params.put("name", str_name);
        params.put("uom_lines", uom_lines);
        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CREATE_UOM, new VolleyCallback() {
            private String message = " failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                String message = obj.optString("message");
                String error_message = obj.optString("error_message");
                if (statusCode==200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    ed_name.setText("");
                    uom_cat_ed.setText("");
                    ed_code.setText("");
                    dialog.dismiss();
                    getUOMList();
                }else {
                    dialog.dismiss();
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

    private void DiscountUpdate(String name,int id) {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("token", sm.getJWTToken());
        params.put("name", name);
        params.put("shop_id", id+"");




        new VolleyRequestHandler(getActivity(), params).putRequest(ApiConstants.PUT_UPDATE_SHOPS, new VolleyCallback() {
            private String message = "Update failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                String message = obj.optString("message");
                String error_message = obj.optString("error_message");
                if (statusCode==200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    getUOMList();
                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Log.v("Response", result.toString());
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUOMList() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_UOM_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
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

                    if (status.equalsIgnoreCase("success")) {
                        // Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ProductDetail>() {
                        }.getType();

                        productDetail = gson.fromJson(response.toString(), listType);
                        if (productDetail.getUomCategories() == null || productDetail.getUomCategories().size() == 0) {
                                binding.noDataFound.setVisibility(View.VISIBLE);
                            } else {
                                binding.totalCustomerText.setText("Total : "+productDetail.getUomCategories().size()+"  ");
                                binding.noDataFound.setVisibility(View.GONE);
                                adapter = new UOMAdapter(getActivity(), productDetail.getUomCategories());
                                binding.recyclerVendor.setAdapter(adapter);
                            }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }
    private void filterList (String text){

        ArrayList<UomCategoryModel> model = new ArrayList<>();
        for (UomCategoryModel detail : productDetail.getUomCategories()) {
            if (detail.getName().toLowerCase().contains(text.toLowerCase())) {
                model.add(detail);
            }
        }

        adapter.updateList(model);
    }

}
