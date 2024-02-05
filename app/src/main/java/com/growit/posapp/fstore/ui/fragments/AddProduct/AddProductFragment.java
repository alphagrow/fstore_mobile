package com.growit.posapp.fstore.ui.fragments.AddProduct;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.AddProductListAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.adapters.ImageAdapter;
import com.growit.posapp.fstore.adapters.POSAdapter;
import com.growit.posapp.fstore.adapters.UOMAdapter;
import com.growit.posapp.fstore.adapters.UOMSpinnerAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddProductBinding;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.model.AttributeModel;
import com.growit.posapp.fstore.model.AttributeValue;
import com.growit.posapp.fstore.model.ListAttributesModel;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.Purchase.PurchaseModel;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.UomCategoryModel;
import com.growit.posapp.fstore.model.UomLineModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.tables.GST;
import com.growit.posapp.fstore.ui.LoginActivity;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddProductFragment extends Fragment implements View.OnClickListener {


    ArrayList<Bitmap> imagesUriArrayList;
    private ProgressBar progressBar;
    private ImageView imageView, video_image;
    private VideoView videoView;
    String str_product_name, str_product_price, str_uom, str_uom_cate, vendor_tax = "",customer_tax="";
    String imageFilePath;
    ProgressBar idPBLoading;
    private TextView video_text;
    Map<String, List<String>> selected_value_map = new HashMap<>();
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_VIDEO = 4;
    private String str_image_aa;
    ProgressDialog progressDialog;
    MediaController mediaControls;
    //    private RecyclerView recy_image;
    Bitmap bitmap = null;
    //    RadioGroup sel_non_gov;
    ImageAdapter adapter;
    //    TextView mfd_date, exp_date, exp_date_alarm;
    ProductDetail model_uom_type;
    DatePickerDialog.OnDateSetListener date_mfd, date_exp, date_exp_alarm;
    final Calendar myCalendar = Calendar.getInstance();
    FragmentAddProductBinding binding;

    String str_mfd_date, str_exp_date;
    private String[] detailed_type = {"product", "service"};
    private String[] uom_list = {"Days"};
    String str_detailed_type;
    String str_non_gov_product = "Non Gov-Authorized";
    //    String crop_id;
    ListAttributesModel model_attribute;
    List<AttributeModel> model = new ArrayList<>();
    List<UomLineModel> uomLines = new ArrayList<>();
    List<Value> cropList = new ArrayList<>();
    List<UomCategoryModel> uom_model = new ArrayList<>();
    List<UomCategoryModel> uom_model_type = new ArrayList<>();
    String str_crop_id;
    ArrayList<Integer> crop_id_list = new ArrayList<>();
    ArrayList<Integer> attribute_id_list = new ArrayList<>();
    String selected_crop_id;
    Bitmap imageBitmap = null;
    String str_image_crop;
    StateModel stateModel;


    //   List<StateModel> uom_list = new ArrayList<>();
    public AddProductFragment() {
    }

    public static AddProductFragment newInstance() {
        return new AddProductFragment();
    }

    UomLineModel uom_model_list;
    List<StateModel> purchase_tax_list = new ArrayList<>();
    List<StateModel> sale_tax_list = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
// Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false);

        imagesUriArrayList = new ArrayList();
        init();
        return binding.getRoot();
    }

    private void init() {

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, detailed_type);
        binding.detailTypeSpinner.setAdapter(spinnerArrayAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        binding.submitBtn.setOnClickListener(this);
        binding.detailTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(R.color.text_color);
                str_detailed_type = detailed_type[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (Utility.isNetworkAvailable(getContext())) {
            getTaxList();
            getUOMList();
            getCropRequest();
            getAttributeList();
        } else {
            Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();

        }

        binding.etUomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //           if (position != 0) {
                str_uom = uomLines.get(position).getId() + "";
                binding.etUomSpinnType.setText(uomLines.get(position).getName());
//                    List<UomLineModel> str_uom = uom_model.get(position).getUomLines();
//                    if(model_uom_type.getUomCategories().get(position).getUomLines() != null) {
//                        uom_model_type.clear();
//                       UomCategoryModel stateModel = new UomCategoryModel();
//                        stateModel.setId(-1);
//                        stateModel.setName("--Select UOM--");
//                        uom_model_type.add(stateModel);
//                        for (int i = 0; i < model_uom_type.getUomCategories().get(position).getUomLines().size(); i++) {
//                            stateModel = new UomCategoryModel();
//                            stateModel.setId(model_uom_type.getUomCategories().get(position).getUomLines().get(i).getId());
//                            stateModel.setName(model_uom_type.getUomCategories().get(position).getUomLines().get(i).getName());
//                            uom_model_type.add(stateModel);
//                        }
//                        if (getContext() != null) {
//                            UOMSpinnerAdapter adapter = new UOMSpinnerAdapter(getContext(), uom_model_type);
//                            binding.etUomSpinnType.setAdapter(adapter);
//
//                        }
//
//
//                 //   }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        binding.etUomSpinnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) {
//                    str_uom_cate  = uom_model_type.get(position).getId() + "";
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        binding.mfdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date_mfd, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        binding.spinVendorTax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    vendor_tax = purchase_tax_list.get(position).getId() + "";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinCustomerTax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    customer_tax = sale_tax_list.get(position).getId() + "";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.expDateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date_exp_alarm, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        binding.expDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date_exp, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        date_mfd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        date_exp = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                ExpireLabel();
            }
        };
        date_exp_alarm = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                AlarmLabel();
            }
        };
        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();

            }
        });
        binding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideo();
            }
        });

        binding.selNonGov.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                // RadioButton rb = view.findViewById(checkedId);
                if (binding.govNonAuth.getText().toString().equalsIgnoreCase("Non-Authorized")) {
                    str_non_gov_product = "Non Gov-Authorized";
                } else if (binding.govAuth.getText().toString().equalsIgnoreCase("Gov-Authorized")) {
                    str_non_gov_product = "Gov-Authorized";
                }
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        str_mfd_date = sdf.format(myCalendar.getTime());
// SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yy");
// String date_e = sd.format(myCalendar.getTime());
        binding.mfdDate.setText(str_mfd_date);
    }

    private void ExpireLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        str_exp_date = sdf.format(myCalendar.getTime());
        binding.expDate.setText(str_exp_date);
    }

    private void AlarmLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str_exp_date = sdf.format(myCalendar.getTime());

        binding.expDateAlarm.setText(str_exp_date);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit_btn) {
            str_product_name = binding.etProductName.getText().toString();
            str_product_price = binding.etProductPrice.getText().toString();
            String str_techNamePest = binding.techNamePest.getText().toString();
            String str_brand_name = binding.brandName.getText().toString();
            String str_mkt_by = binding.mktBy.getText().toString();
            String str_batchNumber = binding.batchNumber.getText().toString();
            String str_cirNumber = binding.cirNumber.getText().toString();
            String str_whichPest = binding.whichPest.getText().toString();

            str_uom_cate = binding.etUomSpinnType.getText().toString();
            String str_description = binding.description.getText().toString();
            String str_hsn_code = binding.edHsnCode.getText().toString();
            String str_hsn_code_dec = binding.edHsnCodeDescription.getText().toString();

            if (str_product_name.length() == 0) {
                Toast.makeText(getActivity(), "Enter the Product Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (str_product_price.length() == 0) {
                Toast.makeText(getActivity(), "Enter the Product price", Toast.LENGTH_SHORT).show();
                return;
            }
            if(customer_tax.length() == 0){
                Toast.makeText(getActivity(),"Select Customer", Toast.LENGTH_SHORT).show();
                return;
            }
            if(vendor_tax.length() == 0){
                Toast.makeText(getActivity(), "Select Vendor", Toast.LENGTH_SHORT).show();
                return;
            }

//            if (str_uom_cate.length()==0) {
//                Toast.makeText(getActivity(), "Select UOM Category", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (binding.expDate.getText().toString().length() == 0) {
                Toast.makeText(getActivity(), " Select Expiry date", Toast.LENGTH_SHORT).show();
                return;
            }
//            if (binding.mfdDate.getText().toString().length() == 0) {
//                Toast.makeText(getActivity(), " Select Manufacturing date", Toast.LENGTH_SHORT).show();
//                return;
//            }
//

            if (!Utility.isNetworkAvailable(getActivity())) {
                Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray attribute_json_array = new JSONArray();
            selected_value_map.forEach((key, value) -> {
                if (value != null) {
                    JSONObject attribute_object = new JSONObject();
                    try {
                        ArrayList<Integer> numbers = new ArrayList<Integer>();
                        for (int i = 0; i < value.size(); i++) {
                            numbers.add(Integer.parseInt(value.get(i)));
                        }
                        attribute_object.put("attribute_id", Integer.parseInt(key));
                        attribute_object.put("value_ids", (new JSONArray(numbers)));
                        attribute_json_array.put(attribute_object);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


            Log.d("attribute_json_array", attribute_json_array.toString());
            if (attribute_json_array.length() != 0) {
                if (str_crop_id != null) {
                    if (str_image_crop != null) {
                        addProductRequest(vendor_tax, str_image_crop, str_hsn_code, str_hsn_code_dec, str_product_name, str_product_price, str_techNamePest, str_brand_name, str_mkt_by, str_batchNumber, str_cirNumber, str_whichPest, str_description, selected_crop_id, attribute_json_array);

                    } else {
                        Toast.makeText(getActivity(), "Select Product Image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Select Category", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Select Attribute", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void addProductRequest(String str_tax, String str_image_crop, String hsn_code, String str_hsn_code_dec, String product_name, String product_price, String str_techNamePest, String str_brand_name, String str_mkt_by, String str_batchNumber, String cir_no, String str_whichPest, String str_description, String selected_crop_id, JSONArray attribute_json_array) {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("token", sm.getJWTToken());
        params.put("name", product_name);
        params.put("description", str_description);
        params.put("list_price", product_price);
        params.put("which_pest", str_whichPest);
        params.put("non_gov_product", str_non_gov_product);
        params.put("exp_date", str_exp_date);
        params.put("expire_alarm", binding.expDateAlarm.getText().toString());
        params.put("uom_id", str_uom);
        params.put("uom_po_id", str_uom);
        params.put("taxes_id", customer_tax);
        params.put("supplier_taxes_id", vendor_tax);
        params.put("hsn_code", hsn_code);
        params.put("hsn_code_description", str_hsn_code_dec);
        params.put("detailed_type", str_detailed_type);
        params.put("image_data", str_image_crop);
        params.put("pos_categ_id", str_crop_id);
        params.put("attribute_lines", attribute_json_array.toString());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait while a moment...");
        progressDialog.setTitle("Registering");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        Log.d("url_addproduct", params.toString());
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.POST_ADD_PRODUCT, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String message_success = obj.optString("message");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    int visibility = (progressBar.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                    progressBar.setVisibility(visibility);
                    progressDialog.cancel();
                    Toast.makeText(getActivity(), message_success, Toast.LENGTH_SHORT).show();
                    resetFields();

                    Bundle bundle = new Bundle();
                    bundle.putString("product_list", "All_product");
                    Fragment fragment = AddProductListFragment.newInstance();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                progressDialog.cancel();
                Log.v("Response", result);
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetFields() {
        binding.etProductName.setText("");
        binding.etProductPrice.setText("");


    }

    private void takePhoto() {
        final CharSequence[] options = {"Take Photo", "Select photo from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    askForPermission("android.permission.CAMERA", 2);
                } else if (options[item].equals("Select photo from Gallery")) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Do something for lollipop and above versions
                        askForPermission(Manifest.permission.READ_MEDIA_IMAGES, 1);
                    } else {
                        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
                    }

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

//        final CharSequence[] options = {"Take Photo", "Select multiple photos from Gallery", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Add Photo!");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (options[item].equals("Take Photo")) {
//                    askForPermission("android.permission.CAMERA", 2);
//                } else if (options[item].equals("Select multiple photos from Gallery")) {
//                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//// Do something for lollipop and above versions
//                        askForPermission(Manifest.permission.READ_MEDIA_IMAGES, 1);
//                    } else {
//                        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
//                    }
//
//                } else if (options[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
    }

    private void getAttributeList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ATTRIBUTES_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
// String url = ApiConstants.BASE_URL + ApiConstants.GET_ATTRIBUTES_LIST;
        Log.d("product_list", url);
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
                        JSONArray jsonArray = obj.getJSONArray("attributes");

                        Gson gson = new Gson();
                        Type listType = new TypeToken<ListAttributesModel>() {
                        }.getType();

                        model_attribute = gson.fromJson(response.toString(), listType);
                        model.addAll(model_attribute.getAttributes());
                        createTextDynamically(model_attribute.getAttributes().size());

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }

    private void getVideo() {
        askForPermission("android.permission.READ_MEDIA_VIDEO", 3);

    }

    private void createTextDynamically(int n) {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        binding.linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        for (int j = 0; j < n; j++) {
            TextView text = new TextView(getActivity());
            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);
            TextView headingTV = new TextView(getActivity());
            headingTV.setTextSize(12f);
            headingTV.setTextColor(getResources().getColor(R.color.edt_top_text_color));
            headingTV.setTypeface(getResources().getFont(R.font.roboto_regular), Typeface.BOLD);

            headingTV.setPadding(0, 50, 12, 0);
            headingTV.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            headingTV.setLayoutParams(editTextParams);

            headingTV.setText("Select "+ model.get(j).getName());

            editTextParams.setMargins(20, -30, 20, 0);
            text.setLayoutParams(editTextParams);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
            text.setTextSize(16);
            text.setId(j);
            final int id_ = text.getId();
            int att_id = model.get(j).getId();
            selected_value_map.put(String.valueOf(att_id), null);

            text.setHint("Select the " + model.get(j).getName());

            // text.setText(name.get(1).getName());
            text.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            text.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.custom_edit_text_cut));
            // et.setEnabled(false);
            binding.linearLayoutMain.addView(headingTV);

            binding.linearLayoutMain.addView(text, editTextParams);
            SetDataTextDataDynamically(text, id_, model.get(j).getValues(), att_id);

        }

    }

    private void askForPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        } else {
            if (requestCode == 1) {
                fromGallery();
            } else if (requestCode == 2) {
                openCamera();
            }
        }
//        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
//        } else {
//            if (requestCode == 1) {
//                fromGallery();
//            } else if (requestCode == 2) {
//                openCamera();
//            } else if (requestCode == 3) {
//                openVideo();
//            }
//        }
    }

    private void fromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE);
//        binding.imageSet.setVisibility(View.GONE);
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_FROM_FILE);
    }

    /**
     * Open camera when user click on camera
     */
    private void openCamera() {
        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picture.resolveActivity(getActivity().getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();

            } catch (IOException ex) {
            }
            if (photo != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), photo);
                picture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(picture, PICK_FROM_CAMERA);
            }

        }
//        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (picture.resolveActivity(getActivity().getPackageManager()) != null) {
//            File photo = null;
//            try {
//                photo = createImageFile();
//
//            } catch (IOException ex) {
//            }
//            if (photo != null) {
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
//
//            }
//
//        }
    }

    /**
     * Open Video when user click on camera
     */
    private void openVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), PICK_FROM_VIDEO);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FROM_FILE) {
                Uri selectedImageUri = data.getData();

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                str_image_crop = getEncoded64ImageStringFromBitmap(imageBitmap);
                binding.image.setImageBitmap(imageBitmap);
            } else if (requestCode == PICK_FROM_CAMERA) {

                try {
                    //our imageFilePath that contains the absolute path to the created file
                    File file = new File(imageFilePath);
                    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));

                    if (!Utility.isNetworkAvailable(getActivity())) {
                        Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    str_image_crop = getEncoded64ImageStringFromBitmap(imageBitmap);
                    binding.image.setImageBitmap(imageBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bmp;
//
//        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK && null != data) {
//            Bundle extras1 = data.getExtras();
//            Bitmap thumbnail_1 = (Bitmap) extras1.get("data");
//            if (thumbnail_1 != null) {
//// imagesUriArrayList.add(new imagesUriArrayList.add(thumbnail_1));
//                imagesUriArrayList.add(thumbnail_1);
//
//            } else {
//                Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK && null != data) {
//            if (data.getClipData() == null) {
//                Toast.makeText(getActivity(), "Please select minimum 2 images", Toast.LENGTH_SHORT).show();
//
//            } else {
//                if (data.getClipData().getItemCount() <= 4) {
//
//                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
//
//                        try {
//
//                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getClipData().getItemAt(i).getUri());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//// String str_image_aa = getEncoded64ImageStringFromBitmap(bitmap);
//// api_array_list.add("data:image/jpeg;base64,"+str_image_aa);
//                        imagesUriArrayList.add(bitmap);
//                    }
//
//                    Log.e("SIZE_img", imagesUriArrayList.size() + "");
//                } else {
//                    Toast.makeText(getActivity(), "Please select maximum 4 images", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        }
//        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        adapter = new ImageAdapter(getActivity(), imagesUriArrayList);
//        binding.recyImage.setAdapter(adapter);
//        binding.recyImage.setLayoutManager(layoutManager);
//
//        if (requestCode == PICK_FROM_VIDEO) {
//            binding.simpleVideoView.setVisibility(View.VISIBLE);
//            Uri selectedImageUri = data.getData();
//// String selectedPath = getPath(selectedImageUri);
//            if (mediaControls == null) {
//// create an object of media controller class
//                mediaControls = new MediaController(getActivity());
//                mediaControls.setAnchorView(videoView);
//                video_text.setText(selectedImageUri.toString());
//            }
//// set the media controller for video view
//            binding.simpleVideoView.setMediaController(mediaControls);
//// set the uri for the video view
//            binding.simpleVideoView.setVideoURI(selectedImageUri);
//// start a video
//            binding.simpleVideoView.start();
//
//        } else if (resultCode == getActivity().RESULT_CANCELED) {
//            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
//        }
//    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteFormat = stream.toByteArray();
        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void getCropRequest() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Value cropPattern = new Value();
                                JSONObject data = jsonArray.getJSONObject(i);
                                Integer cropID = data.optInt("category_id");
                                String name = data.optString("name");
                                cropPattern.setValueId(Integer.valueOf(cropID + ""));
                                cropPattern.setValueName(name);

                                cropList.add(cropPattern);
                            }
                            Log.d("url_string", String.valueOf(cropList.size()));
                            setWhichCrop(cropList);

                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    private void SetDataTextDataDynamically(TextView text_view, int text_id, List<AttributeValue> attribute_value, int att_id) {
        ArrayList<String> attribute_name = new ArrayList<>();
        ArrayList<String> attribute_id = new ArrayList<>();
        ArrayList<Integer> langList = new ArrayList<>();
        for (int i = 0; i < attribute_value.size(); i++) {
            attribute_name.add(attribute_value.get(i).getName());
            attribute_id.add(String.valueOf(attribute_value.get(i).getId()));
        }
        final CharSequence[] items_value = attribute_name.toArray(new CharSequence[attribute_name.size()]);
        boolean[] selected_value = new boolean[attribute_name.size()];


        text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Attribute value");
                builder.setCancelable(false);
                selected_value_map.put(String.valueOf(att_id), null);
                builder.setMultiChoiceItems(items_value, selected_value, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            langList.add(i);
                            Collections.sort(langList);
                        } else {

                            langList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
// use for loop
                        int attribute_value_id = 0;
                        List<String> item_value_list = null;
                        for (int j = 0; j < langList.size(); j++) {
                            stringBuilder.append(items_value[langList.get(j)]);
                            attribute_value_id = Integer.parseInt(attribute_id.get(langList.get(j)));

                            item_value_list = selected_value_map.get(String.valueOf(att_id));
                            if (item_value_list != null) {
                                item_value_list.add(String.valueOf(attribute_value_id));
                                selected_value_map.put(String.valueOf(att_id), item_value_list);
                            } else {
                                item_value_list = new ArrayList<>();
                                item_value_list.add(String.valueOf(attribute_value_id));
                                selected_value_map.put(String.valueOf(att_id), item_value_list);

                            }
                            Log.d("selected_value_map", selected_value_map + "");
                            if (j != langList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        attribute_id_list.add(attribute_value_id);

// set text on textView
                        text_view.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// use for loop
                        for (int j = 0; j < selected_value.length; j++) {
// remove all selection
                            selected_value[j] = false;
// clear language list
                            langList.clear();
// clear text view value
                            text_view.setText("");
                        }
                    }
                });

                builder.show();
            }
        });
    }


    private void setWhichCrop(List<Value> cropList) {
        ArrayList<String> crop_name = new ArrayList<>();
        ArrayList<String> crop_id = new ArrayList<>();
        ArrayList<Integer> langList = new ArrayList<>();
        for (int i = 0; i < cropList.size(); i++) {
            crop_name.add(cropList.get(i).getValueName());
            crop_id.add(String.valueOf(cropList.get(i).getValueId()));
        }
        final CharSequence[] items = crop_name.toArray(new CharSequence[crop_name.size()]);
        boolean[] selected_crop = new boolean[crop_name.size()];


        binding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Category Name");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(items, selected_crop, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {

                            langList.add(i);
                            Collections.sort(langList);
                        } else {

                            langList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        StringBuilder builder_crop_id = new StringBuilder();
                        for (int j = 0; j < langList.size(); j++) {
                            stringBuilder.append(items[langList.get(j)]);
                            builder_crop_id.append(crop_id.get(langList.get(j)));
                            if (j != langList.size() - 1) {
                                stringBuilder.append(", ");
                                builder_crop_id.append(",");
                            }
                        }

                        str_crop_id = builder_crop_id.toString();
                        Log.d("str_crop_id", str_crop_id);
                        binding.textView.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
// use for loop
                        for (int j = 0; j < selected_crop.length; j++) {
                            selected_crop[j] = false;
                            langList.clear();
                            binding.textView.setText("");
                        }
                    }
                });

                builder.show();
            }
        });
    }

    private void getUOMList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_UOM_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        //    Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.d("ALL_CROPS_url", url);
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
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ProductDetail>() {
                        }.getType();
                        model_uom_type = gson.fromJson(response.toString(), listType);
//                        UomCategoryModel stateModel = new UomCategoryModel();
//                        stateModel.setId(-1);
//                        stateModel.setName("--Select UOM--");
//                        uom_model_type.add(stateModel);
                        if (model_uom_type.getUomCategories() == null || model_uom_type.getUomCategories().size() == 0) {

                        } else {
                            for (int i = 0; i < model_uom_type.getUomCategories().size(); i++) {
                                for (int j = 0; j < model_uom_type.getUomCategories().get(i).getUomLines().size(); j++) {
                                    uom_model_list = new UomLineModel();
                                    uom_model_list.setId(model_uom_type.getUomCategories().get(i).getUomLines().get(j).getId());
                                    uom_model_list.setName(model_uom_type.getUomCategories().get(i).getUomLines().get(j).getName());
                                    uomLines.add(uom_model_list);
                                }


                            }

                            UOMSpinnerAdapter adapter = new UOMSpinnerAdapter(getContext(), uomLines);
                            binding.etUomSpinner.setAdapter(adapter);
                        }


                        // Utility.dismissDialoge();
//                        JSONArray jsonArray = obj.getJSONArray("uom_categories");
//                        UomCategoryModel stateModel = new UomCategoryModel();
//                        stateModel.setId(-1);
//                        stateModel.setName("--Select UOM--");
//                        uom_model.add(stateModel);
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            stateModel = new UomCategoryModel();
//                            JSONObject data = jsonArray.getJSONObject(i);
//                            int id = data.optInt("id");
//                            String name = data.optString("name");
//                            String uom_lines = data.op("uom_lines");
//                            stateModel.setId(id);
//                            stateModel.setName(name);
//                            stateModel.setUomLines(uom_lines);
//                            uom_model.add(stateModel);
//                        }
////                        if(getContext()!=null) {
////                            UOMSpinnerAdapter adapter = new UOMSpinnerAdapter(getContext(), uom_model);
////                            binding.etUomSpinner.setAdapter(adapter);
////                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    private void getTaxList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GST_API + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Log.d("gst_d", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                purchase_tax_list = new ArrayList<>();
                sale_tax_list = new ArrayList<>();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = obj.getJSONArray("gst_tax_list");
                        StateModel stateModel = new StateModel();
                        stateModel.setId(-1);
                        stateModel.setName("--Select tax--");
                        purchase_tax_list.add(stateModel);
                        sale_tax_list.add(stateModel);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                stateModel = new StateModel();
                                JSONObject data = jsonArray.getJSONObject(i);
                                int id = data.optInt("id");
                                String name = data.optString("name");
                                String tax_type = data.optString("tax_type");
                                if(tax_type.equals("purchase")){
                                    stateModel.setId(id);
                                    stateModel.setName(name);
                                    purchase_tax_list.add(stateModel);
                                }else if(tax_type.equals("sale")){
                                    stateModel.setId(id);
                                    stateModel.setName(name);
                                    sale_tax_list.add(stateModel);
                                }

                            }
                            if (getContext() != null) {
                                CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), purchase_tax_list);
                                binding.spinVendorTax.setAdapter(adapter);

                                CustomSpinnerAdapter sale_adapter = new CustomSpinnerAdapter(getContext(), sale_tax_list);
                                binding.spinCustomerTax.setAdapter(sale_adapter);

                            }
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