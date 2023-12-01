package com.growit.posapp.fstore.adapters;

import android.content.Context;
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
import com.growit.posapp.fstore.model.AttributeModel;
import com.growit.posapp.fstore.model.AttributeValue;
import com.growit.posapp.fstore.model.ListAttributesModel;
import com.growit.posapp.fstore.model.Value;
import com.growit.posapp.fstore.ui.fragments.AddProduct.CreateAttributeFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.AddPOSCategoryFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.utils.Utility;
import com.skyhope.showmoretextview.ShowMoreTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.ViewHolder> {


    private List<AttributeModel> list;
    private Context mContext;

    public AttributeAdapter(Context context, List<AttributeModel> contacts) {
        list = contacts;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView attribute_name;
        ImageView deleteBtn,update;
        LinearLayout card;
       TextView value;
        public ViewHolder(View itemView) {
            super(itemView);
            attribute_name = itemView.findViewById(R.id.attribute_name);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            update = itemView.findViewById(R.id.update);
            value = itemView.findViewById(R.id.value);
        }
    }

    @Override
    public AttributeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.attribute_item_row, parent, false);
        AttributeAdapter.ViewHolder viewHolder = new AttributeAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull AttributeAdapter.ViewHolder holder, int position) {
        AttributeModel model = list.get(position);
        holder.attribute_name.setText(model.getName());
        List<AttributeValue> value = list.get(position).getValues();
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<value.size();i++){
            stringBuilder.append(value.get(i).getName());
            if (i != value.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        holder.value.setText(stringBuilder);

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("attribute_list", (Serializable) list);
                bundle.putInt("position", position);
                Fragment fragment = CreateAttributeFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();


            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete ?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    AttributeModel model = list.get(position);
                    getDelete(String.valueOf(model.getId()),position);

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    private void getDelete(String id,int position) {
        SessionManagement sm = new SessionManagement(mContext);
        RequestQueue queue = Volley.newRequestQueue(mContext);//162.246.254.203:8069
        String url = ApiConstants.BASE_URL + ApiConstants.DELETE_ATTRIBUTE_VALUES  + "attribute_id=" + id+ "&"+ "user_id=" + sm.getUserID() + "&" +"token=" + sm.getJWTToken();
        Log.v("delete_product_url", url);
        Utility.showDialoge("Please wait while a moment...", mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                Utility.dismissDialoge();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.toString());
                    int statusCode = obj.optInt("statuscode");
                    String status = obj.optString("status");
                    String message = obj.optString("message");
                    String error_message = obj.optString("error_message");

                    if (status.equalsIgnoreCase("success")) {
                        list.remove(position);
                        notifyDataSetChanged();

                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();

                    }
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, error -> Toast.makeText(mContext, R.string.JSONDATA_NULL, Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}