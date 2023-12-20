package com.growit.posapp.fstore.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.OrderDetailAdapter;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import java.lang.reflect.Type;


public class OrderDetailFragment extends Fragment {


    private RecyclerView recyclerView;

    LinearLayout invoiceDownload;
    ProductDetail productDetail;
    OrderDetailAdapter orderHistoryAdapter;
    int position=-1;
    SessionManagement sm;
    int orderID=0;
    TextView noDataFound,orderNo,total_product,date,total_value,paid_by;

    public OrderDetailFragment() {
        // Required empty public constructor
    }


    public static OrderDetailFragment newInstance() {
        OrderDetailFragment fragment = new OrderDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_order_detail, container, false);
        recyclerView = view.findViewById(R.id.orderRecyclerView);
        sm=new SessionManagement(getActivity());
        orderNo=view.findViewById(R.id.order_id);
        invoiceDownload=view.findViewById(R.id.invoiceDownload);
        total_product=view.findViewById(R.id.total_product);
        date=view.findViewById(R.id.date);
        total_value=view.findViewById(R.id.total_value);
        paid_by=view.findViewById(R.id.paid_by);
        noDataFound = view.findViewById(R.id.noDataFound);

        if (getArguments() != null) {
            position = getArguments().getInt("position");
            String orderDetail = getArguments().getString("OrderDetail");
            Gson gson = new Gson();
            Type listType = new TypeToken<ProductDetail>() {
            }.getType();
            productDetail = gson.fromJson(orderDetail, listType);
            getOrderDetail();
        }
        invoiceDownload.setOnClickListener(v -> showPDF(orderID+""));
        return view;
    }

    public void showPDF(String _id) {
        try {
            String url= ApiConstants.BASE_URL +ApiConstants.INVOICE_DOWNLOAD+_id+"&user_id="+sm.getUserID()+"&token="+sm.getJWTToken();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            getActivity().startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
    }
    private void getOrderDetail() {
        if (productDetail.getOrders() == null || productDetail.getOrders().size() == 0) {
            noDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noDataFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            orderNo.setText(productDetail.getOrders().get(position).getOrderNumber());
            orderID=productDetail.getOrders().get(position).getId();
            if(productDetail.getOrders().get(position).getProducts()!=null) {
                int size=productDetail.getOrders().get(position).getProducts().size();
                total_product.setText(size+"");
            }else{
                total_product.setText("0");
            }
            date.setText(productDetail.getOrders().get(position).getOrderDate());
            double paidAmount=Utility.decimalFormat(Double.parseDouble(productDetail.getOrders().get(position).getAmountPaid()));
            total_value.setText(paidAmount+"");
            paid_by.setText(productDetail.getOrders().get(position).getPayment_type());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            orderHistoryAdapter = new OrderDetailAdapter(getActivity(), productDetail.getOrders().get(position).getProducts());
            recyclerView.setAdapter(orderHistoryAdapter);
            recyclerView.setLayoutManager(layoutManager);

        }
    }

}