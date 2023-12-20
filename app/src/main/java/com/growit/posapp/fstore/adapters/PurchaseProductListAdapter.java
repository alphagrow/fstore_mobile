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
import com.growit.posapp.fstore.model.Purchase.PurchaseProductModel;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UpdateAddProductFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class PurchaseProductListAdapter extends RecyclerView.Adapter<PurchaseProductListAdapter.ViewHolder> {

    private List<PurchaseProductModel> list;
    private Context mContext;
    String crop_id,crop_name;

    public PurchaseProductListAdapter(Context context, List<PurchaseProductModel> contacts) {

        this.list = contacts;
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty, order_no, dateTxt, amountTxt, case_id;
        ImageView images, deleteBtn, update;
        TextView product_name;

        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.name_product);
            qty = itemView.findViewById(R.id.Qty_avl_text);
            images = itemView.findViewById(R.id.productThumb);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            update = itemView.findViewById(R.id.update);


        }
    }

    @Override
    public PurchaseProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.purchase_item_row, parent, false);
        PurchaseProductListAdapter.ViewHolder viewHolder = new PurchaseProductListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull PurchaseProductListAdapter.ViewHolder holder, int position) {
        PurchaseProductModel model = list.get(position);
        //   holder.qty.setText("Qty Avl. : " + String.valueOf(model.getQuantity()));
        holder.product_name.setText(model.getProductName());
//        holder.product_name.setShowingChar(100);
//        holder.product_name.setShowingLine(2);
//        holder.product_name.addShowMoreText("");
//        holder.product_name.addShowLessText("Less");
//        holder.product_name.setShowMoreColor(Color.BLACK); // or other color
//        holder.product_name.setShowLessTextColor(Color.RED); // or other color


        Picasso.with(mContext).load(ApiConstants.BASE_URL + model.getImageUrl())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.images);


    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}