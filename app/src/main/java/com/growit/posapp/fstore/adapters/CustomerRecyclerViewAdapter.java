package com.growit.posapp.fstore.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.tables.Customer;


import java.util.List;

public class CustomerRecyclerViewAdapter extends RecyclerView.Adapter<CustomerRecyclerViewAdapter.ViewHolder> {

    private List<Customer> customerDataList;
    private Context mContext;
    // Pass in the contact array into the constructor
    public CustomerRecyclerViewAdapter(Context context,List<Customer> contacts) {
        customerDataList = contacts;
        mContext=context;
    }

    public void filterList(List<Customer> filterer) {
        // below line is to add our filtered
        // list in our course array list.
        customerDataList = filterer;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView,mobileNoText,customer_text;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            mobileNoText = (TextView) itemView.findViewById(R.id.mobileNoTextView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            customer_text = itemView.findViewById(R.id.customer_text);
        }
    }

    @Override
    public CustomerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.customer_row_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CustomerRecyclerViewAdapter.ViewHolder holder, int position) {
        Customer customers = customerDataList.get(position);
        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(customers.getName());
        TextView mobileNoText = holder.mobileNoText;
        mobileNoText.setText(customers.getMobile());
        if(customers.getCustomer_type()!=null&&customers.getCustomer_type().equals("1")) {
            holder.customer_text.setText("FARMER");
            holder.customer_text.setTextColor(mContext.getResources().getColor(R.color.customer_back_color_text));
            holder.customer_text.setBackgroundColor(mContext.getResources().getColor(R.color.customer_back_color));
        }else  if(customers.getCustomer_type()!=null&&customers.getCustomer_type().equals("2")) {
            holder.customer_text.setText("FRANCHISE");
            holder.customer_text.setTextColor(mContext.getResources().getColor(R.color.cust_fre_back_color_text));
            holder.customer_text.setBackgroundColor(mContext.getResources().getColor(R.color.cust_fre_back_color));
        }else  if(customers.getCustomer_type()!=null&&customers.getCustomer_type().equals("3")) {
            holder.customer_text.setTextColor(mContext.getResources().getColor(R.color.customer_del_color_text));
            holder.customer_text.setText("DEALER");
            holder.customer_text.setBackgroundColor(mContext.getResources().getColor(R.color.customer_del_color));

        }

    }


    @Override
    public int getItemCount() {
        if(customerDataList==null){
            return 0;
        }
        return customerDataList.size();
    }
}