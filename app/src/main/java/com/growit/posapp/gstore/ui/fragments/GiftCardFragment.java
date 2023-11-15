package com.growit.posapp.gstore.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.adapters.GiftCardAdapter;
import com.growit.posapp.gstore.model.GiftCardListModel;
import com.growit.posapp.gstore.model.GiftCardModel;
import com.growit.posapp.gstore.model.Product;
import com.growit.posapp.gstore.model.ProductDetail;
import com.growit.posapp.gstore.ui.LoginActivity;
import com.growit.posapp.gstore.utils.ApiConstants;
import com.growit.posapp.gstore.utils.RecyclerItemClickListener;
import com.growit.posapp.gstore.utils.SessionManagement;
import com.growit.posapp.gstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GiftCardFragment extends Fragment {
    GiftCardModel giftCardModel;
    TextView noDataFound;
    private RecyclerView recyclerView;
    List<GiftCardListModel> giftmodel = new ArrayList<>();
    GiftCardAdapter giftCardsAdapter;

    public static GiftCardFragment newInstance() {
        GiftCardFragment fragment = new GiftCardFragment();
        return fragment;
    }

    public GiftCardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_giftcard, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noDataFound = view.findViewById(R.id.noDataFound);
        if (Utility.isNetworkAvailable(getActivity())) {
            getGiftCards();
        } else {
            Toast.makeText(getActivity(), "Network not available.Please try later!", Toast.LENGTH_SHORT).show();

        }


        noDataFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "Network not available.Please try later!", Toast.LENGTH_SHORT).show();
                    return;
                }
                getGiftCards();
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

    private void getGiftCards() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());

//        String url = ApiConstants.BASE_URL + ApiConstants.GET_TRANSACTION_HISTORY + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken()+"&"+"start_date="+"2023-08-16"+"&"+"end_date="+"2023-08-16";
        String url = ApiConstants.BASE_URL + ApiConstants.GET_GIFT_CARDS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
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
                        Gson gson = new Gson();
                        Type listType = new TypeToken<GiftCardModel>() {
                        }.getType();

                        giftCardModel = gson.fromJson(response.toString(), listType);
                        if (giftCardModel.getData() == null || giftCardModel.getData().size() == 0) {
                            noDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noDataFound.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            giftCardsAdapter = new GiftCardAdapter(getActivity(), giftCardModel.getData());
                            recyclerView.setAdapter(giftCardsAdapter);
                            recyclerView.setLayoutManager(layoutManager);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
            noDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });
        queue.add(jsonObjectRequest);
    }


}