package com.growit.posapp.fstore.ui.fragments.PurchaseOrder;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.PurchaseItemListAdapter;
import com.growit.posapp.fstore.databinding.FragmentPurchaseItemCartBinding;
import com.growit.posapp.fstore.db.AppDatabase;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ItemClickListener;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.tables.PurchaseOrder;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PurchaseItemCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PurchaseItemCartFragment extends Fragment {

    FragmentPurchaseItemCartBinding binding;
    List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
    PurchaseItemListAdapter purchaseItemListAdapter;
    List<StateModel> vendorNames = new ArrayList<>();
    String  vendor_id;
    Spinner cstSpinner;
    JSONArray prjsonArray;
    JSONObject productOBJ;
    public PurchaseItemCartFragment() {
        // Required empty public constructor
    }
    public static PurchaseItemCartFragment newInstance() {
        return new PurchaseItemCartFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchase_item_cart, container, false);
        init();
        return binding.getRoot();
    }

    private void init(){
        if (Utility.isNetworkAvailable(getActivity())) {
            getVendorList();
            GetTasks gt = new GetTasks();
            gt.execute();
            showDialogeCustomer();
        } else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
    }
    class GetTasks extends AsyncTask<Void, Void, List<PurchaseOrder>> {

        @Override
        protected List<PurchaseOrder> doInBackground(Void... voids) {
            AppDatabase dbClient = DatabaseClient.getInstance(getActivity()).getAppDatabase();
            purchaseOrderList = dbClient.purchaseDao().getPurchaseOrder();
            if (purchaseOrderList == null || purchaseOrderList.size() == 0) {
                return null;
            }

            return purchaseOrderList;
        }

        @Override
        protected void onPostExecute(List<PurchaseOrder> tasks) {
            super.onPostExecute(tasks);
            // tasks_model = tasks;
            Log.d("task",tasks.size()+"");
            if (tasks != null && tasks.size() > 0) {
                    setBillPanel(tasks.size());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                purchaseItemListAdapter = new PurchaseItemListAdapter(getActivity(), tasks);
                purchaseItemListAdapter.setOnClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(int position) {
                          setBillPanel(tasks.size());
                    }
                });
                binding.itemList.setAdapter(purchaseItemListAdapter);
                binding.itemList.setLayoutManager(layoutManager);

                //        showDialogeCustomer();
            }
        }
    }
private void setBillPanel(int size){
    double sumTotalAmount = 0.0;
    prjsonArray = new JSONArray();
    for (int i = 0; i < size; i++) {
        sumTotalAmount += purchaseOrderList.get(i).getUnitPrice() * purchaseOrderList.get(i).getQuantity();

        try {
            productOBJ = new JSONObject();
            productOBJ.putOpt("product_id", purchaseOrderList.get(i).getProductID());
            productOBJ.putOpt("name", purchaseOrderList.get(i).getProductVariants());

//            productOBJ.putOpt("name", purchaseOrderList.get(i).getProductName() + purchaseOrderList.get(i).getProductVariants());
            productOBJ.putOpt("price_unit", purchaseOrderList.get(i).getUnitPrice());
            productOBJ.putOpt("product_qty", purchaseOrderList.get(i).getQuantity());
            productOBJ.putOpt("taxes_id", purchaseOrderList.get(i).getTaxID());
            prjsonArray.put(productOBJ);
        } catch (JSONException e) {

        }

    }
}
    private void CreatePurchaseOrder(){
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        // params.put("user_id", sm.getUserID()+ "");
        //   params.put("token", sm.getJWTToken());
       params.put("vendor_id", vendor_id);
        params.put("products", prjsonArray.toString());
        params.put("picking_type_id", 8+"");
        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("Pur_create_order", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CREATE_PURCHASE_ORDER, new VolleyCallback() {


            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String  status = obj.optString("status");
                String  message = obj.optString("message");
                String  error_message = obj.optString("error_message");

                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }else {
                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });

    }
    void showDialogeCustomer() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.customer_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        radioGroup.setVisibility(View.GONE);
        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);
        cstSpinner = dialog.findViewById(R.id.cstSpinner);
        TextView  text_list= dialog.findViewById(R.id.text_list);
        text_list.setText("Vendor List");
        cstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    vendor_id = vendorNames.get(position).getId()+"";
                    binding.customerTxt.setText(vendorNames.get(position).getName());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    private void getVendorList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken() + "&" + "active=" + "true";

        // String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
        Utility.showDialoge("Please wait while a moment...", getActivity());
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
                        Utility.dismissDialoge();
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
                           // Boolean active = data.optBoolean("active");
                            stateModel.setId(id);
                            stateModel.setName(name);
                            vendorNames.add(stateModel);
                        }
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), vendorNames);
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

}