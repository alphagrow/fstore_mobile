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
import com.growit.posapp.fstore.model.WarehouseModel;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.UpdateVendorFragment;
import com.skyhope.showmoretextview.ShowMoreTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WareHouseAdapter extends RecyclerView.Adapter<WareHouseAdapter.ViewHolder> {

    private List<WarehouseModel> list;
    private Context mContext;


    public WareHouseAdapter(Context context, List<WarehouseModel> contacts) {
        list = contacts;
        mContext = context;
    }
    public void updateList(ArrayList<WarehouseModel> modellist) {
        this.list=modellist;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTxt,code,company_id;
        ShowMoreTextView product_name;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            code= itemView.findViewById(R.id.code);
            company_id = itemView.findViewById(R.id.company_id);




        }
    }

    @Override
    public WareHouseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.ware_house_item_row, parent, false);
        WareHouseAdapter.ViewHolder viewHolder = new WareHouseAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull WareHouseAdapter.ViewHolder holder, int position) {
        WarehouseModel modelList = list.get(position);
        holder.nameTxt.setText(modelList.getName());
        holder.code.setText(modelList.getCode());
        holder.company_id.setText(modelList.getCompanyId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("warehouse_list", (Serializable) list);
                bundle.putInt("position", position);
                bundle.putString("type_of_vendor_warehouse","warehouse");
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