package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.StockInventoryModelList;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UpdateAddProductFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.ViewHolder> {

    private List<StockInventoryModelList> list;
    private Context mContext;

    public VendorListAdapter(Context context, List<StockInventoryModelList> contacts) {
        list = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty,order_no,dateTxt,amountTxt,case_id;
        ImageView images;
        ShowMoreTextView product_name;
        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name_text);
            qty= itemView.findViewById(R.id.Qty_avl_text);
            images = itemView.findViewById(R.id.images);


        }
    }

    @Override
    public VendorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.product_item_row, parent, false);
        VendorListAdapter.ViewHolder viewHolder = new VendorListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull VendorListAdapter.ViewHolder holder, int position) {
        StockInventoryModelList orders = list.get(position);
        holder.qty.setText("Qty Avl. : "+String.valueOf(orders.getQuantity()));
        holder.product_name.setText(orders.getProductName());
        holder.product_name.setShowingChar(100);
        holder.product_name.setShowingLine(2);
        holder.product_name.addShowMoreText("");
        holder.product_name.addShowLessText("Less");
        holder.product_name.setShowMoreColor(Color.BLACK); // or other color
        holder.product_name.setShowLessTextColor(Color.RED); // or other color
        Picasso.with(mContext).load(ApiConstants.BASE_URL + orders.getProductImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.images);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = UpdateAddProductFragment.newInstance();
                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}