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
import com.growit.posapp.fstore.model.Purchase.PurchaseOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransfersOrderAdapter extends RecyclerView.Adapter<TransfersOrderAdapter.ViewHolder> {

    private List<PurchaseOrder> order;
    private Context mContext;

    public TransfersOrderAdapter(Context context, List<PurchaseOrder> order_list) {
        order = order_list;
        mContext = context;
    }
    public void updateList(ArrayList<PurchaseOrder> modellist) {
        this.order=modellist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty,order_no,dateTxt,amountTxt,product_name,case_id,status;
        ImageView images;
        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            qty= itemView.findViewById(R.id.qty);
            dateTxt= itemView.findViewById(R.id.dateTxt);
            amountTxt= itemView.findViewById(R.id.amountTxt);
            images = itemView.findViewById(R.id.images);
            order_no = itemView.findViewById(R.id.order_no);
            case_id = itemView.findViewById(R.id.case_id);
            status = itemView.findViewById(R.id.status);

        }
    }

    @Override
    public TransfersOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.order_purchase_item_row, parent, false);
        TransfersOrderAdapter.ViewHolder viewHolder = new TransfersOrderAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TransfersOrderAdapter.ViewHolder holder, int position) {
        PurchaseOrder orders = order.get(position);
//        holder.qty.setText("  Items : " +orders.getProducts().size());
        holder.dateTxt.setText(orders.getOrderDate());
        holder.order_no.setText(orders.getName());
        holder.product_name.setText(orders.getPartnerId());
        if(orders.getReceiptStatus().equalsIgnoreCase("false")) {
        }else if(orders.getReceiptStatus().equalsIgnoreCase("full")) {
            holder.status.setText("Received");
        }else {
            holder.status.setText("Pending");
        }


        DecimalFormat form = new DecimalFormat("0.00");
//        Picasso.with(mContext).load(ApiConstants.BASE_URL + orders.getProducts().get(0).getProductImageUrl())
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.no_image)
//                .into(holder.images);

//        holder.amountTxt.setText("Rs. "+form.format(Double.valueOf(orders.getAmountPaid())));
//        if(orders.getPayment_type()!=null) {
//            holder.case_id.setText(orders.getPayment_type());
//        }
    }


    @Override
    public int getItemCount() {
        return order.size();
    }
}