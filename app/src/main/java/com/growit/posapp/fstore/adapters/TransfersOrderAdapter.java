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
import com.growit.posapp.fstore.model.TransfersModelList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransfersOrderAdapter extends RecyclerView.Adapter<TransfersOrderAdapter.ViewHolder> {

    private List<TransfersModelList> order;
    private Context mContext;

    public TransfersOrderAdapter(Context context, List<TransfersModelList> order_list) {
        order = order_list;
        mContext = context;
    }
    public void updateList(ArrayList<TransfersModelList> modellist) {
        this.order=modellist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty,order_no,dateTxt,amountTxt,product_name,case_id,state;
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
            state = itemView.findViewById(R.id.status);

        }
    }

    @Override
    public TransfersOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.transfer_order_item_row, parent, false);
        TransfersOrderAdapter.ViewHolder viewHolder = new TransfersOrderAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TransfersOrderAdapter.ViewHolder holder, int position) {
        TransfersModelList orders = order.get(position);

        holder.order_no.setText(orders.getOrigin());
        holder.product_name.setText(orders.getName());
        holder.state.setText(orders.getState());



//        DecimalFormat form = new DecimalFormat("0.00");
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