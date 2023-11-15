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

import java.util.List;

public class SimilarProductAdapter extends RecyclerView.Adapter<SimilarProductAdapter.ViewHolder> {
    private List<Product> customerDataList;
    private Context mContext;

    public SimilarProductAdapter(Context context, List<Product> contacts) {
        customerDataList = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView,total_amount,pac_text;
        public ImageView productThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productTitle);
            productThumb = itemView.findViewById(R.id.productThumb);
            pac_text = itemView.findViewById(R.id.pac_text);
            total_amount = itemView.findViewById(R.id.total_amount);

        }
    }

    @Override
    public SimilarProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.similar_item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull SimilarProductAdapter.ViewHolder holder, int position) {
        Product product = customerDataList.get(position);
        holder.nameTextView.setText(product.getProductName());
        holder.pac_text.setText(product.getProduct_package());
        holder.total_amount.setText("₹"+String.valueOf(product.getPrice()));


        Picasso.with(mContext).load(ApiConstants.BASE_URL + product.getProductImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.productThumb);
    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}