package com.growit.posapp.fstore.ui.fragments.AddProduct;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ExtraPriceAdapter;
import com.growit.posapp.fstore.adapters.ProductExtraPriceAdapter;
import com.growit.posapp.fstore.adapters.StoreInventoryAdapters;
import com.growit.posapp.fstore.model.ExtraPriceData;
import com.growit.posapp.fstore.model.ExtraVariantData;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.ui.fragments.Inventory.StoreInventoryDetailFragment;
import com.growit.posapp.fstore.ui.fragments.Inventory.StoreInventoryFragment;
import com.growit.posapp.fstore.ui.fragments.Inventory.TransfersDetailsFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ProductExtraPriceListFragment extends Fragment {
    protected List<ExtraVariantData> productList = new ArrayList<>();
    private RecyclerView recyclerView;

    TextView noDataFound,total_order_text,add_text;
    EditText seacrEditTxt;
    private  String mResponse="";
    ExtraPriceData extraPriceData;
    ProductExtraPriceAdapter customAdapter;
    public ProductExtraPriceListFragment() {
        // Required empty public constructor
    }


    public static ProductExtraPriceListFragment newInstance() {
        ProductExtraPriceListFragment fragment = new ProductExtraPriceListFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_extra_price_list, container, false);
        recyclerView = view.findViewById(R.id.orderRecyclerView);
        noDataFound = view.findViewById(R.id.noDataFound);
        total_order_text = view.findViewById(R.id.total_order_text);
        add_text = view.findViewById(R.id.add_text);
        seacrEditTxt = view.findViewById(R.id.seacrEditTxt);
        if (Utility.isNetworkAvailable(getActivity())) {
            getProductExtraPrice();
        }else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
        }

        noDataFound.setOnClickListener(v -> getProductExtraPrice());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),  recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        bundle.putString("OrderDetail", mResponse);
                        bundle.putInt("position", position);
                        Fragment fragment = ExtraPriceFragment.newInstance();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        seacrEditTxt.addTextChangedListener(new TextWatcher() {
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

        return view;
    }

    private void getProductExtraPrice() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_EXTRA_PRICE;
        Utility.showDialoge("Please wait while a moment...", getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject obj = null;
                mResponse=response.toString();
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ExtraPriceData>() {
                        }.getType();
                        extraPriceData = gson.fromJson(response.toString(), listType);
                        customAdapter = new ProductExtraPriceAdapter(getActivity(), extraPriceData.getData());
                        recyclerView.setAdapter(customAdapter);

                        total_order_text.setText("Total: " + extraPriceData.getData().size() + " " + "Products");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            recyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }

    private void filterList(String text){

        ArrayList<ExtraVariantData> model = new ArrayList<>();
        for (ExtraVariantData detail : extraPriceData.getData()){
            if (detail.getProductName().toLowerCase().contains(text.toLowerCase())){
                model.add(detail);
            }
        }

        customAdapter.updateList(model);

    }
}