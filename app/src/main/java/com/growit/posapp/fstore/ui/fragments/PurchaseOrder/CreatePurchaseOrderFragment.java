package com.growit.posapp.fstore.ui.fragments.PurchaseOrder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.growit.posapp.fstore.adapters.AttributeSpinnerAdapter;
import com.growit.posapp.fstore.adapters.CropAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.ProductListAdapter;
import com.growit.posapp.fstore.adapters.PurchaseItemListAdapter;
import com.growit.posapp.fstore.adapters.PurchaseProductListAdapter;
import com.growit.posapp.fstore.databinding.FragmentCreatePurchaseOrderBinding;
import com.growit.posapp.fstore.db.AppDatabase;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ItemClickListener;
import com.growit.posapp.fstore.model.Attribute;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.ProductVariantQuantity;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.Purchase.PurchaseProductModel;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.tables.PurchaseOrder;
import com.growit.posapp.fstore.ui.fragments.Setting.ConfirmOrderFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;
import com.squareup.picasso.Picasso;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreatePurchaseOrderFragment extends Fragment {
    FragmentCreatePurchaseOrderBinding binding;
    Activity contexts;
    List<Value> cropList = new ArrayList<>();
    ProductListAdapter productListAdapter;
    protected List<Product> productList = new ArrayList<>();
    private String cropID = "";
    private String cropName = "";
    CropAdapter cropAdapter = null;
    String vendor_id = "";
    String ware_house_id = "";

    String crop_id, crop_name;
    private double quantity = 1.0;
    PurchaseModel model;
    List<PurchaseProductModel> purchaseProductModel;
    PurchaseProductListAdapter adapter;
    private LinearLayout containerLL;
    List<Attribute> attributes;
    List<ProductVariantQuantity> productVariantQuantities;
    List<Value> value;
    Spinner spinner;
    String str_variant;
    String product_name, product_image,str_batch_no,str_cir_no,str_mfd,str_mfd_by;
    double basePrice = 0.0;
    int patternType = -1;
    //  String[] variantArray = null;
    ArrayList<String> variantArray = new ArrayList<>();
    Map<String, String> variant_value = new HashMap<>();
    StringBuilder stringBuilder;
    JSONArray prjsonArray;
    JSONObject productOBJ;
    String variants;
    double product_list_price = 0.0;
    int product_qty = 1;
    int product_id;
    int taxes_id,taxes_name;

    private int addGSTValue = 0;

    private int total_quant = 1;
    List<StateModel> vendorNames = new ArrayList<>();
    List<StateModel> ware_houseNames = new ArrayList<>();

    List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
    PurchaseItemListAdapter purchaseItemListAdapter;
    Spinner cstSpinner;
    int str_variant_id;
    DatePickerDialog.OnDateSetListener date_mfd;
    final Calendar myCalendar = Calendar.getInstance();
    String purchase_order;
    SessionManagement sm;
    int interstateID;
    public CreatePurchaseOrderFragment() {
        // Required empty public constructor
    }

    public static CreatePurchaseOrderFragment newInstance() {
        return new CreatePurchaseOrderFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_create_purchase_order, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_purchase_order, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        sm = new SessionManagement(getActivity());
        if (Utility.isNetworkAvailable(getActivity())) {
            getVendorList();
            getOperationTypes();
            GetTasks gt = new GetTasks();
            gt.execute();

            getCropRequest();
        } else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
        binding.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ware_house_id.length() == 0) {
                    Toast.makeText(getContext(), "Select Ware House Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (vendor_id.length() == 0) {
                    Toast.makeText(getContext(), "Select Vendor Name", Toast.LENGTH_SHORT).show();
                    return;

                }
                    if(prjsonArray !=null) {
                        CreatePurchaseOrder(vendor_id, prjsonArray.toString());
                    }else {
                        Toast.makeText(getContext(), "Select Items", Toast.LENGTH_SHORT).show();

                    }
            }
        });
        binding.croplistView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), binding.croplistView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        binding.idLLContainer.removeAllViews();
                        variant_value.clear();
                        variantArray.clear();
                        // binding.numberPicker.setValue(1);
                        //   binding.setPatternsTxt.setText("");
                        //    binding.detailLayout.removeAllViews();
                        cropID = cropList.get(position).getValueId() + "";
                        cropName = cropList.get(position).getValueName();
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        cropAdapter = new CropAdapter(getActivity(), cropList, position);
                        binding.croplistView.setAdapter(cropAdapter);
                        binding.croplistView.setLayoutManager(layoutManager);
                        getProductList(cropID);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        binding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), binding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        binding.cardDetail.setVisibility(View.VISIBLE);
                        binding.idLLContainer.removeAllViews();
                        variant_value.clear();
                        variantArray.clear();
                        binding.numberPicker.setValue(1);
                        binding.uomText.setText("");
                        binding.itemPriceTxt.setText("");
                        //  binding.detailLayout.removeAllViews();
                        binding.productImage.setVisibility(View.VISIBLE);
                        product_id = purchaseProductModel.get(position).getProductId();
                        product_name = purchaseProductModel.get(position).getProductName();
                        product_image = purchaseProductModel.get(position).getImageUrl();
                        //  product_list_price = purchaseProductModel.get(position).getListPrice();
                        attributes = purchaseProductModel.get(position).getAttributes();
                        productVariantQuantities = purchaseProductModel.get(position).getProductVariantQuantities();

                        binding.productName.setText(product_name);
                        taxes_id = purchaseProductModel.get(position).getTaxesId();

                        taxes_name = Integer.parseInt(purchaseProductModel.get(position).getTaxes_name().replaceAll("[^0-9]+", ""));
                        // Log.d("taxes_id",taxes_id+"");
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new GetGSTValueTasks().execute();
                        createDynamicSpinner(attributes.size(), product_name);
                        binding.uomText.setText(purchaseProductModel.get(position).getUomPoId());
                        binding.productName.setText(purchaseProductModel.get(position).getProductName());
                        binding.itemPriceTxt.setText(String.valueOf(purchaseProductModel.get(position).getListPrice()));
                        Picasso.with(getActivity()).load(ApiConstants.BASE_URL + purchaseProductModel.get(position).getImageUrl())
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.no_image)
                                .into(binding.productImage);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


//        binding.decre.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Double tprice=0.0;
//                Increment();
//                binding.quantityText.setText(""+product_qty);
//                tprice= product_list_price * Integer.valueOf(product_qty);
//                binding.priceTotal.setText(""+tprice);
//
//            }
//        });
//        binding.itemPriceTxt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                product_list_price= Double.parseDouble(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

//        binding.incre.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Double tprice=0.0;
//                Deccrement();
//                binding.quantityText.setText(""+product_qty);
//                tprice= product_list_price * Integer.valueOf(product_qty);
//                binding.priceTotal.setText(""+tprice);
//
//            }
//        });
        binding.numberPicker.setMin(1);
        binding.numberPicker.setUnit(1);
        binding.numberPicker.setValue(1);
//        binding.numberPicker.setVisibility(View.GONE);
//        binding.submitBtn.setVisibility(View.GONE);
//        binding.proCurrText.setVisibility(View.GONE);
        binding.mfdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(),date_mfd, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        date_mfd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mfdLabel();
            }
        };

        binding.numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                quantity = value;
                product_list_price = Double.parseDouble(binding.itemPriceTxt.getText().toString());
                //  DecimalFormat form = new DecimalFormat("0.00");
                // binding.mrpPriceTxt.setText("₹ " + String.valueOf(product_list_price * quantity));
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                product_list_price = Double.parseDouble(binding.itemPriceTxt.getText().toString());

//                variants = binding.setPatternsTxt.getText().toString().trim();
                String var_pr_name = binding.setPatternsTxt.getText().toString();
                 str_batch_no = binding.batchNumber.getText().toString();
                 str_cir_no = binding.cirNo.getText().toString();
                 str_mfd = binding.mfdDate.getText().toString();
                str_mfd_by = binding.mkdBy.getText().toString();

                if(str_batch_no.length() == 0){
                    Toast.makeText(getActivity(), "Enter the Batch Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(str_cir_no.length() == 0){
                    Toast.makeText(getActivity(), "Enter the CIR Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(str_mfd.length() == 0){
                    Toast.makeText(getActivity(), "Enter the Manufacturing date ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(str_mfd_by.length() == 0){
                    Toast.makeText(getActivity(), "Enter the MFD By", Toast.LENGTH_SHORT).show();
                    return;
                }

                variants = var_pr_name.replaceAll("\\s", "");
                //  String product_quantity = binding.quantityText.getText().toString().trim();
                boolean empty = true;
                if (variantArray != null) {
                    for (int i = 0; i < variantArray.size(); i++) {
                        if (variantArray.get(i) == null) {
                            empty = false;
                            break;
                        }
                    }
                    if (!empty) {
                        Toast.makeText(getActivity(), R.string.Add_Variants, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PurchaseOrder order = new PurchaseOrder();
                    order.setProductID(product_id);
                    order.setProductImage(product_image);
                    order.setVariantID(str_variant_id);
                    order.setProductName(product_name);
                    order.setBatch_number(str_batch_no);
                    order.setCir_number(str_cir_no);
                    order.setMfd_date(str_mfd);
                    order.setMkd_by(str_mfd_by);
                    order.setCropID(Integer.parseInt(cropID));
                    order.setCrop_name(cropName);
                    order.setQuantity(quantity);
                    order.setTotalQuantity(total_quant);
                    order.setTaxName(taxes_name);
                    order.setTaxID(taxes_id);
                    order.setUnitPrice(product_list_price);
                    order.setGst(addGSTValue);

                    if (variants != null) {
                        order.setProductVariants(variants);
                        AsyncTask.execute(() -> {
                            int prodCount = 0;
                            prodCount = DatabaseClient.getInstance(getActivity()).getAppDatabase().purchaseDao().getProductDetailById(order.getProductID(), order.getProductVariants(),order.getUnitPrice());
                            if (prodCount > 0) {
                                DatabaseClient.getInstance(getActivity()).getAppDatabase().purchaseDao().updateProductQuantity((int) order.getQuantity(), order.getProductID(),order.getProductVariants(),order.getUnitPrice());
                                GetTasks gt = new GetTasks();
                                gt.execute();

                            } else {
                                DatabaseClient.getInstance(getActivity()).getAppDatabase().purchaseDao().insert(order);
                                GetTasks gt = new GetTasks();
                                gt.execute();

                            }
                        });
                    }

                    //Toast.makeText(getActivity(), R.string.Add_card, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.No_Variants, Toast.LENGTH_SHORT).show();

                }


            }
        });
        binding.cstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    vendor_id = vendorNames.get(position).getId() + "";
                    binding.gstNo.setText(vendorNames.get(position).getGST_NO());
                    binding.stateText.setText(vendorNames.get(position).getState());
                    String state_id=vendorNames.get(position).getState_id();
                    if(state_id.equals(String.valueOf(sm.getWarehosueState()))) {
                        interstateID=sm.getIntraStateId();
                    }else{
                        interstateID=sm.getInterStateId();
                    }
                    //  binding.customerTxt.setText(vendorNames.get(position).getName());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.warehousesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    ware_house_id = ware_houseNames.get(position).getId() + "";
                    //  binding.customerTxt.setText(vendorNames.get(position).getName());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void mfdLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        str_mfd = sdf.format(myCalendar.getTime());
        binding.mfdDate.setText(str_mfd);
    }

    class GetGSTValueTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            addGSTValue = DatabaseClient.getInstance(getActivity()).getAppDatabase().gstDao().getGSTValueById(taxes_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void tasks) {
            super.onPostExecute(tasks);
//            createLayoutDynamically(productDetail.getData().get(0).getAttributes().size());
        }
    }
    //    private void Increment(){
//        product_qty++;
//    }
//    private void Deccrement(){
//        if (product_qty==1)
//            return;
//        else {
//            product_qty--;
//        }
//    }


    private void createDynamicSpinner(int n, String product_name) {
        for (int j = 0; j < n; j++) {
            LinearLayout.LayoutParams txtLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtLayoutParam.gravity = Gravity.START;
            LinearLayout.LayoutParams spinnerLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            spinnerLayoutParam.gravity = Gravity.CENTER;

            TextView headingTV = new TextView(getActivity());
//            headingTV.setTextColor(R.color.text_color);
            headingTV.setPadding(50, 20, 50, 20);
            headingTV.setText("Select "+attributes.get(j).getAttributeName());

            value = new ArrayList<>();
            String value_name = null;
            for (int i = 0; i < attributes.get(j).getValues().size(); i++) {
                Value value1 = new Value();
                value1.setValueId(attributes.get(j).getValues().get(i).getValueId());
                value1.setValueName(attributes.get(j).getValues().get(i).getValueName());
                //  value_name= attributes.get(j).getValues().get(i).getValueName();
                value.add(value1);
            }
            headingTV.setTextSize(14f);
            headingTV.setTextColor(getResources().getColor(R.color.text_color));
//            headingTV.setTypeface(Typeface.DEFAULT_BOLD);
//            headingTV.setFontFeatureSettings();
            headingTV.setPadding(50, 20, 50, 20);
            headingTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            headingTV.setLayoutParams(txtLayoutParam);
            spinner = new Spinner(getActivity());
            spinner.setPadding(50, 20, 50, 20);
//            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_size));
 //           spinner.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.red));
//           spinner.setBackgroundColor(R.drawable.spinner_bg);
            spinner.setLayoutParams(spinnerLayoutParam);
            spinner.setId(j);
            binding.idLLContainer.addView(headingTV);
            binding.idLLContainer.addView(spinner);

            getSpinner(spinner, spinner.getId(), value, product_name);
        }

    }


    private void getSpinner(Spinner spinner, int spinner_id, List<Value> value, String product_name) {
        AttributeSpinnerAdapter adapter = new AttributeSpinnerAdapter(getActivity(), value);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if(position != 0) {
                //   variantArray.add(value.get(position).getValueName());
                //  val(position);
                if (!variantArray.contains(String.valueOf(spinner_id))) {
                    variantArray.add(String.valueOf(spinner_id));

                }
                variant_value.put(String.valueOf(spinner_id), value.get(position).getValueName());
                //   }

                stringBuilder = new StringBuilder();
                for (int i = 0; i < variantArray.size(); i++) {
                    stringBuilder.append(variant_value.get(variantArray.get(i)));
                    if (i != variantArray.size() - 1) {
                        stringBuilder.append(", ");
                    }

                }
                binding.setPatternsTxt.setText(product_name + " (" + stringBuilder + ")");
                String var_pr_name = binding.setPatternsTxt.getText().toString();
                for (int j = 0; j < productVariantQuantities.size(); j++) {
                    //  String variantDisplay_name = productVariantQuantities.get(j).getVariantDisplayName();

                    Log.d("var_product_name", var_pr_name + "");

                    for (ProductVariantQuantity variantDisplay_name : productVariantQuantities) {
                        if (variantDisplay_name.getVariantDisplayName().contains(var_pr_name)) {
                            total_quant = variantDisplay_name.getQuantity();
                            str_variant_id = Integer.parseInt(variantDisplay_name.getVariant_id());
                            Log.d("ss_variant", str_variant_id + "");
                        }
                    }
                }



                // }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    private void getProductList(String pos_category_id) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + pos_category_id + "&" + "token=" + sm.getJWTToken();
        Log.d("product_list", url);
        Utility.showDialoge("Please wait while a moment...", getActivity());
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
                        Utility.dismissDialoge();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<PurchaseModel>() {
                        }.getType();
                        model = gson.fromJson(response.toString(), listType);

                        for (int i = 0; i < model.getData().size(); i++) {
                            crop_id = String.valueOf(model.getData().get(i).getCategoryId());
                            crop_name = model.getData().get(i).getCategoryName();
                            purchaseProductModel = model.getData().get(i).getProducts();
                        }
                        if (purchaseProductModel == null || purchaseProductModel.size() == 0) {
                            binding.noDataFound.setVisibility(View.VISIBLE);
                            binding.recyclerView.setVisibility(View.GONE);
                            //  Toast.makeText(contexts, "Data not Found", Toast.LENGTH_SHORT).show();
                        } else {
                            binding.noDataFound.setVisibility(View.GONE);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            adapter = new PurchaseProductListAdapter(getActivity(), purchaseProductModel);
                            binding.recyclerView.setAdapter(adapter);
                            binding.recyclerView.setLayoutManager(layoutManager);

                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }

    private void getCropRequest() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_CROPS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Utility.showDialoge("Please wait while a moment...", getActivity());
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
                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("data");
                        cropList = new ArrayList<>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Value cropPattern = new Value();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer cropID = data.optInt("category_id");
                                String name = data.optString("name");
                                String image_url = data.optString("image_url");
                                cropPattern.setValueId(Integer.valueOf(cropID + ""));
                                cropPattern.setValueName(name);
                                cropPattern.setImage_url(image_url);
                                cropPattern.setSelectedPosition(0);
                                cropList.add(cropPattern);
                            }
                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            cropAdapter = new CropAdapter(getActivity(), cropList, -1);
                            binding.croplistView.setAdapter(cropAdapter);
                            binding.croplistView.setLayoutManager(layoutManager);
                            cropID = cropList.get(0).getValueId() + "";
                            cropName = cropList.get(0).getValueName();
                            getProductList(cropID);
                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
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

            if (tasks != null && tasks.size() > 0) {
                binding.layPurOrdList.setVisibility(View.VISIBLE);
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

            }
        }
    }

    private void setBillPanel(int size) {
        double sumTotalAmount = 0.0;
        double taxesTotalAmount = 0.0;
        double lineDiscountAmount = 0.0;
        double totalDiscount = 0.0;
        prjsonArray = new JSONArray();
        for (int i = 0; i < size; i++) {
            sumTotalAmount += purchaseOrderList.get(i).getUnitPrice() * purchaseOrderList.get(i).getQuantity();
            double lineAmount = purchaseOrderList.get(i).getUnitPrice() * purchaseOrderList.get(i).getQuantity();
            lineDiscountAmount = lineAmount * purchaseOrderList.get(i).getTaxName() / 100;
            double amountAfterLineDiscount = lineAmount - lineDiscountAmount;
            totalDiscount += amountAfterLineDiscount;
            try {
                binding.textTaxes.setText("Total Tax Amount ");
                productOBJ = new JSONObject();
                productOBJ.putOpt("product_id", purchaseOrderList.get(i).getProductID());
                productOBJ.putOpt("name", purchaseOrderList.get(i).getProductVariants());
                productOBJ.putOpt("variant_id", purchaseOrderList.get(i).getVariantID());
                productOBJ.putOpt("batch_number", purchaseOrderList.get(i).getBatch_number());
                productOBJ.putOpt("cir_number", purchaseOrderList.get(i).getCir_number());
                productOBJ.putOpt("mfd_date", purchaseOrderList.get(i).getMfd_date());
                productOBJ.putOpt("mkd_by", purchaseOrderList.get(i).getMkd_by());
//              productOBJ.putOpt("name", purchaseOrderList.get(i).getProductName() + purchaseOrderList.get(i).getProductVariants());
                productOBJ.putOpt("price_unit", purchaseOrderList.get(i).getUnitPrice());
                //productOBJ.putOpt("price_unit", lineAmount);
                productOBJ.putOpt("product_qty", purchaseOrderList.get(i).getQuantity());
                productOBJ.putOpt("taxes_id", purchaseOrderList.get(i).getTaxID());
                prjsonArray.put(productOBJ);
            } catch (JSONException e) {

            }

        }
        double paid_amount = sumTotalAmount - totalDiscount;
        binding.txtAmount.setText("₹ " + String.valueOf(paid_amount));
        binding.amountTxt.setText("₹ " + String.valueOf(sumTotalAmount));
        binding.itemCountTxt.setText(String.valueOf(size));
        binding.payAmount.setText("₹" + String.valueOf(sumTotalAmount+paid_amount));
    }

    private void callOrderConfirmFragment(String  purchase_order) {
        Bundle bundle = new Bundle();
        bundle.putString("purchase_order", purchase_order);
        Fragment fragment = ConfirmOrderFragment.newInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    private void CreatePurchaseOrder(String str_vendor_id,String prjsonArray) {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
         params.put("user_id", sm.getUserID()+ "");
           params.put("token", sm.getJWTToken());
        params.put("vendor_id", str_vendor_id);
        params.put("company_id", sm.getCompanyID()+"");
        params.put("products", prjsonArray);
        params.put("fiscal_position_id", interstateID+"");
        params.put("picking_type_id",ware_house_id);
        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("Pur_create_order", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_CREATE_PURCHASE_ORDER, new VolleyCallback() {


            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                String message = obj.optString("message");
                 purchase_order = obj.optString("purchase_order");
                String error_message = obj.optString("error_message");

                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    AsyncTask.execute(() -> {
                        DatabaseClient.getInstance(getActivity()).getAppDatabase()
                                .purchaseDao()
                                .delete();
                    });
                    callOrderConfirmFragment( purchase_order);

                    Utility.dismissDialoge();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
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

    //    void showDialogeCustomer() {
//        Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.customer_dialoge);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.setCancelable(false);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
//
//        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
//        radioGroup.setVisibility(View.GONE);
//        TextView okay_text = dialog.findViewById(R.id.ok_text);
//        TextView cancel_text = dialog.findViewById(R.id.cancel_text);
//        cstSpinner = dialog.findViewById(R.id.cstSpinner);
//        TextView  text_list= dialog.findViewById(R.id.text_list);
//        text_list.setText("Vendor List");
//        cstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) {
//                    vendor_id = vendorNames.get(position).getId()+"";
//                    binding.customerTxt.setText(vendorNames.get(position).getName());
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        okay_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//
//            }
//        });
//
//        cancel_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }
    private void getVendorList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken() + "&" + "active=" + "true";
        // String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.v("url", url);
        //   Utility.showDialoge("Please wait while a moment...", getActivity());
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
//                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("vendors");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("Select Vendor");
                        vendorNames.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            Boolean active = data.optBoolean("active");
                            if (active == true) {
                                int id = data.optInt("vendor_id");
                                String name = data.optString("name");
                                String vat = data.optString("vat");
                                String state_name = data.optString("state_name");
                                String state_id = data.optString("state_id");
                                stateModel.setId(id);
                                stateModel.setName(name);
                                stateModel.setGST_NO(vat);
                                stateModel.setState(state_name);
                                stateModel.setState_id(state_id);
                                vendorNames.add(stateModel);
                            }
                        }
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), vendorNames);
                            binding.cstSpinner.setAdapter(adapter);
                        }

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }
    private void getOperationTypes() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_OPERATION_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
    //    String url = ApiConstants.BASE_URL + ApiConstants.GET_OPERATION_LIST;

        Log.v("url", url);
        //   Utility.showDialoge("Please wait while a moment...", getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                ware_houseNames = new ArrayList<>();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (status.equalsIgnoreCase("success")) {
//                        Utility.dismissDialoge();
                        JSONArray jsonArray = obj.getJSONArray("operation_types");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("Select warehouse");
                        ware_houseNames.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            String name = data.optString("name");
                            if (name.equalsIgnoreCase("Receipts")) {
                                int id = data.optInt("id");
                                String warehouse_id = data.optString("warehouse_id");
                                stateModel.setId(id);
                                stateModel.setName(warehouse_id);
                                ware_houseNames.add(stateModel);
                            }
                        }
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), ware_houseNames);
                            binding.warehousesSpinner.setAdapter(adapter);
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