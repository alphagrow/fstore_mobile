package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.Value;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.ViewHolder> {
    int selectedPosition = -1;
    private List<Value> customerDataList;
    private Context mContext;

    public CropAdapter(Context context, List<Value> contacts,int pos) {
        customerDataList = contacts;
        mContext = context;
        selectedPosition=pos;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        ImageView productThumb;
        LinearLayout card;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameBtn);
            productThumb = itemView.findViewById(R.id.productThumb);
            card = itemView.findViewById(R.id.card);
        }
    }

    @Override
    public CropAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.card_items, parent, false);
        CropAdapter.ViewHolder viewHolder = new CropAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CropAdapter.ViewHolder holder, int position) {

        Value customers = customerDataList.get(position);
        holder.nameTextView.setText(customers.getValueName());

//        holder.card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectedPosition = position;
//                notifyDataSetChanged();
//
//            }
//        });
        if (selectedPosition == position)
            holder.nameTextView.setTextColor(Color.parseColor("#3E9547"));
        else
            holder.nameTextView.setTextColor(Color.parseColor("#808080"));
        Picasso.with(mContext).load(customers.getImage_url())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.productThumb);
    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}