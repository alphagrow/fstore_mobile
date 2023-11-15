package com.growit.posapp.gstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.model.Product;
import com.growit.posapp.gstore.utils.ApiConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private List<Product> customerDataList;
    private Context mContext;

    public ProductListAdapter(Context context, List<Product> contacts) {
        customerDataList = contacts;
        mContext = context;
    }

    public void filterList(ArrayList<Product> filterer) {
        customerDataList = filterer;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView,qut,price;
        public ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productTitle);
            thumbnail = itemView.findViewById(R.id.productThumb);
            price = itemView.findViewById(R.id.price);

        }
    }

    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.product_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ViewHolder holder, int position) {
        Product product = customerDataList.get(position);
        TextView textView = holder.nameTextView;
        textView.setText(product.getProductName());
holder.price.setText("₹"+String.valueOf(product.getPrice()));
        Picasso.with(mContext).load(ApiConstants.BASE_URL + product.getProductImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}