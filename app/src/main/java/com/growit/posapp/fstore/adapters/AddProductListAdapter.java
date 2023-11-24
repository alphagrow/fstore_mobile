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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.StockInventoryModel;
import com.growit.posapp.fstore.model.StockInventoryModelList;
import com.growit.posapp.fstore.ui.fragments.AddProduct.AddProductFragment;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UpdateAddProductFragment;
import com.growit.posapp.fstore.ui.fragments.UpdateCustomerFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class AddProductListAdapter extends RecyclerView.Adapter<AddProductListAdapter.ViewHolder> {

    private List<Product> list;
    private Context mContext;

    public AddProductListAdapter(Context context, List<Product> contacts) {
        list = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty,order_no,dateTxt,amountTxt,case_id;
        ImageView images,deleteBtn;
        ShowMoreTextView product_name;
        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name_text);
            qty= itemView.findViewById(R.id.Qty_avl_text);
            images = itemView.findViewById(R.id.images);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);


        }
    }

    @Override
    public AddProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.product_item_row, parent, false);
        AddProductListAdapter.ViewHolder viewHolder = new AddProductListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull AddProductListAdapter.ViewHolder holder, int position) {
        Product model = list.get(position);
        holder.qty.setText("Qty Avl. : "+String.valueOf(model.getQuantity()));
        holder.product_name.setText(model.getProductName());
        holder.product_name.setShowingChar(100);
        holder.product_name.setShowingLine(2);
        holder.product_name.addShowMoreText("");
        holder.product_name.addShowLessText("Less");
        holder.product_name.setShowMoreColor(Color.BLACK); // or other color
        holder.product_name.setShowLessTextColor(Color.RED); // or other color
        Picasso.with(mContext).load(ApiConstants.BASE_URL + model.getProductImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.images);

holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
//        Bundle bundle = new Bundle();
//        if(isSearch) {
//            bundle.putSerializable("CustomerOBJ", (Serializable) searchCustomerDataList);
//            bundle.putInt("position", position);
//        }else{
//            bundle.putSerializable("CustomerOBJ", (Serializable) customerDataList);
//            bundle.putInt("position", position);
//        }


        Fragment fragment = UpdateAddProductFragment.newInstance();
       // fragment.setArguments(bundle);
        FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }
});
holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Product model = list.get(position);
        getDelete(model.getProductID());
    }
});

    }
    private void getDelete(String id) {
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