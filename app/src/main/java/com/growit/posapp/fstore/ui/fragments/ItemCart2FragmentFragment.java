package com.growit.posapp.fstore.ui.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.PaymentActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.CustomerArrayAdapter;
import com.growit.posapp.fstore.adapters.ItemListAdapter;
import com.growit.posapp.fstore.db.AppDatabase;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ItemClickListener;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.tables.Customer;
import com.growit.posapp.fstore.tables.PosOrder;
import com.growit.posapp.fstore.ui.fragments.Setting.ConfirmOrderFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ItemCart2FragmentFragment extends Fragment {
    boolean ifFreeProduct = false;
    SessionManagement sm;
    RecyclerView recyclerView;
    RadioGroup radio_group_pay;
    private MyBroadcastReceiver receiver;
    ItemListAdapter productListAdapter;
    List<PosOrder> posOrderList = new ArrayList<>();
    JSONArray prjsonArray;
    JSONObject productOBJ;
    String str_pay_type;
    double subTotal_50;
    String paymentMode = "-1";
    double paidAmount = 0.0;
    TextView itemCountTxt,gstLabelTxt,totalAmount,gstTxt,paidTxt,subTotalTxt,lineDiscountTxt;
    Button orderBtn;
    TextView customerTxt;
    String customer_id;
    List<StateModel> CustomerNames = new ArrayList<>();
    public ItemCart2FragmentFragment() {
        // Required empty public constructor
    }


    public static ItemCart2FragmentFragment newInstance() {
        ItemCart2FragmentFragment fragment = new ItemCart2FragmentFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_cart2_fragment, container, false);
        recyclerView = view.findViewById(R.id.itemList);
        radio_group_pay = view.findViewById(R.id.radioGroup_pay);
        itemCountTxt = view.findViewById(R.id.itemCountTxt);
        gstLabelTxt = view.findViewById(R.id.gstLabelTxt);
        orderBtn = view.findViewById(R.id.orderBtn);
        totalAmount = view.findViewById(R.id.amountTxt);
        gstTxt = view.findViewById(R.id.gstTxt);
        customerTxt = view.findViewById(R.id.customerTxt);
        paidTxt = view.findViewById(R.id.paidTxt);
        subTotalTxt = view.findViewById(R.id.subTotalTxt);
        lineDiscountTxt = view.findViewById(R.id.lineDiscountTxt);
        receiver = new MyBroadcastReceiver();
        getActivity().registerReceiver(receiver, new IntentFilter(ApiConstants.ACTION_PAYMENT));
        sm = new SessionManagement(getActivity());
        if (Utility.isNetworkAvailable(getActivity())) {

            getCartItems();
            showDialogeCustomer();
        } else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        radio_group_pay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb = view.findViewById(checkedId);
                if (rb.getText().toString().equalsIgnoreCase("Cash")) {
                    str_pay_type = "Cash";
                    paymentMode = "1";
                } else if (rb.getText().toString().equalsIgnoreCase("Online")) {
                    str_pay_type = "Online";
                    paymentMode = "2";
                }
            }
        });
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_pay_type != null) {
                    if (str_pay_type != null && str_pay_type.equals("Cash")) {
                        try {
                            createPosOrderRequest(paymentMode, "");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (str_pay_type != null && str_pay_type.equals("Online")) {
                        if (!Utility.isNetworkAvailable(getContext())) {
                            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                        }

                        Intent profileIntent = new Intent(getActivity(), PaymentActivity.class);
                        profileIntent.putExtra("amount", paidAmount);
                        startActivity(profileIntent);
                    }

                } else {
                    Toast.makeText(getContext(), R.string.PAYMENT_METHOD, Toast.LENGTH_SHORT).show();

                }
            }
        });

        return view;
    }


    private void getCartItems() {

        if (Utility.isNetworkAvailable(getActivity())) {

        } else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        class GetTasks extends AsyncTask<Void, Void, List<PosOrder>> {

            @Override
            protected List<PosOrder> doInBackground(Void... voids) {
                AppDatabase dbClient = DatabaseClient.getInstance(getActivity()).getAppDatabase();
                posOrderList = dbClient.productDao().getOrder();
                if (posOrderList == null || posOrderList.size() == 0) {
                    return null;
                }

                return posOrderList;
            }

            @Override
            protected void onPostExecute(List<PosOrder> tasks) {
                super.onPostExecute(tasks);
                // tasks_model = tasks;

                if (tasks != null && tasks.size() > 0) {
                    setBillPanel(tasks.size());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    productListAdapter = new ItemListAdapter(getActivity(), tasks);
                    productListAdapter.setOnClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            setBillPanel(tasks.size());
                        }
                    });
                    recyclerView.setAdapter(productListAdapter);
                    recyclerView.setLayoutManager(layoutManager);


                    //        showDialogeCustomer();
                }
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String test = intent.getStringExtra("dataToPass");
            if (test.equalsIgnoreCase("4")) {
                String transactionid = intent.getStringExtra("transactionid");
                try {
                    if (!Utility.isNetworkAvailable(getContext())) {
                        Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    createPosOrderRequest(paymentMode, transactionid);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void setBillPanel(int size) {
        double sumTotalAmount = 0.0;
        double lineDiscountAmount = 0.0;
        double totalDiscount = 0.0;
        double gstAmount = 0.0;
        double extradiscount = 0.0;
        double discount=0.0;
        prjsonArray = new JSONArray();

        for (int i = 0; i < size; i++) {
            sumTotalAmount += posOrderList.get(i).getUnitPrice() * posOrderList.get(i).getQuantity();
            double lineAmount = posOrderList.get(i).getUnitPrice() * posOrderList.get(i).getQuantity();
            lineDiscountAmount = lineAmount * posOrderList.get(i).getDiscount_per() / 100;

            double amountAfterLineDiscount = lineAmount - lineDiscountAmount;
            totalDiscount += lineDiscountAmount;
            double lineGstAmount = 0.0;

            posOrderList.get(i).setAmount_tax(amountAfterLineDiscount * posOrderList.get(0).getGst() / 100);

            lineGstAmount = amountAfterLineDiscount * posOrderList.get(0).getGst() / 100;
            gstAmount += lineGstAmount;
            try {
                productOBJ = new JSONObject();
//                productOBJ.putOpt("gift_card", offerDiscount);
                productOBJ.putOpt("product_id", posOrderList.get(i).getProductID());
                productOBJ.putOpt("crop_name", posOrderList.get(i).getCrop_name());
                productOBJ.putOpt("variant_id", posOrderList.get(i).getVariantID());
                productOBJ.putOpt("full_product_name", posOrderList.get(i).getProductName() + posOrderList.get(i).getProductVariants());
                productOBJ.putOpt("price_unit", posOrderList.get(i).getUnitPrice());
                productOBJ.putOpt("qty", posOrderList.get(i).getQuantity());
                productOBJ.putOpt("amount_tax", posOrderList.get(i).getAmount_tax());//total tax lineWise
                productOBJ.putOpt("price_subtotal", amountAfterLineDiscount);//amount after discount
                Log.i("amountAfterLineDiscount---", amountAfterLineDiscount + "");
                Log.i("gstAmount---", lineGstAmount + "");
                productOBJ.putOpt("price_subtotal_incl", amountAfterLineDiscount + lineGstAmount);//price_subtotal - offer discount + GST

                Log.i("price_subtotal_incl", amountAfterLineDiscount + lineGstAmount + "");
                productOBJ.putOpt("tax_ids", posOrderList.get(i).getTaxID());
                prjsonArray.put(productOBJ);
            } catch (JSONException e) {

            }

        }



        totalAmount.setText(Utility.decimalFormat(sumTotalAmount) + "");
        itemCountTxt.setText(size + "");
        lineDiscountTxt.setText("- " + Utility.decimalFormat(totalDiscount) + "");
        subTotal_50 = sumTotalAmount - (totalDiscount);
        paidAmount = subTotal_50 + gstAmount;
        subTotalTxt.setText(Utility.decimalFormat(subTotal_50) + "");
        paidTxt.setText(Utility.decimalFormat(paidAmount) + "");
        gstTxt.setText("+ " + Utility.decimalFormat(gstAmount) + "");



    }

    private void createPosOrderRequest(String payment_method_id, String transactionid) throws JSONException {
        JSONArray paymentLineJsonArray = new JSONArray();
        JSONObject paymentOBJ = new JSONObject();
        paymentOBJ.put("amount", paidAmount);
        if (payment_method_id.equalsIgnoreCase("2")) {
            paymentOBJ.put("transaction_id", transactionid);
        }
//        Log.v("paid_amount", String.valueOf(paidAmount));
        paymentOBJ.put("payment_method_id", payment_method_id);// cash 1 bank 2
        paymentLineJsonArray.put(paymentOBJ);
        Log.v("paymentOBJ", String.valueOf(paymentOBJ));
        Utility.showDialoge("Please wait while a moment...", getActivity());
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("token", sm.getJWTToken());
        params.put("partner_id", customer_id);
//        params.put("partner_id", customerID);
//        params.put("coupon_code", coupanCode);
        params.put("products", prjsonArray.toString());
        params.put("payment_line", paymentLineJsonArray.toString());

        Log.d("pos_order",params.toString());
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POS_ORDER, new VolleyCallback() {
            private String message = "Order failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response POS Order", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String session_id = obj.optString("session_id");
                sm.saveSessionID(session_id);
                ifFreeProduct = false;
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    AsyncTask.execute(() -> {
                        DatabaseClient.getInstance(getActivity()).getAppDatabase()
                                .productDao()
                                .delete();
                    });
                    Utility.dismissDialoge();
                    callOrderConfirmFragment();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
//                Log.v("Response POS Order", result);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void callOrderConfirmFragment() {
        Fragment fragment = ConfirmOrderFragment.newInstance();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }
    private void getCustomerList(Spinner cstSpinner) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_CUSTOMER + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

        // String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
        //   Utility.showDialoge("Please wait while a moment...", getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                CustomerNames = new ArrayList<>();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("data");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("Select Contact");
                        CustomerNames.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            int id = data.optInt("customer_id");
                            String name = data.optString("name");
                            stateModel.setId(id);
                            stateModel.setName(name);
                            CustomerNames.add(stateModel);

                        }
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), CustomerNames);
                           cstSpinner.setAdapter(adapter);
                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }
    void showDialogeCustomer() {

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.customer_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);
       Spinner cstSpinner = dialog.findViewById(R.id.cstSpinner);
        getCustomerList(cstSpinner);
        cstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    customer_id = CustomerNames.get(position).getId() + "";
                    customerTxt.setText(CustomerNames.get(position).getName());

//                    getCartItems();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getCartItems();
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

}