package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.StockInventoryModelList;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.ui.fragments.AddProduct.AddProductListFragment;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UpdateAddProductFragment;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.AddVendorFragment;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.UpdateVendorFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.ViewHolder> {

    private List<VendorModelList> list;
    private Context mContext;

    public VendorListAdapter(Context context, List<VendorModelList> contacts) {
        list = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTxt,mobile,status;
        ImageView deleteBtn,acti_img,acti_img_2;
        ShowMoreTextView product_name;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            mobile= itemView.findViewById(R.id.mobile);
            deleteBtn= itemView.findViewById(R.id.deleteBtn);
            acti_img = itemView.findViewById(R.id.acti_img);
            acti_img_2 = itemView.findViewById(R.id.acti_img_2);
            status = itemView.findViewById(R.id.status);


        }
    }

    @Override
    public VendorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.vendor_item_row, parent, false);
        VendorListAdapter.ViewHolder viewHolder = new VendorListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull VendorListAdapter.ViewHolder holder, int position) {
        VendorModelList modelList = list.get(position);
        holder.nameTxt.setText(modelList.getName());
        holder.mobile.setText(modelList.getMobile());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("vendor_list", (Serializable) list);
                bundle.putInt("position", position);
                Fragment fragment = UpdateVendorFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


            }
        });


//            if (list.get(position).getStatus().equals("INACTIVE")) {
//                holder.status.setText(list.get(position).getStatus());
//                holder.status.setTextColor(Color.parseColor("#F55625"));
//                holder.acti_img_2.setVisibility(View.VISIBLE);
//                holder.acti_img.setVisibility(View.GONE);
//
//            } else {
//                holder.status.setTextColor(Color.parseColor("#008000"));
//                holder.status.setText(list.get(position).getStatus());
//                holder.acti_img.setVisibility(View.VISIBLE);
//                holder.acti_img_2.setVisibility(View.GONE);
//            }


        holder.acti_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to change Inactive ?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    VendorModelList model = list.get(position);
                    getStatus(String.valueOf(model.getVendorId()),holder);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        holder.acti_img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to Active ?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    VendorModelList model = list.get(position);
                    getStatus(String.valueOf(model.getVendorId()),holder);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }
    private void getStatus(String id,final VendorListAdapter.ViewHolder holder_h ) {
        SessionManagement sm = new SessionManagement(mContext);
        RequestQueue queue = Volley.newRequestQueue(mContext);//162.246.254.203:8069
        String url = ApiConstants.BASE_URL + ApiConstants.DELETE_ARCHIVE_VENDOR + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken() + "&" + "vendor_id=" + id;
        Log.v("delete_Vendor_url", url);
        Utility.showDialoge("Please wait while a moment...", mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                Utility.dismissDialoge();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    String message = obj.optString("message");
                    String error_message = obj.optString("error_message");
                    if (status.equalsIgnoreCase("success")) {

                        if (holder_h.status.getText().toString().equalsIgnoreCase("INACTIVE")) {
                            holder_h.status.setText("ACTIVE");
                            holder_h.status.setTextColor(Color.parseColor("#008000"));

                            holder_h.acti_img.setVisibility(View.VISIBLE);
                            holder_h.acti_img_2.setVisibility(View.GONE);
                        } else {
                            holder_h.status.setTextColor(Color.parseColor("#F55625"));
                            holder_h.acti_img_2.setVisibility(View.VISIBLE);
                            holder_h.acti_img.setVisibility(View.GONE);
                            holder_h.status.setText("INACTIVE");
                        }
                    }else {
                        Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();

                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> Toast.makeText(mContext, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}