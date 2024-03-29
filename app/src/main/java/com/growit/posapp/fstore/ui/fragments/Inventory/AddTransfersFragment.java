package com.growit.posapp.fstore.ui.fragments.Inventory;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.AttributeSpinnerAdapter;
import com.growit.posapp.fstore.adapters.CropAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.ProductListAdapter;
import com.growit.posapp.fstore.adapters.PurchaseProductListAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddTranferBinding;
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
import com.growit.posapp.fstore.tables.TransferOrder;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddTransfersFragment extends Fragment {

    FragmentAddTranferBinding binding;
    Activity contexts;
    List<Value> cropList = new ArrayList<>();
    ProductListAdapter productListAdapter;
    protected List<Product> productList = new ArrayList<>();
    private String cropID = "";
    private String cropName = "";
    CropAdapter cropAdapter = null;
    String customer_id = "";
    String operation_type_id = "";
    String location_id = "";
    String dis_location_id = "";

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
    String product_name, product_image;
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
    List<StateModel> CustomerNames = new ArrayList<>();
    List<StateModel> ware_houseNames = new ArrayList<>();
    List<StateModel> location = new ArrayList<>();

    List<TransferOrder> purchaseOrderList = new ArrayList<>();
    TransferItemListAdapter purchaseItemListAdapter;
    Spinner cstSpinner;
    String str_internal_transfer;
    int str_variant_id;

    public AddTransfersFragment() {
        // Required empty public constructor
    }

    public static AddTransfersFragment newInstance() {
        return new AddTransfersFragment();
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
//         inflater.inflate(R.layout.fragment_add_tranfer, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_tranfer, container, false);
        init();
        return binding.getRoot();

    }
        private void init() {
            Glide.with(getActivity()).load(R.drawable.growit_gif_02).into(binding.gifLoader);
            binding.gifLoader.setVisibility(View.VISIBLE);
            if (Utility.isNetworkAvailable(getActivity())) {
                getCustomerList();
                getOperationTypes();
                getLocation();
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
                    if (operation_type_id.length() == 0) {
                        Toast.makeText(getContext(), "Select Operation Type", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (location_id.length() == 0) {
                        Toast.makeText(getContext(), "Select Source Location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (dis_location_id.length() == 0) {
                        Toast.makeText(getContext(), "Select Destination Location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (customer_id.length() == 0) {
                        Toast.makeText(getContext(), "Select Customer ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(prjsonArray !=null){
                        TransfersOrder(prjsonArray.toString(),customer_id,operation_type_id,location_id,dis_location_id);
                    }else {
                        Toast.makeText(getContext(), "Select Items ", Toast.LENGTH_SHORT).show();

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
            binding.numberPicker.setMin(1);
            binding.numberPicker.setUnit(1);
            binding.numberPicker.setValue(1);


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
                        TransferOrder order = new TransferOrder();
                        order.setProductID(product_id);
                        order.setProductImage(product_image);
                        order.setVariantID(str_variant_id);
                        order.setProductName(product_name);
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
                                prodCount = DatabaseClient.getInstance(getActivity()).getAppDatabase().transferDao().getProductDetailById(order.getProductID(), order.getProductVariants(),order.getUnitPrice());
                                if (prodCount > 0) {
                                    DatabaseClient.getInstance(getActivity()).getAppDatabase().transferDao().updateProductQuantity((int) order.getQuantity(), order.getProductID(),order.getProductVariants(),order.getUnitPrice());
                                   GetTasks gt = new GetTasks();
                                    gt.execute();

                                } else {
                                    DatabaseClient.getInstance(getActivity()).getAppDatabase().transferDao().insert(order);
                                    GetTasks gt = new GetTasks();
                                    gt.execute();

                                }
                            });
                        }

                        //                Toast.makeText(getActivity(), R.string.Add_card, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.No_Variants, Toast.LENGTH_SHORT).show();

                    }


                }
            });
            binding.customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        customer_id = CustomerNames.get(position).getId() + "";
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
                        operation_type_id = ware_houseNames.get(position).getId() + "";


                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            binding.locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        location_id = location.get(position).getId() + "";

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            binding.disSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        dis_location_id = location.get(position).getId() + "";

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

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
                LinearLayout.LayoutParams spinnerLayoutParam = new LinearLayout.LayoutParams(610,150);
                spinnerLayoutParam.gravity = Gravity.START;

                TextView headingTV = new TextView(getActivity());

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
                headingTV.setTextSize(15f);
                headingTV.setTextColor(getResources().getColor(R.color.spinner_color));

                headingTV.setTypeface(getResources().getFont(R.font.helvetica_neue_medium));
                headingTV.setPadding(0, 15, 12, 20);
                headingTV.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                headingTV.setLayoutParams(txtLayoutParam);
                spinner = new Spinner(getActivity());
                spinner.setPadding(0, 15, 12, 20);
                spinner.setBackground(getResources().getDrawable(R.drawable.product_size));
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
//            Utility.showDialoge("Please wait while a moment...", getActivity());
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
//                            Utility.dismissDialoge();
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
//            Utility.showDialoge("Please wait while a moment...", getActivity());
            binding.gifLoader.setVisibility(View.VISIBLE);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("Response", response.toString());
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.toString());
                        int statusCode = obj.optInt("statuscode");
                        String status = obj.optString("status");
                        binding.gifLoader.setVisibility(View.GONE);
                        if (statusCode == 200 && status.equalsIgnoreCase("success")) {
//                            Utility.dismissDialoge();
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


        class GetTasks extends AsyncTask<Void, Void, List<TransferOrder>> {

            @Override
            protected List<TransferOrder> doInBackground(Void... voids) {
                AppDatabase dbClient = DatabaseClient.getInstance(getActivity()).getAppDatabase();
                purchaseOrderList = dbClient.transferDao().getPurchaseOrder();
                if (purchaseOrderList == null || purchaseOrderList.size() == 0) {
                    return null;
                }

                return purchaseOrderList;
            }

            @Override
            protected void onPostExecute(List<TransferOrder> tasks) {
                super.onPostExecute(tasks);
                // tasks_model = tasks;

                if (tasks != null && tasks.size() > 0) {
                    setBillPanel(tasks.size());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    purchaseItemListAdapter = new TransferItemListAdapter(getActivity(), tasks);
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
//                sumTotalAmount += purchaseOrderList.get(i).getUnitPrice() * purchaseOrderList.get(i).getQuantity();
//                double lineAmount = purchaseOrderList.get(i).getUnitPrice() * purchaseOrderList.get(i).getQuantity();
//                lineDiscountAmount = lineAmount * purchaseOrderList.get(i).getTaxName() / 100;
//                double amountAfterLineDiscount = lineAmount - lineDiscountAmount;
//                totalDiscount += amountAfterLineDiscount;
                try {
//                    binding.textTaxes.setText("Total Tax Amount ");
                    productOBJ = new JSONObject();
                    productOBJ.putOpt("product_id", purchaseOrderList.get(i).getVariantID());
//                    productOBJ.putOpt("name", purchaseOrderList.get(i).getProductVariants());
                 //   productOBJ.putOpt("variant_id", purchaseOrderList.get(i).getVariantID());
//                    productOBJ.putOpt("price_unit", purchaseOrderList.get(i).getUnitPrice());
                    productOBJ.putOpt("quantity", purchaseOrderList.get(i).getQuantity());

                    prjsonArray.put(productOBJ);
                } catch (JSONException e) {

                }

            }
//            double paid_amount = sumTotalAmount - totalDiscount;
//            binding.txtAmount.setText("₹ " + String.valueOf(paid_amount));
//            binding.amountTxt.setText("₹ " + String.valueOf(sumTotalAmount));
            binding.itemCountTxt.setText(String.valueOf(size));
//            binding.payAmount.setText("₹" + String.valueOf(sumTotalAmount+paid_amount));
        }

        private void callOrderConfirmFragment(String internal_transfer) {
            Bundle bundle = new Bundle();
            bundle.putString("purchase_order", internal_transfer);
            Fragment fragment = ConfirmOrderFragment.newInstance();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        private void TransfersOrder(String prjsonArray,String str_customer_id,String dis_location_id,String source_location_id,String destination_location_id) {
            SessionManagement sm = new SessionManagement(getActivity());
            Map<String, String> params = new HashMap<>();
            params.put("user_id", sm.getUserID()+ "");
            params.put("token", sm.getJWTToken());
            params.put("partner_id", str_customer_id);
            params.put("operation_type_id", dis_location_id);
            params.put("source_location_id", source_location_id);
            params.put("destination_location_id", destination_location_id);
            params.put("products", prjsonArray);
            Utility.showDialoge("Please wait while a moment...", getActivity());

            Log.v("transfer_order", String.valueOf(params));
            new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_TRANSFER_ORDER, new VolleyCallback() {
                @Override
                public void onSuccess(Object result) throws JSONException {
                    Log.v("Response", result.toString());
                    JSONObject obj = new JSONObject(result.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                     str_internal_transfer = obj.optString("internal_transfer");
                    String message = obj.optString("message");
                    String error_message = obj.optString("error_message");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        AsyncTask.execute(() -> {
                            DatabaseClient.getInstance(getActivity()).getAppDatabase()
                                    .transferDao()
                                    .delete();
                        });
                        callOrderConfirmFragment(str_internal_transfer);

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


        private void getCustomerList() {
            SessionManagement sm = new SessionManagement(getActivity());
            RequestQueue queue = Volley.newRequestQueue(getActivity());

            String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_CUSTOMER + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

            // String url = ApiConstants.BASE_URL + ApiConstants.GET_VENDOR_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
            Log.v("url_f", url);
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
                                binding.customerSpinner.setAdapter(adapter);
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
        //String url = ApiConstants.BASE_URL + ApiConstants.GET_OPERATION_LIST;

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
                        stateModel.setName("Select Operation Types");
                        ware_houseNames.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            String name = data.optString("name");
                            if (name.equalsIgnoreCase("Internal Transfers")) {
                                int id = data.optInt("id");
                                String warehouse_id = data.optString("warehouse_id");
                                stateModel.setId(id);
                                stateModel.setName(warehouse_id+" / "+name);
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexts = getActivity();

    }

    private void getLocation() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_LOCATION_LIST+ "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();

        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_LOCATION_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
    //    Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.d("ALL_CROPS_url",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = obj.getJSONArray("data");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("Select Location");
                        location.add(stateModel);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stateModel = new StateModel();
                            JSONObject data = jsonArray.getJSONObject(i);
                            String name = data.optString("name");
                            int id = data.optInt("id");
                            stateModel.setId(id);
                            stateModel.setName(name);
                            location.add(stateModel);

                        }
                        if (getContext() != null) {
                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), location);
                            binding.locationSpinner.setAdapter(adapter);
                            binding.disSpinner.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

}