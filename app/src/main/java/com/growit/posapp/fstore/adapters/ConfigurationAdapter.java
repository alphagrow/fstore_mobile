package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.ConfigurationModel;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.AddProduct.AddProductListFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.AddPOSCategoryFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationAdapter extends RecyclerView.Adapter<ConfigurationAdapter.ViewHolder> {


    private List<ConfigurationModel> list;
    private Context mContext;

    public ConfigurationAdapter(Context context, List<ConfigurationModel> contacts) {
        list = contacts;
        mContext = context;
    }

    public void updateList(ArrayList<ConfigurationModel> modellist) {
        this.list=modellist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        ImageView productThumb,deleteBtn,update;
        LinearLayout card;
        TextView id,name,ware_text,per_text;
        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.location_text);
            ware_text = itemView.findViewById(R.id.ware_text);
            per_text = itemView.findViewById(R.id.per_text);

        }
    }

    @Override
    public ConfigurationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.conf_item_row, parent, false);
        ConfigurationAdapter.ViewHolder viewHolder = new ConfigurationAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ConfigurationAdapter.ViewHolder holder, int position) {
        ConfigurationModel model = list.get(position);
        holder.id.setText(String.valueOf(model.getId()));
        holder.name.setText(model.getName());
        holder.ware_text.setText(model.getParentId());
        holder.per_text.setText(model.getPercentage());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}