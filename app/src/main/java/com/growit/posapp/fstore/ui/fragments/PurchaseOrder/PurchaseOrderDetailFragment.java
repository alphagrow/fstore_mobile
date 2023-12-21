package com.growit.posapp.fstore.ui.fragments.PurchaseOrder;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.growit.posapp.fstore.adapters.OrderDetailAdapter;
import com.growit.posapp.fstore.adapters.PurchaseOrderDetailAdapter;
import com.growit.posapp.fstore.databinding.FragmentPurchaseOrderDetailBinding;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.ui.ResetPasswordActivity;
import com.growit.posapp.fstore.ui.fragments.OrderDetailFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class PurchaseOrderDetailFragment extends Fragment {

    FragmentPurchaseOrderDetailBinding binding;
    PurchaseModel productDetail;
    PurchaseOrderDetailAdapter orderHistoryAdapter;
    int position=-1;
    SessionManagement sm;
    int orderID;
    int purchase_order_id;
    public PurchaseOrderDetailFragment() {
        // Required empty public constructor
    }

    public static PurchaseOrderDetailFragment newInstance() {
        PurchaseOrderDetailFragment fragment = new PurchaseOrderDetailFragment();
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
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_purchase_order_detail, container, false);
        init();
        return  binding.getRoot();
    }
    private void init(){
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            String orderDetail = getArguments().getString("OrderDetail");
            Gson gson = new Gson();
            Type listType = new TypeToken<PurchaseModel>() {
            }.getType();
            productDetail = gson.fromJson(orderDetail, listType);
            getOrderDetail();
        }
        binding.invoiceDownload.setOnClickListener(v -> showPDF(productDetail.getOrders().get(position).getId()));
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkAvailable(getContext())) {
                    cancelOrder(productDetail.getOrders().get(position).getId());
                } else {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                }
            }
        });
        binding.receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkAvailable(getContext())) {
                    showDialogeReceiveProduct();
                } else {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
    public void showPDF(int _id) {
       // String url= ApiConstants.BASE_URL +ApiConstants.GET_PURCHASE_ORDER_DOWNLOAD+_id+"&user_id="+sm.getUserID()+"&token="+sm.getJWTToken();

        try {
            String url= ApiConstants.BASE_URL +ApiConstants.GET_PURCHASE_ORDER_DOWNLOAD+"&purchase_order_id="+_id;
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
        if (productDetail.getOrders() == null || productDetail.getOrders().size() == 0) {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.orderRecyclerView.setVisibility(View.GONE);
        } else {
            binding.noDataFound.setVisibility(View.GONE);
            binding.orderRecyclerView.setVisibility(View.VISIBLE);
            binding.orderId.setText(productDetail.getOrders().get(position).getName());
            if(productDetail.getOrders().get(position).getReceiptStatus().equalsIgnoreCase("false")) {
            }else if(productDetail.getOrders().get(position).getReceiptStatus().equalsIgnoreCase("full")) {
                binding.status.setText("Received");
            }else {
                binding.status.setText("Pending");
            }

            orderID=productDetail.getOrders().get(position).getId();
            if(productDetail.getOrders().get(position).getOrderLines()!=null) {
                int size=productDetail.getOrders().get(position).getOrderLines().size();
                binding.totalProduct.setText(size+"");
            }else{
                binding.totalProduct.setText("0");
            }
         //     purchase_order_id= productDetail.getOrders().get(position).getId();
            binding.date.setText(productDetail.getOrders().get(position).getOrderDate());
            double paidAmount= Utility.decimalFormat(Double.parseDouble(productDetail.getOrders().get(position).getAmountTotal()));
            binding.totalValue.setText(paidAmount+"");
//            binding.paidBy.setText(productDetail.getOrders().get(position).getPayment_type());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            orderHistoryAdapter = new PurchaseOrderDetailAdapter(getActivity(), productDetail.getOrders().get(position).getOrderLines());
            binding.orderRecyclerView.setAdapter(orderHistoryAdapter);
            binding.orderRecyclerView.setLayoutManager(layoutManager);

        }
    }
    private void cancelOrder(int purchase_order_id){
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
//            params.put("user_id", sm.getUserID()+"");
//            params.put("token", sm.getJWTToken());
        params.put("purchase_order_id", purchase_order_id+"");

        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("cancel", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CANCEL_PURCHASE_ORDER, new VolleyCallback() {
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
                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onError(String result) throws Exception {
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
                Utility.dismissDialoge();
            }
        });




    }
    private  void showDialogeReceiveProduct() {

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.purchase_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        EditText  qut_text = dialog.findViewById(R.id.qut_text);
        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);


        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_qut= qut_text.getText().toString();
                if(!str_qut.isEmpty()) {
                    getReceiveProducts(str_qut,productDetail.getOrders().get(position).getId());
                }else {
                    Toast.makeText(getActivity(), "Enter the quantity", Toast.LENGTH_SHORT).show();

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

    private void getReceiveProducts(String qut,int purchase_order_id){

        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
//            params.put("user_id", sm.getUserID()+"");
//            params.put("token", sm.getJWTToken());
        params.put("quantity", qut+"");
        params.put("purchase_order_id", purchase_order_id+"");

        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("POST_RECEIVE_PRODUCTS", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_RECEIVE_PRODUCTS, new VolleyCallback() {
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
                }else {
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                    Utility.dismissDialoge();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
                Utility.dismissDialoge();
            }
        });




    }

}