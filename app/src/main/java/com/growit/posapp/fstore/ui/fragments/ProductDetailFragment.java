package com.growit.posapp.fstore.ui.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.growit.posapp.fstore.adapters.VariantAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.SimilarProductAdapter;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.tables.PosOrder;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.Value;

import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.RecyclerItemClickListener;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private TextView add_btn, setPatternsTxt, nameTxt;
    private RecyclerView  recyclerView;//horizontalView
    String s1 = "", s2 = "", s3 = "";
    String[] variantArray = null;
    int patternType = -1;
    private ImageView productImage;
    ProductDetail productDetail;
    double discount = 0.0;
    private double quantity = 1.0;
    private int totaQquantity = 1;
    StringBuilder stringBuilder;
    SessionManagement sm;

    public static ProductDetailFragment newInstance() {
        return new ProductDetailFragment();
    }

    private String productID = "";
    private String variantID = "";
    private String cropID = "",cropName="";
    protected List<Product> productList = new ArrayList<>();
    SimilarProductAdapter productListAdapter;
    private LinearLayout buttonsLayout;
    private TextView itemPriceTxt, mrpPriceTxt;
    private SliderLayout sliderLayout;
    private HashMap<String, String> sliderImages;
    NumberPicker numberPicker;
    private int addGSTValue = 0;
    double basePrice = 0.0;
    String variants;
    ProgressBar progress_5, progress_4, progress_3, progress_2, progress_1;

    TextView pro_curr_text, menu_lay, info_lay, description_text, pac_text;
    LinearLayout recy_lay_menu, review_lay, review_layout_xml, info_xml;
    RecyclerView recy_rating;
    Spinner spinner_1, spinner_2, spinner_3, spinner_4;
    LinearLayout spinner_layout_1, spinner_layout_2, spinner_layout_3, spinner_layout_4;
    List<Value> variant_model_1, variant_model_2, variant_model_3, variant_model_4;
    String str_variant_1,str_variant_2,str_variant_3,str_variant_4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail, parent, false);
        add_btn = view.findViewById(R.id.add_btn);
        nameTxt = view.findViewById(R.id.nameTxt);
        numberPicker = view.findViewById(R.id.number_picker);
        buttonsLayout = view.findViewById(R.id.buttons);
        itemPriceTxt = view.findViewById(R.id.itemPriceTxt);
        mrpPriceTxt = view.findViewById(R.id.mrpPriceTxt);
        mrpPriceTxt.setPaintFlags(mrpPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        productImage = view.findViewById(R.id.productImage);
        setPatternsTxt = view.findViewById(R.id.setPatternsTxt);
//        horizontalView = view.findViewById(R.id.horizontal_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        sliderLayout = view.findViewById(R.id.sliderLayout);
        pro_curr_text = view.findViewById(R.id.pro_curr_text);
        ///****************
        description_text = view.findViewById(R.id.description_text);
        menu_lay = view.findViewById(R.id.menu_lay);
        pac_text = view.findViewById(R.id.pac_text);

        ///menu layout
        recy_lay_menu = view.findViewById(R.id.recy_lay_menu);
        View menu_line = view.findViewById(R.id.menu_line);

        ///review layout
        review_layout_xml = view.findViewById(R.id.review_layout_xml);
        review_lay = view.findViewById(R.id.review_lay);
        View review_line = view.findViewById(R.id.review_line);

        ///info layout
        info_xml = view.findViewById(R.id.info_xml);
        View info_line = view.findViewById(R.id.info_line);
        info_lay = view.findViewById(R.id.ifo_lay);

        //spinner
        spinner_1 = view.findViewById(R.id.spinner_1);
        spinner_2 = view.findViewById(R.id.spinner_2);
        spinner_3 = view.findViewById(R.id.spinner_3);
        spinner_4 = view.findViewById(R.id.spinner_4);

        spinner_layout_1 = view.findViewById(R.id.spinner_layout_1);
        spinner_layout_2 = view.findViewById(R.id.spinner_layout_2);
        spinner_layout_3 = view.findViewById(R.id.spinner_layout_3);
        spinner_layout_4 = view.findViewById(R.id.spinner_layout_4);
        ///review recyclerView
        recy_rating = view.findViewById(R.id.recy_rag);// get the reference of ViewFlipper
        GridLayoutManager layoutManager_rating = new GridLayoutManager(getActivity(), 1, LinearLayoutManager.VERTICAL, false);
        recy_rating.setLayoutManager(layoutManager_rating);

        progress_5 = view.findViewById(R.id.progress_5);
        progress_4 = view.findViewById(R.id.progress_4);
        progress_3 = view.findViewById(R.id.progress_3);
        progress_3 = view.findViewById(R.id.progress_3);
        progress_2 = view.findViewById(R.id.progress_2);

        progress_5.setMax(100);
        progress_5.setProgress(80);
        // progress_5.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        progress_4.setMax(100);
        progress_4.setProgress(70);
        //  progress_4.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        progress_3.setMax(100);
        progress_3.setProgress(50);
        //  progress_3.setProgressTintList(ColorStateList.valueOf(Color.DKGRAY));
        progress_2.setMax(100);
        progress_2.setProgress(40);
        // progress_2.setProgressTintList(ColorStateList.valueOf(Color.MAGENTA));


        ////**************************///

        sm = new SessionManagement(getActivity());
        sliderImages = new HashMap<>();
        if (getArguments() != null) {
            productID = getArguments().getString("PID");
            cropID = getArguments().getString("CPID");
            cropName= getArguments().getString("CropName");
            if (Utility.isNetworkAvailable(getActivity())) {
                getProductDetail(productID);
                prepareProducts(cropID);
            } else {
                Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

            }

        }

        numberPicker.setMin(1);
        numberPicker.setUnit(1);
        numberPicker.setValue(1);
        numberPicker.setVisibility(View.GONE);
        add_btn.setVisibility(View.GONE);
        pro_curr_text.setVisibility(View.GONE);
        numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                quantity = value;
                DecimalFormat form = new DecimalFormat("0.00");
                itemPriceTxt.setText("₹ " + String.valueOf(form.format(Double.valueOf(basePrice * quantity))));
                // itemPriceTxt.setText("₹ " + basePrice * quantity + "");
            }
        });

        add_btn.setOnClickListener(v -> {
            boolean empty = true;
            if (variantArray != null) {
                for (int i = 0; i < variantArray.length; i++) {
                    if (variantArray[i] == null) {
                        empty = false;
                        break;
                    }
                }
                if (!empty) {
                    Toast.makeText(getActivity(), R.string.Add_Variants, Toast.LENGTH_SHORT).show();
                    return;
                }
                PosOrder order = new PosOrder();
                order.setProductID(Integer.parseInt(productID));
                order.setVariantID(Integer.parseInt(variantID));
                order.setProductName(nameTxt.getText().toString());
                order.setCropID(Integer.parseInt(cropID));
                order.setCrop_name(cropName);
                order.setQuantity(quantity);
                order.setTotalQuantity(totaQquantity);
                order.setTaxID(productDetail.getData().get(0).getTaxesId());
                order.setUnitPrice(productDetail.getData().get(0).getListPrice());
//                if (sm.getCustomerType() == 1) {
//                    discount = ((quantity * productDetail.getData().get(0).getListPrice()) * 15) / 100;
//                    order.setLineDiscount(15);
//                    order.setDiscountAmount(discount);
//                }
                order.setGst(addGSTValue);
                order.setProductImage(productDetail.getData().get(0).getImageUrl());

                if (variants != null) {
                    order.setProductVariants("(" + variants + ")");
                    AsyncTask.execute(() -> {
                        int prodCount = 0;
                        prodCount = DatabaseClient.getInstance(getActivity()).getAppDatabase().productDao().getProductDetailById(order.getProductID(), order.getProductVariants());
                        if (prodCount > 0) {
                            DatabaseClient.getInstance(getActivity()).getAppDatabase().productDao().updateProductQuantity((int) order.getQuantity(), order.getProductID(), order.getProductVariants());
                        } else {
                            DatabaseClient.getInstance(getActivity()).getAppDatabase().productDao().insert(order);
                        }
                        sendToSomeActivity();
                    });
                }
                Toast.makeText(getActivity(), R.string.Add_card, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.No_Variants, Toast.LENGTH_SHORT).show();

            }
        });
//        horizontalView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(), horizontalView, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        variantArray[patternType] = productDetail.getData().get(0).getAttributes().get(patternType).getValues().get(position).getValueName();
//
//                        StringBuilder stringBuilder = new StringBuilder();
//                        for (int i = 0; i < variantArray.length; i++) {
//                            stringBuilder.append(variantArray[i]).append(",");
//                        }
//                        variants = stringBuilder.substring(0, stringBuilder.length() - 1);
////                        Log.v("variantArray", variants.toString());
////                        Log.i("ProductName--", stringBuilder.substring(0, stringBuilder.length() - 1));
//                        String R1 = nameTxt.getText().toString() + "(" + variants + ")";
//                        String productNameSearchStr = R1.replaceAll("\\s", "");
//                        String variantid = "";
//                        for (int i = 0; i < productDetail.getData().get(0).getExtraPriceLists().size(); i++) {
//                            String variantName = productDetail.getData().get(0).getExtraPriceLists().get(i).getProduct_variant_name().trim().replaceAll("\\s", "");
//
//                            ///****************/////////
//                            //   categoryName = productDetail.getData().get(0).getCategory_name();
//                            ////////////******
//
//                            if (productNameSearchStr.equalsIgnoreCase(variantName)) {
//                                basePrice = productDetail.getData().get(0).getExtraPriceLists().get(i).getPrice_extra();
//                                itemPriceTxt.setText("₹ " + basePrice * quantity + "");
//                                mrpPriceTxt.setText("₹ " + productDetail.getData().get(0).getExtraPriceLists().get(i).getMrp_price() + "");
//                                productDetail.getData().get(0).setListPrice(basePrice);
//
//                                variantid = productDetail.getData().get(0).getExtraPriceLists().get(i).getProduct_variant_id();
//                            }
//                        }
//
//                        for (int i = 0; i < productDetail.getData().get(0).getProductVariantQuantities().size(); i++) {
//                            String id = productDetail.getData().get(0).getProductVariantQuantities().get(i).getVariant_id();
//                            if (variantid.equalsIgnoreCase(id)) {
//                                totaQquantity = productDetail.getData().get(0).getProductVariantQuantities().get(i).getQuantity();
//                                if ((int) totaQquantity > 0) {
//                                    numberPicker.setMax(totaQquantity);
//                                    variantID = productDetail.getData().get(0).getProductVariantQuantities().get(i).getVariant_id();
//                                    numberPicker.setVisibility(View.VISIBLE);
//                                    add_btn.setVisibility(View.VISIBLE);
//                                    pro_curr_text.setVisibility(View.GONE);
//                                } else {
//                                    numberPicker.setVisibility(View.GONE);
//                                    add_btn.setVisibility(View.GONE);
//                                    pro_curr_text.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        }
//
//                        setPatternsTxt.setText(productNameSearchStr);
//                        horizontalView.setVisibility(View.GONE);
//                    }
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                }));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        productID = productList.get(position).getProductID();
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getProductDetail(productID);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        ////////////////************Review*****************////////////////
        review_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_line.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                menu_line.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                review_line.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
                review_layout_xml.setVisibility(View.VISIBLE);
                info_xml.setVisibility(View.GONE);
                recy_lay_menu.setVisibility(View.GONE);

            }
        });

        ////////////////***********Menu******************////////////////
        menu_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_line.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                menu_line.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
                review_line.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                recy_lay_menu.setVisibility(View.VISIBLE);
                review_layout_xml.setVisibility(View.GONE);
                info_xml.setVisibility(View.VISIBLE);
            }
        });

        ////////////////**************Info***************////////////////
        info_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_line.setBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
                menu_line.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                review_line.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                info_xml.setVisibility(View.VISIBLE);
                recy_lay_menu.setVisibility(View.GONE);
                review_layout_xml.setVisibility(View.GONE);

            }
        });
        spinner_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //    str_variant_1 = variant_model_1.get(position).getValueName() + "";
                patternType=0;
                val(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                patternType=1;

                str_variant_2 = variant_model_2.get(position).getValueName() + "";
                val(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // str_variant_3 = variant_model_3.get(position).getValueName() + "";
                patternType=2;
                val(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // str_variant_4 = variant_model_4.get(position).getValueName() + "";
                patternType=3;
                val(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void setupSlider(String pName, String productImage) {
        Log.i("-------------", productImage);
        sliderImages = new HashMap<>();
        sliderImages.put(pName, productImage);
        for (String name : sliderImages.keySet()) {

            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .description(name)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.stopAutoCycle();
        sliderLayout.addOnPageChangeListener(this);
    }

    private void sendToSomeActivity() {
        Intent intent = new Intent();
        intent.setAction(ApiConstants.ACTION);
        intent.putExtra("dataToPass", "1");
        getActivity().sendBroadcast(intent);
    }

//    private void colorPatterns(List<Value> valueList) {
//        if (valueList != null && valueList.size() > 0) {
//            horizontalView.setVisibility(View.VISIBLE);
//            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
//            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            HorizontalWidgetAdapter patternListAdapter = new HorizontalWidgetAdapter(getActivity(), valueList);
//            horizontalView.setAdapter(patternListAdapter);
//            horizontalView.setLayoutManager(layoutManager);
//        }
//    }

    private void createLayoutDynamically(int n) {
        variantArray = new String[n];
        buttonsLayout.removeAllViews();
        buttonsLayout.invalidate();

        ////////////////************

        if (n == 1) {
            patternType=0;
            variant_model_1 = productDetail.getData().get(0).getAttributes().get(0).getValues();
            spinner_layout_1.setVisibility(View.VISIBLE);
            VariantAdapter variantAdapter = new VariantAdapter(getActivity(), variant_model_1);
            spinner_1.setAdapter(variantAdapter);
        } else if (n == 2) {
            patternType=1;
            variant_model_1 = productDetail.getData().get(0).getAttributes().get(0).getValues();
            variant_model_2 = productDetail.getData().get(0).getAttributes().get(1).getValues();

            spinner_layout_1.setVisibility(View.VISIBLE);
            spinner_layout_2.setVisibility(View.VISIBLE);
            VariantAdapter variantAdapter = new VariantAdapter(getActivity(), variant_model_1);
            spinner_1.setAdapter(variantAdapter);

            VariantAdapter variantAdapter_2 = new VariantAdapter(getActivity(), variant_model_2);
            spinner_2.setAdapter(variantAdapter_2);
        } else if (n == 3) {
            patternType=2;
            variant_model_1 = productDetail.getData().get(0).getAttributes().get(0).getValues();
            variant_model_2 = productDetail.getData().get(0).getAttributes().get(1).getValues();
            variant_model_3 = productDetail.getData().get(0).getAttributes().get(2).getValues();

            spinner_layout_1.setVisibility(View.VISIBLE);
            spinner_layout_2.setVisibility(View.VISIBLE);
            spinner_layout_3.setVisibility(View.VISIBLE);

            VariantAdapter variantAdapter = new VariantAdapter(getActivity(), variant_model_1);
            spinner_1.setAdapter(variantAdapter);

            VariantAdapter variantAdapter_2 = new VariantAdapter(getActivity(), variant_model_2);
            spinner_2.setAdapter(variantAdapter_2);

            VariantAdapter variantAdapter_3 = new VariantAdapter(getActivity(), variant_model_3);
            spinner_3.setAdapter(variantAdapter_3);
        } else if (n == 4) {
            patternType=3;
            variant_model_1 = productDetail.getData().get(0).getAttributes().get(0).getValues();
            variant_model_2 = productDetail.getData().get(0).getAttributes().get(1).getValues();
            variant_model_3 = productDetail.getData().get(0).getAttributes().get(2).getValues();
            variant_model_4 = productDetail.getData().get(0).getAttributes().get(3).getValues();

            spinner_layout_1.setVisibility(View.VISIBLE);
            spinner_layout_2.setVisibility(View.VISIBLE);
            spinner_layout_3.setVisibility(View.VISIBLE);
            spinner_layout_4.setVisibility(View.VISIBLE);

            VariantAdapter variantAdapter = new VariantAdapter(getActivity(), variant_model_1);
            spinner_1.setAdapter(variantAdapter);

            VariantAdapter variantAdapter_2 = new VariantAdapter(getActivity(), variant_model_2);
            spinner_2.setAdapter(variantAdapter_2);

            VariantAdapter variantAdapter_3 = new VariantAdapter(getActivity(), variant_model_3);
            spinner_3.setAdapter(variantAdapter_3);

            VariantAdapter variantAdapter_4 = new VariantAdapter(getActivity(), variant_model_4);
            spinner_1.setAdapter(variantAdapter_4);
        }
        ////************


//        for (int i = 0; i < n; i++) {
//            Button myButton = new Button(getActivity());
//            myButton.setText(productDetail.getData().get(0).getAttributes().get(i).getAttributeName());
//            myButton.setBackgroundResource(R.color.colorPrimary);
//            myButton.setTextColor(Color.BLACK);
//            myButton.setTextSize(14f);
//            myButton.setId(i);
//            final int id_ = myButton.getId();
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            layoutParams.setMargins(10, 10, 5, 0);
//            myButton.setLayoutParams(layoutParams);
//            buttonsLayout.addView(myButton);
//            ViewGroup.LayoutParams vg_lp = myButton.getLayoutParams();
//            LinearLayout.LayoutParams rl_lp = new LinearLayout.LayoutParams(vg_lp);
//            rl_lp.gravity = Gravity.CENTER_HORIZONTAL;
//
//            myButton.setOnClickListener(view -> {
//                patternType = id_;
//                colorPatterns(productDetail.getData().get(0).getAttributes().get(id_).getValues());
//            });
//        }
    }

    private void getProductDetail(String pid) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_DETAIL + "user_id=" + sm.getUserID() + "&" + "product_id=" + pid + "&" + "token=" + sm.getJWTToken();
        Log.v("url_product", url.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response_product", response.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ProductDetail>() {
                        }.getType();

                        productDetail = gson.fromJson(response.toString(), listType);
                        nameTxt.setText(productDetail.getData().get(0).getProductName());
                        ////***
                        description_text.setText(productDetail.getData().get(0).getDescription());
                        pac_text.setText(productDetail.getData().get(0).getProduct_package());
                        ///****
//                        setupSlider("₹ " + productDetail.getData().get(0).getListPrice() + " | " + productDetail.getData().get(0).getProductName(), ApiConstants.BASE_URL + productDetail.getData().get(0).getImageUrl());
                        Log.i("----", ApiConstants.BASE_URL + productDetail.getData().get(0).getImageUrl());
                        Glide.with(getActivity())
                                .load(ApiConstants.BASE_URL + productDetail.getData().get(0).getImageUrl())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(productImage);


                        if (productDetail.getData().get(0).getExtraPriceLists() == null || productDetail.getData().get(0).getExtraPriceLists().size() == 0) {
                            return;
                        }
                        basePrice = productDetail.getData().get(0).getExtraPriceLists().get(0).getPrice_extra();
                        for (int i = 1; i < productDetail.getData().get(0).getExtraPriceLists().size(); i++) {

                            if (basePrice > productDetail.getData().get(0).getExtraPriceLists().get(i).getPrice_extra()) {
                                basePrice = productDetail.getData().get(0).getExtraPriceLists().get(i).getPrice_extra();
                                mrpPriceTxt.setText("₹ " + productDetail.getData().get(0).getExtraPriceLists().get(i).getMrp_price() + "");
                            }

                        }
                        Log.i("Price--", basePrice + "");
                        itemPriceTxt.setText("₹ " + basePrice);

                        productDetail.getData().get(0).setListPrice(basePrice);
                        AsyncTask.execute(() -> {
                            addGSTValue = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                                    .gstDao().getGSTValueById(productDetail.getData().get(0).getTaxesId());
                        });
                        createLayoutDynamically(productDetail.getData().get(0).getAttributes().size());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    private void prepareProducts(String id) {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_PRODUCT_LIST + "user_id=" + sm.getUserID() + "&" + "pos_category_id=" + id + "&" + "token=" + sm.getJWTToken();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response_product", response.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");

                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = obj.getJSONArray("data");

                        JSONArray productArray = jsonArray.getJSONObject(0).getJSONArray("products");
                        productList = new ArrayList<>();
                        if (productArray.length() > 0) {
                            for (int i = 0; i < productArray.length(); i++) {
                                Product product = new Product();
                                JSONObject data = productArray.getJSONObject(i);
                                int ID = data.optInt("product_id");
                                String name = data.optString("product_name");

                                //*********
                                String product_package = data.optString("product_package");
                                double amount = data.optDouble("list_price");
                                ////************
                                product.setProductID(ID + "");
                                product.setProductName(name);
                                product.setPrice(amount);
                                product.setProduct_package(product_package);
                                String image = "";
                                if (data.opt("image_url").equals(false)) {

                                    image = "";
                                } else {
                                    image = data.optString("image_url");
                                }
                                product.setProductImage(image);
                                productList.add(product);
                            }
                            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            productListAdapter = new SimilarProductAdapter(getActivity(), productList);
                            recyclerView.setAdapter(productListAdapter);
                            recyclerView.setLayoutManager(layoutManager);
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
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void val(int position){
        variantArray[patternType] = productDetail.getData().get(0).getAttributes().get(patternType).getValues().get(position).getValueName();

        stringBuilder = new StringBuilder();
        for (int i = 0; i < variantArray.length; i++) {
            stringBuilder.append(variantArray[i]).append(",");
        }
        variants = stringBuilder.substring(0, stringBuilder.length() - 1);
//                        Log.v("variantArray", variants.toString());
//                        Log.i("ProductName--", stringBuilder.substring(0, stringBuilder.length() - 1));
        String R1 = nameTxt.getText().toString() + "(" + variants + ")";
        String productNameSearchStr = R1.replaceAll("\\s", "");
        String variantid = "";
        for (int i = 0; i < productDetail.getData().get(0).getExtraPriceLists().size(); i++) {
            String variantName = productDetail.getData().get(0).getExtraPriceLists().get(i).getProduct_variant_name().trim().replaceAll("\\s", "");

            ///****************/////////
            //   categoryName = productDetail.getData().get(0).getCategory_name();
            ////////////******

            if (productNameSearchStr.equalsIgnoreCase(variantName)) {
                basePrice = productDetail.getData().get(0).getExtraPriceLists().get(i).getPrice_extra();
                itemPriceTxt.setText("₹ " + basePrice * quantity + "");
                mrpPriceTxt.setText("₹ " + productDetail.getData().get(0).getExtraPriceLists().get(i).getMrp_price() + "");
                productDetail.getData().get(0).setListPrice(basePrice);

                variantid = productDetail.getData().get(0).getExtraPriceLists().get(i).getProduct_variant_id();
            }
        }

        for (int i = 0; i < productDetail.getData().get(0).getProductVariantQuantities().size(); i++) {
            String id = productDetail.getData().get(0).getProductVariantQuantities().get(i).getVariant_id();
            if (variantid.equalsIgnoreCase(id)) {
                totaQquantity = productDetail.getData().get(0).getProductVariantQuantities().get(i).getQuantity();
                if ((int) totaQquantity > 0) {
                    numberPicker.setMax(totaQquantity);
                    variantID = productDetail.getData().get(0).getProductVariantQuantities().get(i).getVariant_id();
                    numberPicker.setVisibility(View.VISIBLE);
                    add_btn.setVisibility(View.VISIBLE);
                    pro_curr_text.setVisibility(View.GONE);
                } else {
                    numberPicker.setVisibility(View.GONE);
                    add_btn.setVisibility(View.GONE);
                    pro_curr_text.setVisibility(View.VISIBLE);
                }
            }
        }

        setPatternsTxt.setText(productNameSearchStr);
    }
}