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
import com.growit.posapp.fstore.model.OrderData;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<OrderData> customerDataList;
    private Context mContext;

    public OrderHistoryAdapter(Context context, List<OrderData> contacts) {
        customerDataList = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty,order_no,dateTxt,amountTxt,product_name,case_id;
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

        }
    }

    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.order_history_item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull OrderHistoryAdapter.ViewHolder holder, int position) {
        OrderData orders = customerDataList.get(position);
        holder.qty.setText("  Items : " +orders.getProducts().size());
        holder.dateTxt.setText(orders.getOrderDate());
        holder.order_no.setText(orders.getOrderNumber());
        holder.product_name.setText(orders.getCustomerName());
        DecimalFormat form = new DecimalFormat("0.00");
        Picasso.with(mContext).load(ApiConstants.BASE_URL + orders.getProducts().get(0).getProductImageUrl())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.images);

        holder.amountTxt.setText("Rs. "+form.format(Double.valueOf(orders.getAmountPaid())));
        if(orders.getPayment_type()!=null) {
            holder.case_id.setText(orders.getPayment_type());
        }
    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}