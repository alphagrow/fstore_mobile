package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.Value;

import java.util.List;

public class HorizontalWidgetAdapter extends RecyclerView.Adapter<HorizontalWidgetAdapter.ViewHolder> {

    private List<Value> customerDataList;
    private Context mContext;

    public HorizontalWidgetAdapter(Context context, List<Value> contacts) {
        customerDataList = contacts;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameBtn);
        }
    }

    @Override
    public HorizontalWidgetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.pattern_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull HorizontalWidgetAdapter.ViewHolder holder, int position) {
        Value customers = customerDataList.get(position);
        TextView textView = holder.nameTextView;
        textView.setText(customers.getValueName());
    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}