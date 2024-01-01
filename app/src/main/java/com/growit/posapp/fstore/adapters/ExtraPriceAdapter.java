package com.growit.posapp.fstore.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.PriceData;
import com.growit.posapp.fstore.tables.Customer;

import java.util.List;

public class ExtraPriceAdapter extends RecyclerView.Adapter<ExtraPriceAdapter.ViewHolder> {

    private List<PriceData> customerDataList;
    private Context mContext;

    // Pass in the contact array into the constructor
    public ExtraPriceAdapter(Context context, List<PriceData> contacts) {
        customerDataList = contacts;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        EditText mrpTxt,priceTxt;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.variantName);
            mrpTxt = (EditText) itemView.findViewById(R.id.mrp_text);
            priceTxt = (EditText) itemView.findViewById(R.id.priceTxt);
        }
    }

    @Override
    public ExtraPriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.extraprice_rowitem, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ExtraPriceAdapter.ViewHolder holder, int position) {
        PriceData customers = customerDataList.get(position);
        // Set item views based on your views and data model
        holder.nameTextView.setText(customers.getProductVariantName());
        holder.mrpTxt.setText(customers.getMrpPrice()+"");
        holder.priceTxt.setText(customers.getPriceExtra()+"");

    }


    @Override
    public int getItemCount() {
        if (customerDataList == null) {
            return 0;
        }
        return customerDataList.size();
    }
}
