package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ExtraPriceAdapter;
import com.growit.posapp.fstore.model.ExtraPriceData;
import com.growit.posapp.fstore.model.ExtraVariantData;
import com.growit.posapp.fstore.model.TransfersModel;
import com.growit.posapp.fstore.tables.Customer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtraPriceFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ExtraPriceAdapter customAdapter;
    TextView searchEditTxt;
    private TextView total_customer_text;
    ImageView backBtn;
    private int position = 0;
    ExtraPriceData extraPriceData;
    private List<ExtraVariantData> search_variant;
    ImageView gif;
    public static ExtraPriceFragment newInstance() {
        return new ExtraPriceFragment();
    }
    public ExtraPriceFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.extraprice_fragment, parent, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        searchEditTxt = view.findViewById(R.id.seacrEditTxt);
        LinearLayout lay_add_customer = view.findViewById(R.id.lay_add_customer);
        total_customer_text = view.findViewById(R.id.total_customer_text);
        backBtn = view.findViewById(R.id.backBtn);
        Button add_text = view.findViewById(R.id.submit_btn);
        lay_add_customer.setVisibility(View.VISIBLE);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        gif = view.findViewById(R.id.gif);
        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(gif);
       gif.setVisibility(View.GONE);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
//            String orderDetail = getArguments().getString("OrderDetail");
            search_variant = (List<ExtraVariantData>) getArguments().getSerializable("OrderDetail");

//            Gson gson = new Gson();
//            Type listType = new TypeToken<ExtraPriceData>() {
//            }.getType();
//            extraPriceData = gson.fromJson(orderDetail, listType);

        }
        total_customer_text.setText("Total: " + search_variant.get(position).getVariants().size() + " " + "Variants List");
        customAdapter = new ExtraPriceAdapter(getActivity(), search_variant.get(position).getVariants());
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < search_variant.get(position).getVariants().size(); i++) {
                    View view = linearLayoutManager.getChildAt(i);
                    if(view !=null) {
                        EditText mrpEditText = view.findViewById(R.id.mrp_text);
                        EditText priceEditText = view.findViewById(R.id.priceTxt);
                        String mrp = mrpEditText.getText().toString();
                        String extra_price = priceEditText.getText().toString();
                        JSONObject obj = new JSONObject();
                        try {
                            obj.putOpt("product_tmpl_id", search_variant.get(position).getProductId());
                            obj.putOpt("variant_id", Integer.parseInt(search_variant.get(position).getVariants().get(i).getProductVariant()));
                            obj.putOpt("extra_price", Double.parseDouble(extra_price));
                            obj.putOpt("mrp_price", Double.parseDouble(mrp));
                            jsonArray.put(obj);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                try {
                    createExtraPrice(jsonArray);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.v("----", jsonArray.toString());
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = ProductExtraPriceListFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
     //   getVariants();
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


//    private void getVariants() {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
//        String url = ApiConstants.BASE_URL + ApiConstants.GET_EXTRA_PRICE;
//        Utility.showDialoge("Please wait while a moment...", getActivity());
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(response.toString());
//                    int statusCode = obj.optInt("statuscode");
//                    String status = obj.optString("status");
//                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                        Utility.dismissDialoge();
//                        Gson gson = new Gson();
//                        Type listType = new TypeToken<ExtraPriceData>() {
//                        }.getType();
//                        extraPriceData = gson.fromJson(response.toString(), listType);
//                        customAdapter = new ExtraPriceAdapter(getActivity(), extraPriceData.getData());
//                        recyclerView.setAdapter(customAdapter);
//                        recyclerView.setLayoutManager(linearLayoutManager);
//                        total_customer_text.setText("Total: " + extraPriceData.getData().size() + " " + "Variants");
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//            }
//        }, error -> {
//            recyclerView.setVisibility(View.GONE);
//        });
//        queue.add(jsonObjectRequest);
//    }



    private void createExtraPrice(JSONArray jsonArray) throws JSONException {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID()+"");
        params.put("token", sm.getJWTToken());
        params.put("pricelist_data", jsonArray.toString());
//        Utility.showDialoge("Please wait while a moment...", getActivity());
        gif.setVisibility(View.VISIBLE);
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.ADD_EXTRAPRICE, new VolleyCallback() {
            private String message = "Order failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response POS Order", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String  str_message = obj.optString("message");
                gif.setVisibility(View.GONE);
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                    Fragment fragment = ProductExtraPriceListFragment.newInstance();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }
            }

            @Override
            public void onError(String result) throws Exception {
//                Utility.dismissDialoge();
                gif.setVisibility(View.GONE);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
