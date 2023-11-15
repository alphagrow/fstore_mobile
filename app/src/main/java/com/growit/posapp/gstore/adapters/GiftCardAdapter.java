package com.growit.posapp.gstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.model.GiftCardListModel;


import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class GiftCardAdapter extends RecyclerView.Adapter<GiftCardAdapter.ViewHolder> {

    private List<GiftCardListModel> giftcardList;
    private Context mContext;

    public GiftCardAdapter(Context context, List<GiftCardListModel> contacts) {
        giftcardList = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text_code, text_amount, active, expiry_date;

        public ViewHolder(View itemView) {
            super(itemView);
            text_code = itemView.findViewById(R.id.text_code);
            text_amount = itemView.findViewById(R.id.text_amount);
            active = itemView.findViewById(R.id.active);
            expiry_date = itemView.findViewById(R.id.expiry_date);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.giftcard_item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date1, date2;
        GiftCardListModel model = giftcardList.get(position);
        holder.text_code.setText(model.getCode());
        holder.text_amount.setText("₹ " + model.getValue());
        holder.expiry_date.setText("Expiry : " + model.getExpirationDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1 = sdf.parse("2020-02-01");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        try {
            date2 = sdf.parse("2020-02-01");
           // date2 = sdf.parse(model.getExpirationDate().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (date1.equals(date2)) {
            holder.active.setText("Active");
            holder.active.setTextColor(Color.parseColor("#ff0000"));
        }

        if (date1.after(date2)) {
            holder.active.setText("Active");
            holder.active.setTextColor(Color.parseColor("#ff0000"));

        }

        if (date1.before(date2)) {
            holder.active.setTextColor(Color.parseColor("#0000"));

            holder.active.setText("Inactive");
        }

    }


    @Override
    public int getItemCount() {
        return giftcardList.size();
    }

}