package com.growit.posapp.fstore.adapters;

import android.content.Context;
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
import com.growit.posapp.fstore.model.Purchase.PurchaseProductModel;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UpdateAddProductFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class AllAddProductListAdapter extends RecyclerView.Adapter<AllAddProductListAdapter.ViewHolder> {

    private List<PurchaseProductModel> list;
    private Context mContext;
    String crop_id,crop_name;

    public AllAddProductListAdapter(Context context, List<PurchaseProductModel> contacts) {
        this.list = contacts;
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty, order_no, dateTxt, amountTxt, case_id;
        ImageView images, deleteBtn, update;
        TextView product_name;

        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name_text);
            qty = itemView.findViewById(R.id.Qty_avl_text);
            images = itemView.findViewById(R.id.images);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            update = itemView.findViewById(R.id.update);


        }
    }

    @Override
    public AllAddProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.product_item_row, parent, false);
        AllAddProductListAdapter.ViewHolder viewHolder = new AllAddProductListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull AllAddProductListAdapter.ViewHolder holder, int position) {
        PurchaseProductModel model = list.get(position);
        //   holder.qty.setText("Qty Avl. : " + String.valueOf(model.getQuantity()));
        holder.product_name.setText(model.getProductName());
        holder.update.setVisibility(View.GONE);
//        holder.product_name.setShowingChar(100);
//        holder.product_name.setShowingLine(2);
//        holder.product_name.addShowMoreText("");
//        holder.product_name.addShowLessText("Less");
//        holder.product_name.setShowMoreColor(Color.BLACK); // or other color
//        holder.product_name.setShowLessTextColor(Color.RED); // or other color


//        Picasso.with(mContext).load(ApiConstants.BASE_URL + model.getProductImage())
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.no_image)
//                .into(holder.images);

//        holder.update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("product_list", (Serializable) list);
//                bundle.putInt("position", position);
//                Fragment fragment = UpdateAddProductFragment.newInstance();
//                fragment.setArguments(bundle);
//                FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
//
//
//            }
//        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete ?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    PurchaseProductModel model = list.get(position);
                    getDelete(String.valueOf(model.getProductId()), position);

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private void getDelete(String id, int position) {
        SessionManagement sm = new SessionManagement(mContext);
        RequestQueue queue = Volley.newRequestQueue(mContext);//162.246.254.203:8069
        String url = ApiConstants.BASE_URL + ApiConstants.DELETE_PRODUCT + "user_id=" + sm.getUserID() + "&" + "token=" + sm.getJWTToken() + "&" + "product_id=" + id;
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
                    } else {
                        Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
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
