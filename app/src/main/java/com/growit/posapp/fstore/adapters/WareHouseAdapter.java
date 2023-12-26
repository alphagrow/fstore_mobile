package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.UpdateVendorFragment;
import com.skyhope.showmoretextview.ShowMoreTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WareHouseAdapter extends RecyclerView.Adapter<WareHouseAdapter.ViewHolder> {

    private List<VendorModelList> list;
    private Context mContext;


    public WareHouseAdapter(Context context, List<VendorModelList> contacts) {
        list = contacts;
        mContext = context;
    }
    public void updateList(ArrayList<VendorModelList> modellist) {
        this.list=modellist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTxt,mobile,status;
        ImageView deleteBtn,acti_img,acti_img_2,update;
        ShowMoreTextView product_name;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            mobile= itemView.findViewById(R.id.mobile);
            deleteBtn= itemView.findViewById(R.id.deleteBtn);
            acti_img = itemView.findViewById(R.id.acti_img);
            acti_img_2 = itemView.findViewById(R.id.acti_img_2);
            status = itemView.findViewById(R.id.status);
            update = itemView.findViewById(R.id.update);


        }
    }

    @Override
    public WareHouseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.vendor_item_row, parent, false);
        WareHouseAdapter.ViewHolder viewHolder = new WareHouseAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull WareHouseAdapter.ViewHolder holder, int position) {
        VendorModelList modelList = list.get(position);
        holder.nameTxt.setText(modelList.getName());
        holder.mobile.setText(modelList.getMobile());

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("vendor_list", (Serializable) list);
                bundle.putInt("position", position);
                Fragment fragment = UpdateVendorFragment.newInstance();
                fragment.setArguments(bundle);
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