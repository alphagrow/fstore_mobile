package com.growit.posapp.fstore.ui.fragments.POSCategory;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
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
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ImageAdapter;
import com.growit.posapp.fstore.databinding.AddCustomerFragmentBinding;
import com.growit.posapp.fstore.databinding.FragmentPOSCategoryBinding;
import com.growit.posapp.fstore.ui.fragments.AddCustomerFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddPOSCategoryFragment extends Fragment {

    FragmentPOSCategoryBinding binding;
    boolean isAllFieldsChecked = false;

    String imageFilePath;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 3;
    public AddPOSCategoryFragment() {
        // Required empty public constructor
    }
    public static AddPOSCategoryFragment newInstance() {
        return new AddPOSCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_p_o_s_category, container, false);
        init();
        return  binding.getRoot();
    }
    private void init(){
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked= CheckAllFields();
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getContext(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isAllFieldsChecked) {
                    getPosCategory();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FROM_FILE) {
                Uri selectedImageUri = data.getData();
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!Utility.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.profileImage.setImageBitmap(imageBitmap);
            } else if (requestCode == PICK_FROM_CAMERA) {

                try {
                    //our imageFilePath that contains the absolute path to the created file
                    File file = new File(imageFilePath);
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));

                    if (!Utility.isNetworkAvailable(getActivity())) {
                        Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                        return;
                    }
                   binding.profileImage.setImageBitmap(imageBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
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
    private boolean CheckAllFields() {
        if (binding.cropName.length()== 0) {
            binding.cropName.setError("This field is required");
            Toast.makeText(getActivity(), "Enter the Crop Name", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }
    private void getPosCategory(){
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
//        params.put("name", nameStr);

        Utility.showDialoge("Please wait while a moment...", getActivity());
        Log.v("add", String.valueOf(params));
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.ADD_CUSTOMER, new VolleyCallback() {
            private String message = "Registration failed!!";

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");

                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    Utility.dismissDialoge();

                }
            }

            @Override
            public void onError(String result) throws Exception {
                Toast.makeText(getActivity(), R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show();
            }
        });

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

}