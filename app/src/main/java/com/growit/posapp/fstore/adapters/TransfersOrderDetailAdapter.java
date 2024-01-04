package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.Purchase.PurchaseOrderLine;
import com.growit.posapp.fstore.model.TransfersModelList;
import com.skyhope.showmoretextview.ShowMoreTextView;

import java.util.List;

public class TransfersOrderDetailAdapter extends RecyclerView.Adapter<TransfersOrderDetailAdapter.ViewHolder> {

    private List<TransfersModelList> customerDataList;
    private Context mContext;

    public TransfersOrderDetailAdapter(Context context, List<TransfersModelList> contacts) {
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
    public TransfersOrderDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.tran_order_det_layout, parent, false);
        TransfersOrderDetailAdapter.ViewHolder viewHolder = new TransfersOrderDetailAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TransfersOrderDetailAdapter.ViewHolder holder, int position) {
        TransfersModelList orders = customerDataList.get(position);
        holder.product_name.setText(orders.getName());

//        holder.qty.setText("Qty : " + orders.getQuantity());
//        holder.total_amount.setText("Rs. "+orders.getPrice() + "");


//        Picasso.with(mContext).load(ApiConstants.BASE_URL + orders.getProductImageUrl())
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.no_image)
//                .into(holder.product_Image);


    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}