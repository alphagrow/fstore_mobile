package com.growit.posapp.fstore.ui.fragments.Inventory;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.PurchaseOrderDetailAdapter;
import com.growit.posapp.fstore.adapters.TransfersOrderDetailAdapter;
import com.growit.posapp.fstore.databinding.FragmentTransfersDetailsBinding;
import com.growit.posapp.fstore.databinding.FragmentTransfersOrderListBinding;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.TransfersModel;
import com.growit.posapp.fstore.model.TransfersModelList;
import com.growit.posapp.fstore.ui.fragments.PurchaseOrder.PurchaseOrderDetailFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class TransfersDetailsFragment extends Fragment {

FragmentTransfersDetailsBinding binding;
    TransfersModel productDetail;
    TransfersOrderDetailAdapter adapter;
    int position=-1;
    SessionManagement sm;
    int orderID;
    int purchase_order_id;
    public TransfersDetailsFragment() {
        // Required empty public constructor
    }

    public static TransfersDetailsFragment newInstance() {
        TransfersDetailsFragment fragment = new TransfersDetailsFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_transfers_details, container, false);
        init();
        return  binding.getRoot();
    }
    private void init(){
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            String orderDetail = getArguments().getString("OrderDetail");
            Gson gson = new Gson();
            Type listType = new TypeToken<TransfersModel>() {
            }.getType();
            productDetail = gson.fromJson(orderDetail, listType);
            getOrderDetail();
        }
        binding.invoiceDownload.setOnClickListener(v -> showPDF(productDetail.getData().get(position).getId()));
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }
    public void showPDF(int _id) {
        // String url= ApiConstants.BASE_URL +ApiConstants.GET_PURCHASE_ORDER_DOWNLOAD+_id+"&user_id="+sm.getUserID()+"&token="+sm.getJWTToken();
        SessionManagement sm=new SessionManagement(getActivity());
        try {
            String url= ApiConstants.BASE_URL +ApiConstants.GET_DELIVERY_DOWNLOAD+"&transfer_id="+_id+"&user_id="+sm.getUserID()+"&token="+sm.getJWTToken();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            getActivity().startActivity(intent);
//            Log.d("url",url);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
    }
    private void getOrderDetail() {
        if (productDetail.getData() == null || productDetail.getData().size() == 0) {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.orderRecyclerView.setVisibility(View.GONE);
        } else {
            binding.noDataFound.setVisibility(View.GONE);
            binding.orderRecyclerView.setVisibility(View.VISIBLE);
            binding.orderId.setText(productDetail.getData().get(position).getOrigin());
            binding.locationTo.setText(productDetail.getData().get(position).getSourceLocation());
            binding.locationFrom.setText(productDetail.getData().get(position).getDestinationLocation());
            binding.wareHouse.setText(productDetail.getData().get(position).getName());
            binding.state.setText(productDetail.getData().get(position).getState());
            orderID=productDetail.getData().get(position).getId();

//            binding.date.setText(productDetail.getOrders().get(position).getOrderDate());
//            double paidAmount= Utility.decimalFormat(Double.parseDouble(productDetail.getData().get(position).getAmountTotal()));
//            binding.totalValue.setText(paidAmount+"");
////            binding.paidBy.setText(productDetail.getOrders().get(position).getPayment_type());
//            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//            adapter = new TransfersOrderDetailAdapter(getActivity(), productDetail.getData().get(position).getOrderLines());
//            binding.orderRecyclerView.setAdapter(adapter);
//            binding.orderRecyclerView.setLayoutManager(layoutManager);

        }
    }


}