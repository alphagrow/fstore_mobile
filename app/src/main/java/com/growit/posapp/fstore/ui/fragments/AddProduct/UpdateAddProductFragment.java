package com.growit.posapp.fstore.ui.fragments.AddProduct;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.DatePickerDialog;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.adapters.ImageAdapter;
import com.growit.posapp.fstore.databinding.FragmentAddProductBinding;
import com.growit.posapp.fstore.databinding.FragmentAddProductListBinding;
import com.growit.posapp.fstore.databinding.FragmentUpdateAddProductBinding;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    String str_product_name, str_product_price, str_uom, str_size, str_color, str_whole_pattern;
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

    DatePickerDialog.OnDateSetListener date_mfd,date_exp,date_exp_alarm;
    final Calendar myCalendar = Calendar.getInstance();
    FragmentUpdateAddProductBinding binding;

    //    private static final int SELECT_VIDEO = 3;
    public UpdateAddProductFragment() {
        // Required empty public constructor
    }

    public static AddProductFragment newInstance() {
        return new AddProductFragment();
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
//        imageView = view.findViewById(R.id.imageView);
//        et_product_name = view.findViewById(R.id.et_product_name);
//        et_product_price = view.findViewById(R.id.et_product_price);
//        et_uom = view.findViewById(R.id.et_uom);
//        submit_btn = view.findViewById(R.id.submit_btn);
//        et_size = view.findViewById(R.id.et_size);
//        et_color = view.findViewById(R.id.et_color);
//        et_whole_pattern = view.findViewById(R.id.et_whole_pattern);
//        product_image = view.findViewById(R.id.image);
//        idPBLoading = view.findViewById(R.id.idPBLoading);
//        video_text = view.findViewById(R.id.video_text);
//        video_image = view.findViewById(R.id.video);
//        videoView = view.findViewById(R.id.simpleVideoView);
//        image_set = view.findViewById(R.id.image_set);
//        mfd_date = view.findViewById(R.id.mfd_date);
//        exp_date =view.findViewById(R.id.exp_date);
//        exp_date_alarm = view.findViewById(R.id.exp_date_alarm);
//        tech_name_pest = view.findViewById(R.id.tech_name_pest);
//        brand_name = view.findViewById(R.id.brand_name);
//        mkt_by =view.findViewById(R.id.mkt_by);
//        batch_number = view.findViewById(R.id.batch_number);
//        cir_number = view.findViewById(R.id.cir_number);
//        which_crop = view.findViewById(R.id.which_crop);
//        which_pest =view.findViewById(R.id.which_pest);
//        packing_std = view.findViewById(R.id.packing_std);
//        layout = view.findViewById(R.id.imageLayout);
//        recy_image = view.findViewById(R.id.recy_image);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        binding.submitBtn.setOnClickListener(this);

        binding.mfdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date_mfd, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

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
        date_exp_alarm  = new DatePickerDialog.OnDateSetListener() {
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
    }
    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy'   'HH:mm:ss");
        String   str_mfd_date = sdf.format(myCalendar.getTime());
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yy");
        String date_e = sd.format(myCalendar.getTime());
        mfd_date.setText(date_e);
    }
    private void ExpireLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy'   'HH:mm:ss");
        String   str_exp_date = sdf.format(myCalendar.getTime());
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yy");
        String date_e = sd.format(myCalendar.getTime());
        exp_date.setText(date_e);
    }
    private void AlarmLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy'   'HH:mm:ss");
        String   str_exp_date = sdf.format(myCalendar.getTime());
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yy");
        String date_e = sd.format(myCalendar.getTime());
        exp_date_alarm.setText(date_e);
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
            str_uom = binding.etUom.getText().toString();
            str_size = binding.etSize.getText().toString();
            str_color = binding.etColor.getText().toString();
            str_whole_pattern = binding.etWholePattern.getText().toString();
            if (str_product_name.length() == 0 || str_product_price.length() == 0 || str_uom.length() == 0) {
                Toast.makeText(getActivity(), "Please add mandatory fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utility.isNetworkAvailable(getActivity())) {
                Toast.makeText(getActivity(), R.string.NETWORK_GONE, Toast.LENGTH_SHORT).show();
                return;
            }
            addProductRequest(str_product_name, str_product_price, str_uom, str_size, str_color, str_whole_pattern);
        }
    }

    private void addProductRequest(String product_name, String product_price, String uom, String size, String color, String whole_pattern) {
        SessionManagement sm = new SessionManagement(getActivity());
        Map<String, String> params = new HashMap<>();
        params.put("user_id", sm.getUserID() + "");
        params.put("product_name", product_name);
        params.put("product_price", product_price);
        params.put("uom", uom);
        params.put("size", size);
        params.put("color", color);
        params.put("whole_pattern", whole_pattern);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait while a moment...");
        progressDialog.setTitle("Registering");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        new VolleyRequestHandler(getActivity(), params).createRequest(ApiConstants.ADD_CUSTOMER, new VolleyCallback() {
            private String message = "Registration failed!!";
            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                message = obj.optString("status");
                if (statusCode == 200 && message.equalsIgnoreCase("success")) {
                    int visibility = (progressBar.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                    progressBar.setVisibility(visibility);
                    progressDialog.cancel();
//                    SaveTask st = new SaveTask();
//                    st.execute();
                    resetFields();
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
        binding.etUom.setText("");
        binding.etSize.setText("");
        binding.etColor.setText("");
        binding.etWholePattern.setText("");

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
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
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
}