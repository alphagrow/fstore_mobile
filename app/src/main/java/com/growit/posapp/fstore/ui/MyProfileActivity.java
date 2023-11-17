package com.growit.posapp.fstore.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.DataPart;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText nameET, emailET, mobileET;
    private FrameLayout picImageFL;
    String imageFilePath;
    private ImageView profile_image, backBtn;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    private String nameStr, emailStr, mobileStr;
    ProgressBar idPBLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        setContentView(R.layout.activity_my_profile);
        initView();
    }


    private void initView() {
        nameET = findViewById(R.id.et_username);
        emailET = findViewById(R.id.et_useremail);
        idPBLoading = findViewById(R.id.idPBLoading);
        backBtn = findViewById(R.id.backBtn);
        nameET.setFocusable(false);
        emailET.setFocusable(false);
        mobileET = findViewById(R.id.et_usermobile);
        mobileET.setFocusable(false);
        profile_image = findViewById(R.id.profile_image);
        picImageFL = findViewById(R.id.fl_pic_image);
        backBtn.setOnClickListener(this);
        profile_image.setOnClickListener(this);
        findViewById(R.id.tv_gallery).setOnClickListener(this);
        findViewById(R.id.tv_camera).setOnClickListener(this);
        findViewById(R.id.tv_cross).setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FROM_FILE) {
                Uri selectedImageUri = data.getData();
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                idPBLoading.setVisibility(View.VISIBLE);

                if (!Utility.isNetworkAvailable(getApplication())) {
                    Toast.makeText(getApplication(), "Network not available.Please try later!", Toast.LENGTH_SHORT).show();
                    return;
                }
                 uploadImage(imageBitmap);

            } else if (requestCode == PICK_FROM_CAMERA) {
                Log.v("camera", imageFilePath);
                try {
                    //our imageFilePath that contains the absolute path to the created file
                    File file = new File(imageFilePath);
                    Bitmap imageBitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                    idPBLoading.setVisibility(View.VISIBLE);
                    if (!Utility.isNetworkAvailable(getApplication())) {
                        Toast.makeText(getApplication(), "Network not available.Please try later!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    uploadImage(imageBitmap);
                    //do whatever else after
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        if (viewID == R.id.backBtn) {
//            startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
            finish();
        } else if (viewID == R.id.profile_image) {
            picImageFL.setVisibility(View.VISIBLE);
        } else if (viewID == R.id.tv_cross) {
            picImageFL.setVisibility(View.GONE);
        } else if (viewID == R.id.tv_gallery) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                // Do something for lollipop and above versions
                askForPermission(Manifest.permission.READ_MEDIA_IMAGES, 1);
            } else{
                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
            }

        } else if (viewID == R.id.tv_camera) {
            askForPermission(Manifest.permission.CAMERA, 2);
        }

    }

    private void fromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), PICK_FROM_FILE);
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

    private void askForPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MyProfileActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{permission,Manifest.permission.MANAGE_EXTERNAL_STORAGE}, requestCode);
        } else {
            if (requestCode == 1) {
                fromGallery();
            } else if (requestCode == 2) {
                openCamera();
            }
        }
    }

    void uploadImage(Bitmap bitmap) {
        SessionManagement sm = new SessionManagement(this);

        String apiURL = ApiConstants.BASE_URL + "" + ApiConstants.UPLOAD_PHOTO + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    idPBLoading.setVisibility(View.GONE);
                    profile_image.setImageBitmap(bitmap);
                    picImageFL.setVisibility(View.VISIBLE);
                    String res=new String(response.data);
                    Log.v("Response", res);
                    JSONObject obj = new JSONObject(new String(response.data));
//                    JSONObject resultObj = obj.getJSONObject("results");
//                    String status = obj.optString("status");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Log.v("Error Response", error.toString());
            idPBLoading.setVisibility(View.GONE);
            picImageFL.setVisibility(View.GONE);
        }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("photo_data", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
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

}