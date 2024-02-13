package com.growit.posapp.fstore.ui.fragments.AddProduct;

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
import com.growit.posapp.fstore.adapters.ConfigurationAdapter;
import com.growit.posapp.fstore.model.ConfigurationModel;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private List<ConfigurationModel> list;
    private Context mContext;

    public UserAdapter(Context context, List<ConfigurationModel> contacts) {
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
        TextView id,name,ware_text,login_text;
        public ViewHolder(View itemView) {
            super(itemView);
//            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.location_text);
            //        ware_text = itemView.findViewById(R.id.ware_text);
            login_text = itemView.findViewById(R.id.login_text);

        }
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.user_row, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        ConfigurationModel model = list.get(position);
        holder.name.setText(model.getName());
        holder.login_text.setText(model.getLogin());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}