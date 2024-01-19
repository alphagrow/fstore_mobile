package com.growit.posapp.fstore.ui.fragments.AddProduct;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ImageAdapter;


import com.growit.posapp.fstore.adapters.UOMSpinnerAdapter;
import com.growit.posapp.fstore.databinding.FragmentUpdateAddProductBinding;
import com.growit.posapp.fstore.model.Attribute;
import com.growit.posapp.fstore.model.AttributeModel;
import com.growit.posapp.fstore.model.AttributeValue;
import com.growit.posapp.fstore.model.ListAttributesModel;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.Purchase.PurchaseProductModel;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.model.UomCategoryModel;
import com.growit.posapp.fstore.model.UomLineModel;
import com.growit.posapp.fstore.model.Value;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class UpdateAddProductFragment extends Fragment implements View.OnClickListener {
    private EditText et_product_name, et_product_price, et_uom, et_size, et_color, et_whole_pattern,
            tech_name_pest,brand_name,mkt_by,batch_number,cir_number,which_crop,which_pest,packing_std;
    private ImageView product_image,image_set;
    private Button submit_btn;

    ArrayList<String> api_array_list;

    ArrayList<String> cammer_api_array_list;
    LinearLayout layout;
    ArrayList<Bitmap> imagesUriArrayList;
    private ProgressBar progressBar;
    private ImageView imageView, video_image;
    private VideoView videoView;
    String str_product_name, str_product_price, str_uom, str_uom_cate,str_tax, str_color, str_whole_pattern;
    String imageFilePath;
    ProgressBar idPBLoading;
    private TextView video_text;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_VIDEO = 4;
    private  String str_image_aa;
    ProgressDialog progressDialog;
    MediaController mediaControls;
    private RecyclerView recy_image;
    Bitmap bitmap = null;
    RadioGroup sel_non_gov;
    ImageAdapter adapter;
    TextView mfd_date,exp_date,exp_date_alarm;
    String str_detailed_type;
    private String[] detailed_type = {"product"};

    DatePickerDialog.OnDateSetListener date_mfd,date_exp,date_exp_alarm;
    final Calendar myCalendar = Calendar.getInstance();
    FragmentUpdateAddProductBinding binding;
    Map<String, List<String>> selected_value_map = new HashMap<>();
    List<PurchaseProductModel> list=null;
    int  position;
    String product_id;
    String crop_id,str_crop_name;
    Activity contexts;
    List<AttributeModel> model = new ArrayList<>();
    ArrayList<Integer> attribute_id_list = new ArrayList<>();
    ListAttributesModel model_attribute;
    String str_non_gov_product="Non-Gov";
    List<Value> cropList = new ArrayList<>();
    List<Attribute> attributes;
    //    private static final int SELECT_VIDEO = 3;
    ArrayList<String>crop_id_list = new ArrayList<>();
   String str_crop_id;
    ArrayList<Integer>sel_value_id = new ArrayList<>();
    ProductDetail model_uom_type;
    List<UomCategoryModel> uom_model_type = new ArrayList<>();
    UomLineModel uom_model_list;
    List<UomLineModel> uomLines= new ArrayList<>();
    public UpdateAddProductFragment() {
        // Required empty public constructor
    }

    public static UpdateAddProductFragment newInstance() {
        return new UpdateAddProductFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // R.layout.fragment_update_add_product
        binding= FragmentUpdateAddProductBinding.inflate(inflater, container, false);
        imagesUriArrayList = new ArrayList();
        init();
        return binding.getRoot();
    }

    private void init() {

        if (getArguments() != null) {
            list = (List<PurchaseProductModel>) getArguments().getSerializable("product_list");
            int position = getArguments().getInt("position");
            str_crop_id = String.valueOf(getArguments().getInt("crop_id"));
            str_crop_name = getArguments().getString("crop_name");
            int value_id=0;
            List<Attribute> attributeValue_list= list.get(position).getAttributes();
            for (int i=0;i<attributeValue_list.size();i++){
                for (int j=0;j<attributeValue_list.get(i).getValues().size();j++) {
                 value_id = attributeValue_list.get(i).getValues().get(j).getValueId();
                }
                sel_value_id.add(value_id);
            }

            Log.d("sel_value",sel_value_id.toString());
            product_id= String.valueOf(list.get(position).getProductId());
            binding.etProductName.setText(list.get(position).getProductName());
            binding.etProductPrice.setText(String.valueOf(list.get(position).getListPrice()));
            binding.packingStd.setText(list.get(position).getProductPackage());
//            binding.whichCrop.setText(str_crop_name);
            binding.whichPest.setText(list.get(position).getWhichPest());
            binding.expDate.setText(list.get(position).getExpDate());
            binding.description.setText(list.get(position).getDescription());
//            binding.ediTax.setText(list.get(position).getTaxesId());
             attributes = list.get(position).getAttributes();
             Log.d("uom_id_f",list.get(position).getUomId()+"FFFF  "+list.get(position).getUomPoId());

            if (Utility.isNetworkAvailable(getContext())) {
                getUOMList();
               getCropRequest();
                getAttributeList();
            } else {
                Toast.makeText(contexts, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
            }

        }
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        binding.updateBtn.setOnClickListener(this);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, detailed_type);
        binding.detailTypeSpinner.setAdapter(spinnerArrayAdapter);
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


        binding.expDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(contexts, date_exp, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
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
        binding.etUomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //           if (position != 0) {
                str_uom = uomLines.get(position).getId() + "";
                binding.etUomSpinnType.setText(uomLines.get(position).getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                //  RadioButton rb = view.findViewById(checkedId);
                if (binding.govNonAuth.getText().toString().equalsIgnoreCase("Non-Gov")) {

                } else if (binding.govAuth.getText().toString().equalsIgnoreCase("Gov Authorized")) {

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

        binding.selNonGov.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                //  RadioButton rb = view.findViewById(checkedId);
                if (binding.govNonAuth.getText().toString().equalsIgnoreCase("Non-Gov")) {
                    str_non_gov_product = "Non-Gov";
                } else if (binding.govAuth.getText().toString().equalsIgnoreCase("Gov Authorized")) {
                    str_non_gov_product = "Gov Authorized";
                }
            }
        });

    }


    private void ExpireLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String   str_exp_date = sdf.format(myCalendar.getTime());
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String date_e = sd.format(myCalendar.getTime());
        binding.expDate.setText(date_e);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.update_btn) {
            str_product_name = binding.etProductName.getText().toString();
            str_product_price = binding.etProductPrice.getText().toString();
            str_tax = binding.ediTax.getText().toString();
            String str_whichPest = binding.whichPest.getText().toString();
            String str_description = binding.description.getText().toString();
            String str_exp_date = binding.expDate.getText().toString();

            //   String str_uomProduct = binding.etUomProduct.getText().toString();

//            if (str_product_name.length() == 0 || str_product_price.length() == 0 || str_uom.length() == 0) {
//                Toast.makeText(getActivity(), "Please add mandatory fields.", Toast.LENGTH_SHORT).show();
//                return;
//            }

            if (str_tax.length()==0) {
                Toast.makeText(getActivity(), "Enter the Product tax", Toast.LENGTH_SHORT).show();
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
            Log.d("crop_id_list",crop_id_list.toString());
            Log.d("attribute_json_array", attribute_json_array.toString());


            if (!Utility.isNetworkAvailable(contexts)) {
                Toast.makeText(contexts, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                return;
            }

            if (attribute_json_array != null) {
                if (str_crop_id != null) {
                    updateProductRequest(str_crop_id,str_product_name, str_product_price,str_whichPest,str_description,str_exp_date,attribute_json_array);
                } else {
                    Toast.makeText(getActivity(), "Select Category", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Select Attribute", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateProductRequest(String crop_id_list,String str_product_name,String str_product_price,String str_whichPest,String str_description,String str_exp_date,JSONArray attribute_json_array) {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("token", sm.getJWTToken());
        params.put("name", str_product_name);
        params.put("description", str_description);
        params.put("list_price", str_product_price);
        params.put("which_pest", str_whichPest);
        params.put("non_gov_product", str_non_gov_product);
        params.put("exp_date", str_exp_date);
        params.put("detailed_type", str_detailed_type);
        params.put("attribute_lines", attribute_json_array.toString());
        params.put("product_id", product_id);
        //       params.put("taxes_id", str_tax);

        params.put("uom_id", str_uom);
        params.put("uom_po_id", str_uom);
        params.put("pos_categ_id", crop_id_list+"");



        Log.d("update_product",params.toString());

        new VolleyRequestHandler(getActivity(), params).putRequest(ApiConstants.PUT_UPDATE_PRODUCT, new VolleyCallback() {
            private String message = "Update failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                JSONObject obj = new JSONObject(result.toString());

                Log.d("result",result.toString());
                String status = obj.optString("status");
                message = obj.optString("message");
                String error_message = obj.optString("error_message");
                if (status.equalsIgnoreCase("success")) {
                    Toast.makeText(getActivity(), "Update Product", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("product_list", "All_product");
                    Fragment fragment = AddProductListFragment.newInstance();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }else {
                    Toast.makeText(contexts, error_message, Toast.LENGTH_SHORT).show();
                    Log.v("error_message", error_message.toString());
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Log.v("Response", result.toString());
                Toast.makeText(contexts, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        contexts = getActivity();
        //  hideKeyboard(activity);
    }
    private void takePhoto() {
        final CharSequence[] options = {"Take Photo", "Select multiple photos from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    askForPermission("android.permission.CAMERA", 2);
                } else if (options[item].equals("Select multiple photos from Gallery")) {
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
    }


    private void getVideo(){
        askForPermission("android.permission.READ_MEDIA_VIDEO", 3);

    }
    private void askForPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(contexts, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(contexts, new String[]{permission}, requestCode);
        } else {
            if (requestCode == 1) {
                fromGallery();
            } else if (requestCode == 2) {
                openCamera();
            }else if (requestCode == 3) {
                openVideo();
            }
        }
    }

    private void fromGallery() {
        binding.imageSet.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_FILE);
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
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
//                Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName(), photo);
//                picture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(picture, PICK_FROM_CAMERA);
            }

        }
    }
    /**
     * Open Video when user click on camera
     */
    private void openVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a Video "), PICK_FROM_VIDEO);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bmp;

        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK && null != data) {
            Bundle extras1 = data.getExtras();
            Bitmap thumbnail_1 = (Bitmap) extras1.get("data");
            if (thumbnail_1 != null) {
                //  imagesUriArrayList.add(new imagesUriArrayList.add(thumbnail_1));
                imagesUriArrayList.add(thumbnail_1);

            } else {
                Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_FROM_FILE && resultCode == RESULT_OK && null != data) {
            if(data.getClipData()== null){
                Toast.makeText(getActivity(), "Please select minimum 2 images", Toast.LENGTH_SHORT).show();

            }else {
                if (data.getClipData().getItemCount() <= 4) {

                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {

                        try {

                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getClipData().getItemAt(i).getUri());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                    String str_image_aa = getEncoded64ImageStringFromBitmap(bitmap);
//                    api_array_list.add("data:image/jpeg;base64,"+str_image_aa);
                        imagesUriArrayList.add(bitmap);
                    }

                    Log.e("SIZE_img", imagesUriArrayList.size() + "");
                } else {
                    Toast.makeText(getActivity(), "Please select maximum 4 images", Toast.LENGTH_SHORT).show();

                }
            }
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new ImageAdapter(getActivity(), imagesUriArrayList);
        binding.recyImage.setAdapter(adapter);
        binding.recyImage.setLayoutManager(layoutManager);

        if (requestCode == PICK_FROM_VIDEO) {
            binding.simpleVideoView.setVisibility(View.VISIBLE);
            Uri selectedImageUri = data.getData();
            //  String    selectedPath = getPath(selectedImageUri);
            if (mediaControls == null) {
                // create an object of media controller class
                mediaControls = new MediaController(getActivity());
                mediaControls.setAnchorView(videoView);
                video_text.setText(selectedImageUri.toString());
            }
            // set the media controller for video view
            binding.simpleVideoView.setMediaController(mediaControls);
            // set the uri for the video view
            binding.simpleVideoView.setVideoURI(selectedImageUri);
            // start a video
            binding.simpleVideoView.start();

        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

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
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void getAttributeList() {
        SessionManagement sm = new SessionManagement(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ATTRIBUTES_LIST + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        //  String url = ApiConstants.BASE_URL + ApiConstants.GET_ATTRIBUTES_LIST;
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
                        createTextDynamically(model_attribute.getAttributes());

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);

    }
//    private void createTextDynamically(int n) {
//        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = display.getWidth();
//        LinearLayout l = new LinearLayout(getActivity());
//        binding.linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
//        for (int j = 0; j < n; j++) {
//            TextView text = new TextView(getActivity());
//            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);
//
//            editTextParams.setMargins(20, 20, 20, 0);
//            text.setLayoutParams(editTextParams);
//            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
//            text.setTextSize(16);
//            text.setId(j);
//            final int id_ = text.getId();
//            int att_id = model.get(j).getId();
//            selected_value_map.put(String.valueOf(att_id), null);
//
//            text.setHint("Select the " + model.get(j).getName());
//
//            // text.setText(name.get(1).getName());
//            text.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
//
//            text.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.custom_edit_text_cut));
//            // et.setEnabled(false);
//            binding.linearLayoutMain.addView(text, editTextParams);
//
//            SetDataTextDataDynamically(text, id_, model.get(j).getValues(), att_id);
//
//        }
//
//    }


    private void createTextDynamically(List<AttributeModel> model) {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        LinearLayout l = new LinearLayout(getActivity());
        binding.linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        for (int j = 0; j < model.size(); j++) {
            TextView text = new TextView(getActivity());
            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);

            editTextParams.setMargins(20, 20, 20, 0);
            text.setLayoutParams(editTextParams);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
            text.setTextSize(16);
            text.setId(j);
            final int id_ = text.getId();
            int att_id = model.get(j).getId();
            selected_value_map.put(String.valueOf(att_id), null);
            text.setHint("Select the " + model.get(j).getName());

            ////set data in textview
            int attribute_value_id=0;
            List<String> item_value_list = null;
            StringBuilder stringBuilder = new StringBuilder();

//            for (int i = 0; i < model.get(j).getValues().size(); i++) {
//                if(attributes.get(j).getValues() !=null&&attributes.get(j).getValues().size()==0) {
//                    if (model.get(j).getValues().get(i).getId().equals(attributes.get(j).getValues().get(i).getValueId())) {
//                        item_value_list = selected_value_map.get(String.valueOf(att_id));
//                        attribute_value_id = attributes.get(j).getValues().get(i).getValueId();
//                        stringBuilder.append(attributes.get(j).getValues().get(i).getValueName());
//                        Log.d("stringBuilder",stringBuilder.toString());
//                        if (item_value_list != null) {
//                            item_value_list.add(String.valueOf(attribute_value_id));
//                            selected_value_map.put(String.valueOf(att_id), item_value_list);
//                        } else {
//                            item_value_list = new ArrayList<>();
//                            item_value_list.add(String.valueOf(attribute_value_id));
//                            selected_value_map.put(String.valueOf(att_id), item_value_list);
//
//                        }
//                        if (i != attributes.get(j).getValues().size() - 1) {
//                            stringBuilder.append(", ");
//                        }
//                        attribute_id_list.add(attribute_value_id);
//                        text.setHint(stringBuilder);
//                    }
//                }
//            }

            Log.d("set_attribute_json_array", String.valueOf(stringBuilder));


            text.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            text.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.custom_edit_text_cut));
            //  et.setEnabled(false);
            binding.linearLayoutMain.addView(text, editTextParams);

            SetDataTextDataDynamically(text, id_, model.get(j).getValues(), att_id);

        }

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
    private void getCropRequest() {
        SessionManagement sm = new SessionManagement(contexts);
        RequestQueue queue = Volley.newRequestQueue(contexts);
        String url = ApiConstants.BASE_URL + ApiConstants.GET_ALL_CROPS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Utility.showDialoge("Please wait while a moment...", contexts);

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
        }, error -> Toast.makeText(contexts, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
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
          binding.textView.setText(str_crop_name);

        binding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Crop Name");
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
        Log.d("ALL_CROPS_url",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                JSONObject obj = null;
                int spinnerPosition = 0;
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

                        }else {
                            for (int i=0;i<model_uom_type.getUomCategories().size();i++){
                                for (int j=0;j<model_uom_type.getUomCategories().get(i).getUomLines().size();j++){
                                    uom_model_list=new UomLineModel();
                                    uom_model_list.setId(model_uom_type.getUomCategories().get(i).getUomLines().get(j).getId());
                                    uom_model_list.setName(model_uom_type.getUomCategories().get(i).getUomLines().get(j).getName());
                                    if (String.valueOf(model_uom_type.getUomCategories().get(i).getUomLines().get(j).getId()).equalsIgnoreCase(list.get(position).getUomId())) {
                                        spinnerPosition = j;
                                    }
                                    uomLines.add(uom_model_list);
                                }


                            }

                            UOMSpinnerAdapter adapter = new UOMSpinnerAdapter(getContext(), uomLines);
                            binding.etUomSpinner.setAdapter(adapter);
                            binding.etUomSpinner.setSelection(spinnerPosition);
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