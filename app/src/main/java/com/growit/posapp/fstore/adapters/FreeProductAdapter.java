package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.DiscountListModel;

import java.util.List;

public class FreeProductAdapter extends RecyclerView.Adapter<FreeProductAdapter.ViewHolder> {

    private List<DiscountListModel> customerDataList;
    private Context mContext;
    private boolean clickable = true;
    public FreeProductAdapter(Context context, List<DiscountListModel> contacts) {
        customerDataList = contacts;
        mContext = context;

    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView thumbnail;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productTitle);
            thumbnail = itemView.findViewById(R.id.productThumb);
            card =itemView.findViewById(R.id.card);

        }

    }

    @Override
    public FreeProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.free_items, parent, false);

        FreeProductAdapter.ViewHolder viewHolder = new FreeProductAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FreeProductAdapter.ViewHolder holder, int position) {
        DiscountListModel product = customerDataList.get(position);
        TextView textView = holder.nameTextView;
        textView.setText(product.getProductName());
//        Picasso.with(mContext).load(ApiConstants.BASE_URL + product.getProductImage())
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.no_image)
//                .into(holder.thumbnail);



    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}