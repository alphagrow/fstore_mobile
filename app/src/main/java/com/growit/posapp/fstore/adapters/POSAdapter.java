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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.StockInventoryModelList;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.AddProduct.UpdateAddProductFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.AddPOSCategoryFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class POSAdapter extends RecyclerView.Adapter<POSAdapter.ViewHolder> {


    private List<StockInventoryModelList> list;
private Context mContext;

    public POSAdapter(Context context, List<StockInventoryModelList> contacts) {
        list = contacts;
        mContext = context;
    }


public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView nameTextView;
    ImageView productThumb;
    LinearLayout card;
    ShowMoreTextView product_name_text;
    public ViewHolder(View itemView) {
        super(itemView);
        product_name_text = itemView.findViewById(R.id.product_name_text);
        productThumb = itemView.findViewById(R.id.images);
        card = itemView.findViewById(R.id.card);
    }
}

    @Override
    public POSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.product_item_row, parent, false);
        POSAdapter.ViewHolder viewHolder = new POSAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull POSAdapter.ViewHolder holder, int position) {
        StockInventoryModelList model = list.get(position);
        holder.product_name_text.setText(model.getProductName());
        holder.product_name_text.setShowingChar(100);
        holder.product_name_text.setShowingLine(2);
        holder.product_name_text.addShowMoreText("");
        holder.product_name_text.addShowLessText("Less");
        holder.product_name_text.setShowMoreColor(Color.BLACK); // or other color
        holder.product_name_text.setShowLessTextColor(Color.RED); // or other color
        Picasso.with(mContext).load(ApiConstants.BASE_URL + model.getProductImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .into(holder.productThumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AddPOSCategoryFragment.newInstance();
                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}