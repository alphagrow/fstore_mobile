package com.growit.posapp.fstore.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.PaymentActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.CustomerArrayAdapter;
import com.growit.posapp.fstore.adapters.FreeProductAdapter;
import com.growit.posapp.fstore.adapters.ItemListAdapter;
import com.growit.posapp.fstore.db.AppDatabase;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ItemClickListener;
import com.growit.posapp.fstore.model.DiscountListModel;
import com.growit.posapp.fstore.model.DiscountModel;
import com.growit.posapp.fstore.model.GiftCardListModel;
import com.growit.posapp.fstore.model.GiftCardModel;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.tables.Customer;
import com.growit.posapp.fstore.tables.PosOrder;
import com.growit.posapp.fstore.ui.fragments.Setting.ConfirmOrderFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemCartFragment extends Fragment {

    private RecyclerView recyclerView;
    ItemListAdapter productListAdapter;
    Spinner cstSpinner;
    private TextView removeBtn, subTotalTxt_2, subTotalTxt, customerTxt, totalAmount, itemCountTxt, paidTxt, emtyView, gstTxt, gstLabelTxt, discountLabelTxt, lineDiscountTxt;
    ProgressDialog progressDialog;
    List<Customer> customerList = new ArrayList<>();
    List<PosOrder> posOrderList = new ArrayList<>();
    private String customerID = "";
    private String customerTypeLineDiscount = "15";
    LinearLayout discountLayout, sub_total_2_lay;
    SessionManagement sm;
    private MyBroadcastReceiver receiver;
    JSONObject productOBJ = null;
    JSONArray prjsonArray = new JSONArray();
    double paidAmount = 0.0;
    GiftCardModel giftCardModel;
    DiscountModel discountModel;
    //ExtraOrderDiscount extraOrderDiscount;
    EditText couponCodeEditTxt;
    TextView applyBtn, couponCodeAmountTxt;
    RadioGroup radio_group_pay;
    String str_pay_type;
    String paymentMode = "-1";
    RecyclerView recycler_view;
    TextView free_product_button;
    LinearLayout layout_free;
    boolean ifFreeProduct = false;
    List<DiscountListModel> discountListModel = new ArrayList<>();
    double min_amount;
    double min, max;
    String str_coupon;
    String coupanCode = "";
    double subTotal_50;
    List<PosOrder> tasks_model;
    private int selectedPosition = -1;
    FreeProductAdapter freeProductAdapter;
    ProductDetail productDetail;
    String type;
    String price = "1";
    private int addGSTValue = 0;
    double offerDiscount = 0.0;
    String taxesId;
    //    double leftOff_discount = 0.0;
    TextView offerDiscountTxt;
    LinearLayout extra_discount_lay;
    ArrayList<Customer> customer_list;
    int customerType = 1;
    Button orderBtn;

    public ItemCartFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ItemCartFragment newInstance() {
        ItemCartFragment fragment = new ItemCartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_cart, container, false);
        receiver = new MyBroadcastReceiver();
        getActivity().registerReceiver(receiver, new IntentFilter(ApiConstants.ACTION_PAYMENT));
        sm = new SessionManagement(getActivity());
        extra_discount_lay = view.findViewById(R.id.extra_discount_lay);
        recyclerView = view.findViewById(R.id.itemList);
        discountLabelTxt = view.findViewById(R.id.discountLabelTxt);
        applyBtn = view.findViewById(R.id.applyBtn);
        couponCodeAmountTxt = view.findViewById(R.id.couponCodeAmountTxt);
        couponCodeEditTxt = view.findViewById(R.id.couponCodeEditTxt);
        lineDiscountTxt = view.findViewById(R.id.lineDiscountTxt);
        discountLayout = view.findViewById(R.id.discountLayout);
        subTotalTxt = view.findViewById(R.id.subTotalTxt);
        discountLayout.setVisibility(View.GONE);
        orderBtn = view.findViewById(R.id.orderBtn);
        paidTxt = view.findViewById(R.id.paidTxt);
        customerTxt = view.findViewById(R.id.customerTxt);
        itemCountTxt = view.findViewById(R.id.itemCountTxt);
        gstLabelTxt = view.findViewById(R.id.gstLabelTxt);
        gstTxt = view.findViewById(R.id.gstTxt);
        emtyView = view.findViewById(R.id.emtyView);
        totalAmount = view.findViewById(R.id.amountTxt);
        radio_group_pay = view.findViewById(R.id.radioGroup_pay);
        recycler_view = view.findViewById(R.id.recycler_view_free);
        free_product_button = view.findViewById(R.id.free_product);
        subTotalTxt_2 = view.findViewById(R.id.subTotalTxt_2);
        sub_total_2_lay = view.findViewById(R.id.sub_total_2_lay);
        offerDiscountTxt = view.findViewById(R.id.offerDiscountTxt);
        layout_free = view.findViewById(R.id.layout_free);
        removeBtn = view.findViewById(R.id.removeBtn);
        removeBtn.setVisibility(View.GONE);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (customerType == 1) {
            discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
        } else if (customerType == 2) {
            discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
        } else if (customerType == 3) {
            discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
        }

        if (Utility.isNetworkAvailable(getActivity())) {
            getTasks();
            getCartItems();
            showDialogeCustomer();
        } else {
            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

//place order button
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
        //select payment type
        //case ,online
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
        //coupon apply button

        applyBtn.setOnClickListener(v -> {

            if (Utility.isNetworkAvailable(getActivity())) {
                String s = couponCodeEditTxt.getText().toString();
                if (!s.isEmpty()) {
                    getGiftCards(s);
                } else {
                    Toast.makeText(getContext(), R.string.COUPON, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

            }


        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponCodeEditTxt.setText("");
                applyBtn.setVisibility(View.VISIBLE);
                removeBtn.setVisibility(View.GONE);
                offerDiscount = 0.0;
                setBillPanel(posOrderList.size());
            }
        });
        //click free product item
        recycler_view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        int product_id = discountListModel.get(position).getProductId();
                        int variant_id = discountListModel.get(position).getProduct_variant_id();
                        double unit_price = discountListModel.get(position).getUnit_price();
                        String product_name = discountListModel.get(position).getProductName() + "";
                        String taxesId = discountListModel.get(position).getTaxesId() + "";
                        String product_Image = discountListModel.get(position).getProductImageUrl() + "";
                        double qty = Double.parseDouble(discountListModel.get(position).getQty() + "");

//                        double discount = 0.0;
//                        if (customerType == 1) {
//                            // line discount 15
//                            discount = (1 * Integer.parseInt(customerTypeLineDiscount)) / 100;
//
//                        } else if (customerType == 2) {
//                            discount = (1 * Integer.parseInt(customerTypeLineDiscount)) / 100;
//                        } else if (customerType == 3) {
//                            discount = (1 * Integer.parseInt(customerTypeLineDiscount)) / 100;
//                        }

//

                        PosOrder model = new PosOrder();
                        model.setProductID(product_id);
                        model.setVariantID(variant_id);
                        model.setProductName(product_name);
                        model.setProductVariants("");
                        model.setProductImage(product_Image);
                        model.setUnitPrice(unit_price);
                        model.setQuantity(qty);
//                        model.setAmount_tax(tax_amount);
                        model.setTaxID(Integer.parseInt(taxesId));

                        posOrderList.add(model);
//                        tasks_model.add(model);

                        recycler_view.setVisibility(View.GONE);
                        free_product_button.setVisibility(View.GONE);
                        layout_free.setVisibility(View.GONE);
                        productListAdapter.notifyDataSetChanged();
                        if (posOrderList != null && posOrderList.size() > 0) {
                            ifFreeProduct = true;
                            setBillPanel(posOrderList.size());
                            recycler_view.setVisibility(View.GONE);
                            discountLayout.setVisibility(View.VISIBLE);
                            emtyView.setVisibility(View.GONE);
                        } else {
                            ifFreeProduct = false;
                            emtyView.setVisibility(View.VISIBLE);
                            discountLayout.setVisibility(View.GONE);
                        }
//                        if (posOrderList != null && posOrderList.size() > 0) {
//
//                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                            productListAdapter = new ItemListAdapter(getActivity(), posOrderList);
//                            productListAdapter.setOnClickListener(new ItemClickListener() {
//                                @Override
//                                public void onClick(int position) {
//                                    setBillPanel(posOrderList.size());
//                                }
//                            });
//                            recyclerView.setAdapter(productListAdapter);
//                            recyclerView.setLayoutManager(layoutManager);
//                            discountLayout.setVisibility(View.VISIBLE);
//                            emtyView.setVisibility(View.GONE);
//
//                            setBillPanel(posOrderList.size());
//                            recycler_view.setVisibility(View.GONE);
//                        } else {
//                            emtyView.setVisibility(View.VISIBLE);
//                            discountLayout.setVisibility(View.GONE);
//                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })


        );

//click free product button
        free_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (PosOrder detail : posOrderList) {
                if (ifFreeProduct) {
                    recycler_view.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.FREE_PRODUCT, Toast.LENGTH_SHORT).show();
                } else {
                    layout_free.setVisibility(View.VISIBLE);
                    free_product_button.setVisibility(View.VISIBLE);
                    recycler_view.setVisibility(View.VISIBLE);
                    discountListModel = new ArrayList<>();
                    discountListModel = discountModel.getLoyaltyRewards();
                    discountListModel.remove(0);
                    freeProductAdapter = new FreeProductAdapter(getActivity(), discountListModel);
                    recycler_view.setAdapter(freeProductAdapter);
                    recycler_view.setLayoutManager(layoutManager);
                }
//                }

            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }


    private void getTasks() {
        class GetTasks extends AsyncTask<Void, Void, List<Customer>> {

            @Override
            protected List<Customer> doInBackground(Void... voids) {
                customerList = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .customerDao()
                        .getAllCustomerByType(customerType);


                return customerList;
            }

            @Override
            protected void onPostExecute(List<Customer> tasks) {
                super.onPostExecute(tasks);
                Customer cs = new Customer();
                cs.setName("--Select Customer--");
                tasks.add(0, cs);
                customer_list = new ArrayList<>();
//                for (Customer customer : tasks) {
//                    if(customer_type!=null&&customer.getDistrict().equals(customer_type)){
//                        customer_list.add(customer);
//                        CustomerArrayAdapter adapter = new CustomerArrayAdapter(getActivity(), customer_list);
//                        cstSpinner.setAdapter(adapter);
//                    }
//                }
                CustomerArrayAdapter adapter = new CustomerArrayAdapter(getActivity(), tasks);
                cstSpinner.setAdapter(adapter);
//                if (tasks.size() > 1) {
//                    cstSpinner.setSelection(1);
//                } else {
//                    cstSpinner.setSelection(0);
//                }

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    // select customer type
    void showDialogeCustomer() {
        customerType = 1;
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.customer_dialoge);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb = dialog.findViewById(checkedId);
                if (rb.getText().toString().equalsIgnoreCase("Farmer")) {
                    customerType = 1;
                } else if (rb.getText().toString().equalsIgnoreCase("Franchise")) {
                    customerType = 2;
                } else if (rb.getText().toString().equalsIgnoreCase("Dealer")) {
                    customerType = 3;
                }
                if (customerType == 1) {
                    discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
                } else if (customerType == 2) {
                    discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
                } else if (customerType == 3) {
                    discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
                }
                getTasks();
                getCartItems();
            }
        });
        TextView okay_text = dialog.findViewById(R.id.ok_text);
        TextView cancel_text = dialog.findViewById(R.id.cancel_text);
        cstSpinner = dialog.findViewById(R.id.cstSpinner);
        cstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    customerID = customerList.get(position).getCustomer_id() + "";
                    customerTxt.setText(customerList.get(position).getName());
                    customerTypeLineDiscount = customerList.get(position).getDiscounts();
                    discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
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
                if (customerID.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), R.string.CUSTOMER_FIRST, Toast.LENGTH_SHORT).show();
                    return;
                } else if (customerTypeLineDiscount == null) {
                    Toast.makeText(getActivity(), R.string.LOGIN_FIRST, Toast.LENGTH_SHORT).show();
                    return;
                } else if (customerTypeLineDiscount.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), R.string.LOGIN_FIRST, Toast.LENGTH_SHORT).show();
                    return;
                }
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


    private void getCartItems() {

        if (Utility.isNetworkAvailable(getActivity())) {
            getExtraOrderDiscount();
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
                    discountLayout.setVisibility(View.VISIBLE);
                    emtyView.setVisibility(View.GONE);

                    //        showDialogeCustomer();
                } else {
                    emtyView.setVisibility(View.VISIBLE);
                    discountLayout.setVisibility(View.GONE);
                }
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    private void setBillPanel(int size) {
        double sumTotalAmount = 0.0;
        double lineDiscountAmount = 0.0;
        double totalDiscount = 0.0;
        double gstAmount = 0.0;
        double extradiscount = 0.0;
        prjsonArray = new JSONArray();
        double tempDiscountforUI = offerDiscount;
//        isCouponCodeApply(size);
        for (int i = 0; i < size; i++) {
            sumTotalAmount += posOrderList.get(i).getUnitPrice() * posOrderList.get(i).getQuantity();
            double lineAmount = posOrderList.get(i).getUnitPrice() * posOrderList.get(i).getQuantity();
            //line Discount Amount for customer
            if (customerType == 1) {
                lineDiscountAmount = lineAmount * Integer.parseInt(customerTypeLineDiscount) / 100;
                discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
            } else if (customerType == 2) {
                lineDiscountAmount = lineAmount * Integer.parseInt(customerTypeLineDiscount) / 100;
                discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
            } else if (customerType == 3) {
                lineDiscountAmount = lineAmount * Integer.parseInt(customerTypeLineDiscount) / 100;
                discountLabelTxt.setText("Total Discount " + customerTypeLineDiscount + "%");
            }
//            double amountAfterLineDiscount = 0.0;

            // this comented code for gift card logic with split at line items
//            if (offerDiscount >= lineAmount && size > 1) {
//
//                if (i == 0) {
//                    offerDiscount = offerDiscount / 2;
//                    Log.v("offerDiscount",offerDiscount+"");
//                    amountAfterLineDiscount = lineAmount - (lineDiscountAmount + offerDiscount);
//
//                } else if (i == 1) {
//                    amountAfterLineDiscount = lineAmount - (lineDiscountAmount + offerDiscount);
//                }
//            } else if (i == 0) {
//                amountAfterLineDiscount = lineAmount - (lineDiscountAmount + offerDiscount);
//            } else {
//                amountAfterLineDiscount = lineAmount - lineDiscountAmount;
//            }


            double amountAfterLineDiscount = lineAmount - lineDiscountAmount;
            totalDiscount += lineDiscountAmount;
            double lineGstAmount = 0.0;
            if (!ifFreeProduct) {
                posOrderList.get(i).setAmount_tax(amountAfterLineDiscount * posOrderList.get(i).getGst() / 100);
                lineGstAmount = amountAfterLineDiscount * posOrderList.get(i).getGst() / 100;
                gstAmount += lineGstAmount;
            } else {
                posOrderList.get(i).setAmount_tax(amountAfterLineDiscount * posOrderList.get(0).getGst() / 100);
                lineGstAmount = amountAfterLineDiscount * posOrderList.get(0).getGst() / 100;
                gstAmount += lineGstAmount;
            }
            try {
                productOBJ = new JSONObject();
//                productOBJ.putOpt("gift_card", offerDiscount);
                productOBJ.putOpt("product_id", posOrderList.get(i).getProductID());
                productOBJ.putOpt("crop_name", posOrderList.get(i).getCrop_name());
                productOBJ.putOpt("variant_id", posOrderList.get(i).getVariantID());
                productOBJ.putOpt("full_product_name", posOrderList.get(i).getProductName() + posOrderList.get(i).getProductVariants());
                productOBJ.putOpt("price_unit", posOrderList.get(i).getUnitPrice());
                if (customerType == 1) {
                    productOBJ.putOpt("discount", Integer.parseInt(customerTypeLineDiscount));
                } else if (customerType == 2) {
                    productOBJ.putOpt("discount", Integer.parseInt(customerTypeLineDiscount));
                } else if (customerType == 3) {
                    productOBJ.putOpt("discount", Integer.parseInt(customerTypeLineDiscount));
                }
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


        gstLabelTxt.setText("GST Amount");
        totalAmount.setText(Utility.decimalFormat(sumTotalAmount) + "");
        itemCountTxt.setText(size + "");
        lineDiscountTxt.setText("- " + Utility.decimalFormat(totalDiscount) + "");
        couponCodeAmountTxt.setText("-" + tempDiscountforUI);
//        if (isCouponCodeApply(size)) {
//            couponCodeAmountTxt.setText("-" + offerDiscount);
//        } else {
//            couponCodeAmountTxt.setText("-0.0");
//        }
//        subTotal_50 = (sumTotalAmount - totalDiscount - offerDiscount);
        subTotal_50 = sumTotalAmount - (totalDiscount + offerDiscount);
        extra_discount_lay.setVisibility(View.GONE);
        if (subTotal_50 >= 100000.00) {
            extradiscount = subTotal_50 * 2 / 100;
            subTotal_50 = subTotal_50 - extradiscount;
            extra_discount_lay.setVisibility(View.VISIBLE);
            DecimalFormat form = new DecimalFormat("0.00");
            offerDiscountTxt.setText("-" + form.format(Double.valueOf(extradiscount)));
        }


        paidAmount = subTotal_50 + gstAmount;

        subTotalTxt.setText(Utility.decimalFormat(subTotal_50) + "");
        paidTxt.setText(Utility.decimalFormat(paidAmount) + "");
        gstTxt.setText("+ " + Utility.decimalFormat(gstAmount) + "");
        if (subTotal_50 >= 50000) {
            free_product_button.setVisibility(View.VISIBLE);
        } else {
            free_product_button.setVisibility(View.GONE);
        }
    }

    // create PosOrder Request
    private void createPosOrderRequest(String payment_method_id, String transactionid) throws JSONException {
        JSONArray paymentLineJsonArray = new JSONArray();
        JSONObject paymentOBJ = new JSONObject();
        paymentOBJ.put("amount", paidAmount);
        if (payment_method_id.equalsIgnoreCase("2")) {
            paymentOBJ.put("transaction_id", transactionid);
        }
        Log.v("paid_amount", String.valueOf(paidAmount));
        paymentOBJ.put("payment_method_id", payment_method_id);// cash 1 bank 2
        paymentLineJsonArray.put(paymentOBJ);
        Log.v("paymentOBJ", String.valueOf(paymentOBJ));
        Utility.showDialoge("Please wait while a moment...", getActivity());
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("token", sm.getJWTToken());
        params.put("partner_id", customerID);
        params.put("coupon_code", coupanCode);
        params.put("products", prjsonArray.toString());
        params.put("payment_line", paymentLineJsonArray.toString());

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


    //call Gift card api
    private void getGiftCards(String s) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_GIFT_CARDS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response_gift", url);
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
                        if (giftCardModel.getData() != null) {
                            for (GiftCardListModel detail : giftCardModel.getData()) {
                                Log.v("Gift code", s + "    " + detail.getCode());
                                if (detail.getCode().trim().equalsIgnoreCase(s.trim())) {
                                    Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-03");//detail.getCurrent_server_date()
                                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(detail.getExpirationDate());
                                    if (currentDate.before(date)) {
                                        str_coupon = detail.getValue();
                                        offerDiscount = Double.valueOf(str_coupon.trim());
                                        coupanCode = detail.getCode();
                                        applyBtn.setVisibility(View.GONE);
                                        removeBtn.setVisibility(View.VISIBLE);
                                        setBillPanel(posOrderList.size());
                                        return;
                                    } else {
                                        Toast.makeText(getActivity(), R.string.COUPON_EXPIRED, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else {
                                    Toast.makeText(getActivity(), R.string.COUPON_CODE, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }

                        } else {
                            Toast.makeText(getContext(), R.string.VALID_COUPON, Toast.LENGTH_SHORT).show();
                        }


                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
        });
        queue.add(jsonObjectRequest);
    }

    //get Extra Order Discount
    private void getExtraOrderDiscount() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.POST_ORDER_DISCOUNT + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
       Log.v("post_order",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("getExtraOrderDiscount", response.toString());
                JSONObject obj = null;
                discountModel = new DiscountModel();
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<DiscountModel>() {
                        }.getType();
                        discountModel = gson.fromJson(response.toString(), listType);

//                        if (discountModel.getLoyaltyRewards().size() > 0) {
//                            max = discountModel.getLoyaltyRewards().get(0).getMinimumAmount();
//                            min = discountModel.getLoyaltyRewards().get(1).getMinimumAmount();
//                            // get free product List
//                            for (int i = 1; i < discountModel.getLoyaltyRewards().size(); i++) {
//                                DiscountListModel model = new DiscountListModel();
//                                model.setType(discountModel.getLoyaltyRewards().get(i).getType());
//                                model.setProductId(discountModel.getLoyaltyRewards().get(i).getProductId());
//                                model.setProduct_variant_id(discountModel.getLoyaltyRewards().get(i).getProduct_variant_id());
//                                model.setMinimumAmount(discountModel.getLoyaltyRewards().get(i).getMinimumAmount());
//                                model.setDescription(discountModel.getLoyaltyRewards().get(i).getDescription());
//                                model.setRewardId(discountModel.getLoyaltyRewards().get(i).getRewardId());
//                                model.setQty(discountModel.getLoyaltyRewards().get(i).getQty());
//                                model.setProductName(discountModel.getLoyaltyRewards().get(i).getProductName());
//                                model.setProductImageUrl(discountModel.getLoyaltyRewards().get(i).getProductImageUrl());
//                                model.setTaxesId(discountModel.getLoyaltyRewards().get(i).getTaxesId());
//                                discountListModel.add(model);
//                            }
//
//                        }
                        Log.v("model", String.valueOf(discountListModel.size()));
                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> {
        });
        queue.add(jsonObjectRequest);
    }
    //Coupon Code Apply
//    public boolean isCouponCodeApply(int size) {
//
//        double bastAmoutTotal = 0.0;
//        for (int i = 0; i < size; i++) {
//            double lineDiscountAmount = 0.0;
//            double lineAmount = posOrderList.get(i).getUnitPrice() * posOrderList.get(i).getQuantity();
//            if (customerType == 1) {
//                lineDiscountAmount = lineAmount * 15 / 100;
//            } else if(customerType == 2){
//                lineDiscountAmount = lineAmount * 18 / 100;
//            }else if(customerType==3){
//                lineDiscountAmount = lineAmount * 24 / 100;
//            }
//            double amountAfterLineDiscount = lineAmount - lineDiscountAmount;
//            bastAmoutTotal = bastAmoutTotal + amountAfterLineDiscount;
//        }
//        if (bastAmoutTotal >= offerDiscount) {
//            return true;
//        } else {
//            offerDiscount = 0.0;
//            return false;
//        }
//
//    }
//    public  boolean isDateAfter(String startDate,String endDate)
//    {
//        try
//        {
//            String myFormatString = "yyyy-M-dd"; // for example
//            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
//            Date date1 = df.parse(endDate);
//            Date startingDate = df.parse(startDate);
//
//            if (date1.after(startingDate))
//                return true;
//            else
//                return false;
//        }
//        catch (Exception e)
//        {
//
//            return false;
//        }
//    }

}

