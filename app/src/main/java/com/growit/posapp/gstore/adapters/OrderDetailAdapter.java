package com.growit.posapp.gstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.model.OrderData;
import com.growit.posapp.gstore.model.Product;
import com.growit.posapp.gstore.utils.ApiConstants;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;


import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<Product> customerDataList;
    private Context mContext;

    public OrderDetailAdapter(Context context, List<Product> contacts) {
        customerDataList = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty, total_amount, pac_text, crop_text;
        ImageView product_Image;
        ShowMoreTextView textView;
        public  ShowMoreTextView product_name;

        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            total_amount = itemView.findViewById(R.id.total);
            qty = itemView.findViewById(R.id.qut_text);
            product_Image = itemView.findViewById(R.id.product_Image);
            pac_text = itemView.findViewById(R.id.pac_text);
            crop_text = itemView.findViewById(R.id.crop_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.order_detail_card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product orders = customerDataList.get(position);
        holder.product_name.setText(orders.getProductName());
        holder.pac_text.setText(orders.getProduct_package());
        if (!orders.getCrop_name().equalsIgnoreCase("false")) {
            holder.crop_text.setText(orders.getCrop_name());
        }
        holder.product_name.setText(orders.getProductName());
        holder.product_name.setShowingChar(70);
        holder.product_name.setShowingLine(2);
        holder.product_name.addShowMoreText("");
        holder.product_name.addShowLessText("Less");
        holder.product_name.setShowMoreColor(Color.BLACK); // or other color
        holder.product_name.setShowLessTextColor(Color.RED); // or other color
        holder.qty.setText("Qty : " + orders.getQuantity());
        holder.total_amount.setText("Rs. "+orders.getPrice() + "");


        Picasso.with(mContext).load(ApiConstants.BASE_URL + orders.getProductImageUrl())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.product_Image);


    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}