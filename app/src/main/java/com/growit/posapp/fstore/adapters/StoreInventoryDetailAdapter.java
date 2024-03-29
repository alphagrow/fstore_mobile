package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.WarehouseModel;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StoreInventoryDetailAdapter extends RecyclerView.Adapter<StoreInventoryDetailAdapter.ViewHolder> {

    private List<Product> list;
    private Context mContext;

    public StoreInventoryDetailAdapter(Context context, List<Product> contacts) {
        list = contacts;
        mContext = context;
    }
    public void updateList(ArrayList<Product> modellist) {
        this.list=modellist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView product_name ,qty,location_text;
        ImageView images;
        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name_text);
            qty = itemView.findViewById(R.id.Qty_avl_text);
            images = itemView.findViewById(R.id.images);
            location_text = itemView.findViewById(R.id.location_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.store_inventory_item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product orders = list.get(position);
        holder.qty.setText("Qty Avl. : "+orders.getQuantity());
        holder.product_name.setText(orders.getProductName());
        holder.location_text.setText(orders.getLocation_name());
        Picasso.with(mContext).load(ApiConstants.BASE_URL + orders.getProductImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.images);



    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}