package com.growit.posapp.fstore.ui;

import static android.view.View.GONE;
import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.adapters.CustomSpinnerAdapter;
import com.growit.posapp.fstore.databinding.ActivityUserRegistrstionBinding;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.model.DataPart;
import com.growit.posapp.fstore.model.StateModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyMultipartRequest;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserRegistrationActivity extends AppCompatActivity {
    ActivityUserRegistrstionBinding binding;
    List<StateModel> stateNames = new ArrayList<>();
    List<StateModel> districtNames = new ArrayList<>();
    List<StateModel> talukaNames = new ArrayList<>();
    private String login_id = "", str_password = "", str_comp_name = "", str_city = "", str_phone = "", str_website = "", str_insectLicNo = "", str_seedLicNo = "", str_fertLicNo = "", str_gst_no = "", nameStr = "", mobileStr = "", emailStr = "", districtStr = "", streetStr = "", zipStr = "", stateStr = "", talukaStr = "",user_email_str="";
    boolean isAllFieldsChecked = false;
    String update_companyProfile;
    int state, district, taluka;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    String imageFilePath;
    Bitmap imageBitmap = null;
    String str_image_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_user_registrstion);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        binding = DataBindingUtil.setContentView(UserRegistrationActivity.this, R.layout.activity_user_registrstion);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        update_companyProfile = intent.getStringExtra("company_profile");
        if (update_companyProfile.equals("update_company_profile")) {
            binding.titleTxt.setText("Update Company Detail");
            binding.updateBtn.setVisibility(View.VISIBLE);
            binding.submitBtn.setVisibility(GONE);
            binding.layoutUserDetail.setVisibility(GONE);
            binding.imageLay.setVisibility(View.VISIBLE);
            if (Utility.isNetworkAvailable(UserRegistrationActivity.this)) {

                getCompanyDetails();

            } else {
                Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
            }

        } else {
            if (Utility.isNetworkAvailable(UserRegistrationActivity.this)) {

                getStateData("0");

            } else {
                Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
            }
        }


        StateModel st = new StateModel();
        st.setName("--Select District--");
        districtNames.add(st);

        StateModel st2 = new StateModel();
        st2.setName("--Select Taluka--");
        talukaNames.add(st2);
        if (getContext() != null) {
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UserRegistrationActivity.this, districtNames);
            binding.citySpinner.setAdapter(adapter);
        }
        if (getContext() != null) {
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UserRegistrationActivity.this, talukaNames);
            binding.talukaSpinner.setAdapter(adapter);
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (update_companyProfile.equals("update_company_profile")) {
                    startActivity(new Intent(UserRegistrationActivity.this, MyProfileActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(UserRegistrationActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
        binding.imageLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();

            }
        });


        binding.citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    districtStr = districtNames.get(position).getId() + "";
                    if (districtStr != null) {
                        if (!Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
                            Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getTalukaData(districtStr);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    stateStr = stateNames.get(position).getId() + "";
                    if (stateStr != null) {
                        if (!Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
                            Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getDistrictData(stateStr);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.talukaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    talukaStr = talukaNames.get(position).getId() + "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_gst_no = binding.etGstNo.getText().toString();
                nameStr = binding.etUsername.getText().toString();
                user_email_str = binding.edUserEmail.getText().toString();
                mobileStr = binding.etMobile.getText().toString();
                emailStr = binding.etUseremail.getText().toString();
                zipStr = binding.etPincode.getText().toString();
                streetStr = binding.etUseraddress.getText().toString();
                login_id = binding.edUserId.getText().toString();
                str_password = binding.etPassword.getText().toString();
                str_comp_name = binding.etCompanyName.getText().toString();
                str_city = binding.etCity.getText().toString();
                str_phone = binding.etPhone.getText().toString();
                str_website = binding.etWebsite.getText().toString();
                str_insectLicNo = binding.etInsectLicNo.getText().toString();
                str_fertLicNo = binding.etFertLicNo.getText().toString();
                str_seedLicNo = binding.etSeedLicNo.getText().toString();

                if (stateStr.length() == 0) {
                    Toast.makeText(UserRegistrationActivity.this, R.string.Select_state, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (districtStr.length() == 0) {
                    Toast.makeText(UserRegistrationActivity.this, "Select District", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (talukaStr.length() == 0) {
                    Toast.makeText(UserRegistrationActivity.this, "Select Taluka", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
                    Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }


                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
//                    Log.d("imageBitmap", imageBitmap.toString());
   //                 if (imageBitmap != null) {
//                        UserRegistration(imageBitmap);
                    UserRegistration();
//                    } else {
//                        Toast.makeText(UserRegistrationActivity.this, "Select logo", Toast.LENGTH_SHORT).show();
//
//                    }

                }
            }
        });
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_gst_no = binding.etGstNo.getText().toString();
                nameStr = binding.etUsername.getText().toString();
                mobileStr = binding.etMobile.getText().toString();
                emailStr = binding.etUseremail.getText().toString();
                zipStr = binding.etPincode.getText().toString();
                streetStr = binding.etUseraddress.getText().toString();
                login_id = binding.edUserId.getText().toString();
                str_password = binding.etPassword.getText().toString();
                str_comp_name = binding.etCompanyName.getText().toString();
                str_city = binding.etCity.getText().toString();
                str_phone = binding.etPhone.getText().toString();
                str_website = binding.etWebsite.getText().toString();
                str_insectLicNo = binding.etInsectLicNo.getText().toString();
                str_fertLicNo = binding.etFertLicNo.getText().toString();
                str_seedLicNo = binding.etSeedLicNo.getText().toString();

                if (stateStr.length() == 0) {
                    Toast.makeText(UserRegistrationActivity.this, R.string.Select_state, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
                    Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
                    //          if (imageBitmap !=null) {
                    UpdateUserRegistration(imageBitmap);
//                    }else {
//                        Toast.makeText(UserRegistrationActivity.this, "Select logo", Toast.LENGTH_SHORT).show();
//
//                    }
                }


            }
        });

    }

    private void takePhoto() {
        final CharSequence[] options = {"Take Photo", "Select  photos from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserRegistrationActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    askForPermission(Manifest.permission.CAMERA, 2);
                } else if (options[item].equals("Select  photos from Gallery")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

    private void getStateData(String state) {
        Utility.showDialoge("Please wait while a configuring State...", UserRegistrationActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("country_id", ApiConstants.COUNTRY_ID);
        new VolleyRequestHandler(UserRegistrationActivity.this, params).createRequest(ApiConstants.GET_STATES, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                stateNames = new ArrayList<>();
                int spinnerPosition = 0;
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select State--");
                    stateNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        String name = data.optString("name");
                        if (update_companyProfile.equals("update_company_profile")) {
                            if (String.valueOf(id).equalsIgnoreCase(state)) {
                                spinnerPosition = i + 1;
                            }
                        }
                        stateModel.setId(id);
                        stateModel.setName(name);
                        stateNames.add(stateModel);
                    }
                    if (getContext() != null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UserRegistrationActivity.this, stateNames);
                        binding.stateSpinner.setAdapter(adapter);
                        binding.stateSpinner.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Utility.dismissDialoge();
                Toast.makeText(UserRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void askForPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(UserRegistrationActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserRegistrationActivity.this, new String[]{permission, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, requestCode);
        } else {
            if (requestCode == 1) {
                fromGallery();
            } else if (requestCode == 2) {
                openCamera();
            }
        }
    }

    private void fromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE);
    }

    /**
     * Open camera when user click on camera
     */
    private void openCamera() {
        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picture.resolveActivity(getPackageManager()) != null) {
            File photo = null;
            try {
                photo = createImageFile();

            } catch (IOException ex) {
            }
            if (photo != null) {
                Uri photoURI = FileProvider.getUriForFile(this, getPackageName(), photo);
                picture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(picture, PICK_FROM_CAMERA);
            }

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void getDistrictData(String stateStr) {
        Utility.showDialoge("Please wait while a configuring District...", UserRegistrationActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("states_id", stateStr);
        new VolleyRequestHandler(UserRegistrationActivity.this, params).createRequest(ApiConstants.GET_DISTRICT, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                districtNames = new ArrayList<>();
                int spinnerPosition = 0;
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select District--");
                    districtNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        String name = data.optString("name");
                        if (update_companyProfile.equals("update_company_profile")) {
                            if (String.valueOf(id).equalsIgnoreCase(String.valueOf(district))) {
                                spinnerPosition = i + 1;
                            }
                        }
                        stateModel.setId(id);
                        stateModel.setName(name);
                        districtNames.add(stateModel);
                    }
                    if (getContext() != null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UserRegistrationActivity.this, districtNames);
                        binding.citySpinner.setAdapter(adapter);
                        binding.citySpinner.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                Utility.dismissDialoge();
                Toast.makeText(UserRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTalukaData(String districtStr) {
        Utility.showDialoge("Please wait while a configuring Taluka...", UserRegistrationActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("district_id", districtStr);
        new VolleyRequestHandler(UserRegistrationActivity.this, params).createRequest(ApiConstants.GET_TALUKA, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                talukaNames = new ArrayList<>();
                int spinnerPosition = 0;
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    JSONArray jsonArray = obj.getJSONArray("data");
                    StateModel stateModel = new StateModel();
                    stateModel.setId(-1);
                    stateModel.setName("--Select Taluka--");
                    talukaNames.add(stateModel);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stateModel = new StateModel();
                        JSONObject data = jsonArray.getJSONObject(i);
                        int id = data.optInt("id");
                        String name = data.optString("name");
                        if (update_companyProfile.equals("update_company_profile")) {
                            if (String.valueOf(id).equalsIgnoreCase(String.valueOf(taluka))) {
                                spinnerPosition = i + 1;
                            }
                        }
                        stateModel.setId(id);
                        stateModel.setName(name);
                        talukaNames.add(stateModel);
                    }
                    if (getContext() != null) {
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(UserRegistrationActivity.this, talukaNames);
                        binding.talukaSpinner.setAdapter(adapter);
                        binding.talukaSpinner.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onError(String result) {
                Utility.dismissDialoge();
                Log.v("Response", result.toString());
                Toast.makeText(UserRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean CheckAllFields() {

        if (update_companyProfile.equals("update_company_profile")) {

            if (binding.etCompanyName.length() == 0) {
                binding.etCompanyName.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the company Name", Toast.LENGTH_SHORT).show();

                return false;
            }
            if (binding.etPhone.length() == 0) {
                binding.etPhone.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the Phone Number", Toast.LENGTH_SHORT).show();

                return false;
            }
            if (binding.etCity.length() == 0) {
                binding.etCity.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the City", Toast.LENGTH_SHORT).show();
                return false;
            }
//        if (binding.etWebsite.length() == 0) {
//            binding.etWebsite.setError("This field is required");
//            Toast.makeText(UserRegistrationActivity.this, "Enter the Website", Toast.LENGTH_SHORT).show();
//            return false;
//        }

            if (binding.etMobile.length() != 10) {
                binding.etMobile.setError("Enter a 10-digit mobile number");
                Toast.makeText(UserRegistrationActivity.this, R.string.customer_mobile, Toast.LENGTH_SHORT).show();
                return false;
            }


            if (binding.etUseraddress.length() == 0) {
                binding.etUseraddress.setError("Address is required");
                Toast.makeText(UserRegistrationActivity.this, R.string.ADDRESS, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.etPincode.length() != 6) {
                binding.etPincode.setError("Enter a 6-digit Pin code");
                Toast.makeText(UserRegistrationActivity.this, R.string.PIN_CODE, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.etGstNo.length() == 0) {
                binding.etGstNo.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter valid GST No.", Toast.LENGTH_SHORT).show();
                return false;
            }


        } else {

            if (binding.etUsername.length() == 0) {
                binding.etUsername.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, R.string.CUSTOMER_NAME, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.edUserId.length() == 0) {
                binding.edUserId.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the User Id", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.edUserEmail.length() == 0) {
                binding.edUserEmail.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the Email", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.etPassword.length() == 0) {
                binding.etPassword.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the Password", Toast.LENGTH_SHORT).show();

                return false;
            }
            if (binding.etCompanyName.length() == 0) {
                binding.etCompanyName.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the company Name", Toast.LENGTH_SHORT).show();

                return false;
            }
            if (binding.etPhone.length() == 0) {
                binding.etPhone.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the Phone Number", Toast.LENGTH_SHORT).show();

                return false;
            }
            if (binding.etCity.length() == 0) {
                binding.etCity.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter the City", Toast.LENGTH_SHORT).show();
                return false;
            }
//        if (binding.etWebsite.length() == 0) {
//            binding.etWebsite.setError("This field is required");
//            Toast.makeText(UserRegistrationActivity.this, "Enter the Website", Toast.LENGTH_SHORT).show();
//            return false;
//        }

            if (binding.etMobile.length() != 10) {
                binding.etMobile.setError("Enter a 10-digit mobile number");
                Toast.makeText(UserRegistrationActivity.this, R.string.customer_mobile, Toast.LENGTH_SHORT).show();
                return false;
            }


            if (binding.etUseraddress.length() == 0) {
                binding.etUseraddress.setError("Address is required");
                Toast.makeText(UserRegistrationActivity.this, R.string.ADDRESS, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.etPincode.length() != 6) {
                binding.etPincode.setError("Enter a 6-digit Pin code");
                Toast.makeText(UserRegistrationActivity.this, R.string.PIN_CODE, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.etGstNo.length() == 0) {
                binding.etGstNo.setError("This field is required");
                Toast.makeText(UserRegistrationActivity.this, "Enter valid GST No.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        // after all validation return true.
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FROM_FILE) {
                Uri selectedImageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!Utility.isNetworkAvailable(getApplication())) {
                    Toast.makeText(getApplication(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                str_image_logo = getEncoded64ImageStringFromBitmap(imageBitmap);
                binding.imageLogo.setImageBitmap(imageBitmap);


            } else if (requestCode == PICK_FROM_CAMERA) {
                Log.v("camera", imageFilePath);
                try {
                    //our imageFilePath that contains the absolute path to the created file
                    File file = new File(imageFilePath);
                    imageBitmap = MediaStore.Images.Media.getBitmap(UserRegistrationActivity.this.getContentResolver(), Uri.fromFile(file));

                    if (!Utility.isNetworkAvailable(UserRegistrationActivity.this)) {
                        Toast.makeText(UserRegistrationActivity.this, R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    str_image_logo = getEncoded64ImageStringFromBitmap(imageBitmap);
                    binding.imageLogo.setImageBitmap(imageBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteFormat = stream.toByteArray();
        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }
//    private void UserRegistration(Bitmap bitmap) {
//        SessionManagement sm = new SessionManagement(this);
//
//        String apiURL = ApiConstants.BASE_URL + "" + ApiConstants.ADD_COMPANY+"name="+nameStr+ "&" + "login="+login_id+ "&" + "password="+str_password+ "&" + "street="+streetStr+ "&" +"country_id="+ApiConstants.COUNTRY_ID+ "&" +"state_id="+stateStr+ "&" +"city="+str_city+ "&" +"zip="+zipStr+ "&" +"phone="+str_phone+ "&" +"mobile="+mobileStr+ "&" +"email="+emailStr+ "&" +"website="+str_website+ "&" +"insect_lic_no="+str_insectLicNo+ "&" +"fert_lic_no="+ str_fertLicNo+ "&" +"seed_lic_no="+ str_seedLicNo+ "&" +"vat="+ str_gst_no+ "&" +"company_name="+ str_comp_name+ "&" +"taluka_id="+ talukaStr+ "&" +"district_id="+ districtStr + "&" +"user_email="+ user_email_str;
//        Log.d("apiURL",apiURL);
//        Utility.showDialoge("Please wait while a moment...", this);
//        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL, new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//
//                Utility.dismissDialoge();
//                Log.v("Response", response.toString());
//
//                String res=new String(response.data);
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(res);
//                    int statusCode = obj.optInt("statuscode");
//                    String  status = obj.optString("status");
//                    String error_message = obj.optString("error_message");
//                    String str_message = obj.optString("message");
//                    if (statusCode == 200 && status.equalsIgnoreCase("Success")) {
//
//                        Toast.makeText(UserRegistrationActivity.this, str_message, Toast.LENGTH_SHORT).show();
//                    Intent intentLogin = new Intent(UserRegistrationActivity.this, LoginActivity.class);
//                    startActivity(intentLogin);
//                    finish();
//                    } else {
//                        Utility.dismissDialoge();
//                        Toast.makeText(UserRegistrationActivity.this, error_message, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//
//            }
//        }, error -> {
//            Log.v("Error Response", error.toString());
//            Utility.dismissDialoge();
//        }) {
//
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                long imagename = System.currentTimeMillis();
//                params.put("company_logo", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
//                return params;
//            }
//        };
//
//        //adding the request to volley
//        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(this).add(volleyMultipartRequest);
//    }

    private void UserRegistration() {
        SessionManagement sm = new SessionManagement(UserRegistrationActivity.this);
        Map<String, String> params = new HashMap<>();
        // params.put("user_id", sm.getUserID() + "");
        params.put("name", nameStr);
        params.put("login", login_id);
//        params.put("company_logo", str_image_logo);
        params.put("password", str_password);
        params.put("company_name", str_comp_name);
        params.put("street", streetStr);
        params.put("country_id", ApiConstants.COUNTRY_ID);
        params.put("state_id", stateStr);
        params.put("district_id", districtStr);
        params.put("taluka_id", talukaStr);
        params.put("city", str_city);
        params.put("zip", zipStr);
        params.put("phone", str_phone);
        params.put("mobile", mobileStr);
        params.put("email", emailStr);
        params.put("website", str_website);
        params.put("insect_lic_no", str_insectLicNo);
        params.put("fert_lic_no", str_fertLicNo);
        params.put("seed_lic_no", str_seedLicNo);
        params.put("vat", str_gst_no);

        Utility.showDialoge("", UserRegistrationActivity.this);
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(UserRegistrationActivity.this, params).createRequest(ApiConstants.ADD_COMPANY, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                String error_message = obj.optString("error_message");
                String str_message = obj.optString("message");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();
                    resetFields();
                    //   Toast.makeText(UserRegistrationActivity.this, "Company Create", Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(UserRegistrationActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    finish();
                    Toast.makeText(UserRegistrationActivity.this, str_message, Toast.LENGTH_SHORT).show();

                } else {
                    Utility.dismissDialoge();
                    Toast.makeText(UserRegistrationActivity.this, error_message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) throws Exception {
                Utility.dismissDialoge();
                Log.v("Response", result);
                Toast.makeText(UserRegistrationActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void UpdateUserRegistration(Bitmap bitmap) {
        SessionManagement sm = new SessionManagement(this);

        String apiURL = ApiConstants.BASE_URL + "" + ApiConstants.UPDATE_COMPANY + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken() + "&" + "street=" + streetStr + "&" + "country_id=" + ApiConstants.COUNTRY_ID + "&" + "state_id=" + stateStr + "&" + "city=" + str_city + "&" + "zip=" + zipStr + "&" + "phone=" + str_phone + "&" + "mobile=" + mobileStr + "&" + "email=" + emailStr + "&" + "website=" + str_website + "&" + "insect_lic_no=" + str_insectLicNo + "&" + "fert_lic_no=" + str_fertLicNo + "&" + "seed_lic_no=" + str_seedLicNo + "&" + "vat=" + str_gst_no + "&" + "company_id=" + sm.getCompanyID() + "" + "&" + "company_name=" + str_comp_name + "&" + "taluka_id=" + talukaStr + "&" + "district_id=" + districtStr;
        Log.d("apiURL", apiURL);
        Utility.showDialoge("Please wait while a moment...", this);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Utility.dismissDialoge();
                String res=new String(response.data);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(res);
                    int statusCode = obj.optInt("statuscode");
                    String  status = obj.optString("status");
                    String error_message = obj.optString("error_message");
                    String str_message = obj.optString("message");
                    if (statusCode == 200 && status.equalsIgnoreCase("Success")) {
                        Utility.dismissDialoge();
                        Toast.makeText(UserRegistrationActivity.this, str_message, Toast.LENGTH_SHORT).show();

                    } else {
                        Utility.dismissDialoge();
                        Toast.makeText(UserRegistrationActivity.this, error_message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, error -> {
            Log.v("Error Response", error.toString());
            Utility.dismissDialoge();
        }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (bitmap != null) {

                    params.put("company_logo", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                }
                return params;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
//    private void UpdateUserRegistration() {
//        SessionManagement sm = new SessionManagement(UserRegistrationActivity.this);
//        Map<String, String> params = new HashMap<>();
//        params.put("street", streetStr);
//        params.put("country_id", ApiConstants.COUNTRY_ID);
//        params.put("state_id", stateStr);
//        params.put("city", str_city);
//        params.put("zip", zipStr);
//        params.put("phone", str_phone);
//        params.put("mobile", mobileStr);
//        params.put("email", emailStr);
//        params.put("website", str_website);
//        params.put("insect_lic_no", str_insectLicNo);
//        params.put("fert_lic_no", str_fertLicNo);
//        params.put("seed_lic_no", str_seedLicNo);
//        params.put("vat", str_gst_no);
//        params.put("user_id", sm.getUserID()+ "");
//        params.put("company_id", sm.getCompanyID()+"");
//        params.put("token", sm.getJWTToken());
//        params.put("company_name", str_comp_name);
//        params.put("taluka_id", talukaStr);
//        params.put("district_id", districtStr);
//
//        params.put("company_logo",str_image_logo);
//
//        Utility.showDialoge("", UserRegistrationActivity.this);
//        Log.v("add", String.valueOf(params));
//        new VolleyRequestHandler(UserRegistrationActivity.this, params).createRequest(ApiConstants.UPDATE_COMPANY, new VolleyCallback() {
//            private String message = " failed!!";
//
//            @Override
//            public void onSuccess(Object result) throws JSONException {
//                Log.v("Response", result.toString());
//                JSONObject obj = new JSONObject(result.toString());
//                int statusCode = obj.optInt("statuscode");
//                message = obj.optString("status");
//                String error_message = obj.optString("error_message");
//                String str_message = obj.optString("message");
//                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
//                    Utility.dismissDialoge();
//                    Toast.makeText(UserRegistrationActivity.this, str_message, Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Utility.dismissDialoge();
//                    Toast.makeText(UserRegistrationActivity.this, str_message, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(String result) throws Exception {
//                Utility.dismissDialoge();
//                Log.v("Response", result);
//                Toast.makeText(UserRegistrationActivity.this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    private void getCompanyDetails() {
        SessionManagement sm = new SessionManagement(this);
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = ApiConstants.BASE_URL + ApiConstants.GET_COMPANY_DETAILS + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        Utility.showDialoge("Please wait while a moment...", this);
        Log.d("get_company_detail", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response_", response.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    if (status.equalsIgnoreCase("success")) {
                        Utility.dismissDialoge();
                        JSONObject data_json = obj.getJSONObject("data");

                        //  for (int i = 0; i < jsonArray.length(); i++) {

                        int id = data_json.optInt("id");
                        String name = data_json.optString("name");
                        String street = data_json.optString("street");
                        String city = data_json.optString("city");
                        String partner = data_json.optString("partner");
                        String mobile = data_json.optString("mobile");
                        String phone = data_json.optString("phone");
                        String email = data_json.optString("email");
                        String website = data_json.optString("website");
                        String insect_lic_no = data_json.optString("insect_lic_no");
                        String fert_lic_no = data_json.optString("fert_lic_no");
                        String gst = data_json.optString("vat");
                        String seed_lic_no = data_json.optString("seed_lic_no");
                        String zip = data_json.optString("zip");
                        String logo = data_json.optString("logo_url");
                        state = data_json.optInt("state");
                        district = data_json.optInt("district");
                        taluka = data_json.optInt("taluka");

//
                        Glide.with(UserRegistrationActivity.this)
                                .load(logo)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(binding.imageLogo);


                        binding.etGstNo.setText(gst);
                        binding.etUsername.setText(name);
                        binding.etMobile.setText(mobile);
                        binding.etUseremail.setText(email);
                        binding.etPincode.setText(zip);
                        binding.etUseraddress.setText(street);
                        binding.etCompanyName.setText(partner);
                        binding.etCity.setText(city);
                        binding.etPhone.setText(phone);
                        binding.etWebsite.setText(website);
                        binding.etInsectLicNo.setText(insect_lic_no);
                        binding.etFertLicNo.setText(fert_lic_no);
                        binding.etSeedLicNo.setText(seed_lic_no);

                        getStateData(String.valueOf(state));
                    }

                    // }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(this, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        Utility.dismissDialoge();
        queue.add(jsonObjectRequest);
    }

    private Bitmap convertImageViewToBitmap(ImageView v) {
        Bitmap bm = ((BitmapDrawable) v.getDrawable()).getBitmap();
        return bm;
    }

    private void resetFields() {
        binding.etUsername.setText("");
        binding.etMobile.setText("");
        binding.etUseraddress.setText("");
        binding.etPincode.setText("");
        binding.etUseremail.setText("");
        binding.etCity.setText("");
        binding.etPassword.setText("");
        binding.edUserId.setText("");
        binding.etCompanyName.setText("");
        binding.etPhone.setText("");
        binding.etGstNo.setText("");
        binding.etInsectLicNo.setText("");
        binding.etFertLicNo.setText("");
        binding.etSeedLicNo.setText("");
        binding.stateSpinner.setSelection(0);

    }
}