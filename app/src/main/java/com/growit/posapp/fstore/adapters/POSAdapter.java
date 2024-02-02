package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.growit.posapp.fstore.MainActivity;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.StockInventoryModelList;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.AddProduct.AddProductListFragment;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UpdateAddProductFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.AddPOSCategoryFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class POSAdapter extends RecyclerView.Adapter<POSAdapter.ViewHolder> {


    private List<Value> list;
    private Context mContext;

    public POSAdapter(Context context, List<Value> contacts) {
        list = contacts;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        ImageView productThumb,deleteBtn,update;
        LinearLayout card,linear_click;
        TextView product_name_text;
        public ViewHolder(View itemView) {
            super(itemView);
            product_name_text = itemView.findViewById(R.id.product_name_text);
            productThumb = itemView.findViewById(R.id.images);
            card = itemView.findViewById(R.id.card);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            update = itemView.findViewById(R.id.update_crop);
            linear_click = itemView.findViewById(R.id.linear_click);
        }
    }

    @Override
    public POSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.crop_item_row, parent, false);
        POSAdapter.ViewHolder viewHolder = new POSAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull POSAdapter.ViewHolder holder, int position) {
        Value model = list.get(position);
        holder.product_name_text.setText(model.getValueName());
//        holder.product_name_text.setShowingChar(100);
//        holder.product_name_text.setShowingLine(2);
//        holder.product_name_text.addShowMoreText("");
//        holder.product_name_text.addShowLessText("Less");
//        holder.product_name_text.setShowMoreColor(Color.BLACK); // or other color
//        holder.product_name_text.setShowLessTextColor(Color.RED); // or other color
//        Picasso.with(mContext).load(model.getImage_url())
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.no_image)
//                .into(holder.productThumb);
        Glide.with(mContext)
                .load(model.getImage_url())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.productThumb);

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("crop_list", (Serializable) list);
                bundle.putInt("position", position);

                Fragment fragment = AddPOSCategoryFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


            }
        });
        holder.linear_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("crop_list", (Serializable) list);
                bundle.putInt("position", position);
                bundle.putString("product_list", "crop_product");
                Fragment fragment = AddProductListFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete ?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    Value model = list.get(position);
                    getDelete(String.valueOf(model.getValueId()),position);

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    private void getDelete(String id,int position) {
        SessionManagement sm = new SessionManagement(mContext);
        RequestQueue queue = Volley.newRequestQueue(mContext);//162.246.254.203:8069
        String url = ApiConstants.BASE_URL + ApiConstants.DELETE_POS_CATEGORY + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken() + "&" + "pos_category_id=" + id;
        Log.v("delete_product_url", url);
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
                        list.remove(position);
                        notifyDataSetChanged();

                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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