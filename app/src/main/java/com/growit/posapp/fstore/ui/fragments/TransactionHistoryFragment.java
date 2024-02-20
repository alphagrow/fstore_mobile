package com.growit.posapp.fstore.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.TransactionHistoryAdapter;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.Transaction;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionHistoryFragment extends Fragment {

    ProductDetail productDetail;
    TextView noDataFound;
    private RecyclerView recyclerView;
    TransactionHistoryAdapter transactionHistoryAdapter;
    EditText seacrEditTxt;
    Activity activity;
    ImageView gif_loader;
    public static TransactionHistoryFragment newInstance() {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        gif_loader =view.findViewById(R.id.gif_loader);
        Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(gif_loader);
        gif_loader.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.transactionRecyclerView);
        noDataFound = view.findViewById(R.id.noDataFound);
        seacrEditTxt = view.findViewById(R.id.seacrEditTxt);
        if (Utility.isNetworkAvailable(getActivity())) {
            getTransactionHistory();

        }else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        noDataFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                getTransactionHistory();
            }
        });
        seacrEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        return view;

    }
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        //  hideKeyboard(activity);
    }
    private void getTransactionHistory() {
        SessionManagement sm = new SessionManagement(getActivity());
//        Utility.showDialoge("Please wait while a moment...", getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());//162.246.254.203:8069
//        String url = ApiConstants.BASE_URL + ApiConstants.GET_TRANSACTION_HISTORY + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken()+"&"+"start_date="+"2023-08-16"+"&"+"end_date="+"2023-08-16";
        String url = ApiConstants.BASE_URL + ApiConstants.GET_TRANSACTION_HISTORY + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        gif_loader.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("url", url);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    gif_loader.setVisibility(View.GONE);
//                    Utility.dismissDialoge();
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<ProductDetail>() {
                        }.getType();

                        productDetail = gson.fromJson(response.toString(), listType);
                        if (productDetail.getTransactions() == null || productDetail.getTransactions().size() == 0) {
                            noDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noDataFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            transactionHistoryAdapter = new TransactionHistoryAdapter(activity, productDetail.getTransactions());
                            recyclerView.setAdapter(transactionHistoryAdapter);
                            recyclerView.setLayoutManager(layoutManager);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            gif_loader.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Transaction> filteredList = new ArrayList<>();

        // running a for loop to compare elements.
        for (Transaction item : productDetail.getTransactions()) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getCustomerName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(getActivity(), R.string.NO_DATA, Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            transactionHistoryAdapter.filterList(filteredList);
        }
    }

}