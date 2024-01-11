package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.model.ProductDetail;
import com.growit.posapp.fstore.model.UomCategoryModel;

import java.util.ArrayList;
import java.util.List;

public class UOMAdapter extends RecyclerView.Adapter<UOMAdapter.ViewHolder> {


    private List<UomCategoryModel> list;
    private Context mContext;

    public UOMAdapter(Context context, List<UomCategoryModel> contacts) {
        list = contacts;
        mContext = context;
    }

    public void updateList(ArrayList<UomCategoryModel> modellist) {
        this.list=modellist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        ImageView productThumb,deleteBtn,update;
        LinearLayout card;
        TextView name_value_text,name;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            name_value_text = itemView.findViewById(R.id.name_value_text);


        }
    }

    @Override
    public UOMAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.uom_item_row, parent, false);
        UOMAdapter.ViewHolder viewHolder = new UOMAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull UOMAdapter.ViewHolder holder, int position) {
        UomCategoryModel model = list.get(position);
        holder.name.setText(model.getName());
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<model.getUomLines().size();i++){
            stringBuilder.append(model.getUomLines().get(i).getName());
            if (i != model.getUomLines().size() - 1) {
                stringBuilder.append(", ");
            }

        }
        holder.name_value_text.setText(stringBuilder);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
