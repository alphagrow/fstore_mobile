package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ItemClickListener;
import com.growit.posapp.fstore.tables.PosOrder;
import com.growit.posapp.fstore.tables.PurchaseOrder;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.List;

public class PurchaseItemListAdapter extends RecyclerView.Adapter<PurchaseItemListAdapter.ViewHolder>{

    private List<PurchaseOrder> customerDataList;
    private Context mContext;
    ItemClickListener mCallback;

    String product_type;
    public PurchaseItemListAdapter(Context context, List<PurchaseOrder> contacts) {
        customerDataList = contacts;
        mContext = context;

    }

    public void setOnClickListener(ItemClickListener mCallback) {
        this.mCallback = mCallback;
    }


    public void filterList(List<PurchaseOrder> filterer) {
        customerDataList = filterer;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemPriceTxt, itemVariants;
        public ImageView itemImage, deleteBtn;
        public NumberPicker number_picker;
        public ViewHolder(View itemView) {
            super(itemView);
            number_picker= itemView.findViewById(R.id.number_picker);
            itemName = itemView.findViewById(R.id.itemName);
            itemPriceTxt = itemView.findViewById(R.id.itemPriceTxt);
            itemVariants = itemView.findViewById(R.id.itemVariants);
            itemImage = itemView.findViewById(R.id.itemImage);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            //    number_picker.setMax(100);
            number_picker.setMin(1);
            number_picker.setUnit(1);
            number_picker.setValue(1);
        }
    }

    @Override
    public PurchaseItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.itemlist_row, parent, false);
        PurchaseItemListAdapter.ViewHolder viewHolder = new PurchaseItemListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull PurchaseItemListAdapter.ViewHolder holder, int position) {
        PurchaseOrder product = customerDataList.get(position);
        TextView textView = holder.itemName;
        textView.setText(product.getProductName());
        holder.number_picker.setMax(product.getTotalQuantity());

        Glide.with(mContext)
                .load(ApiConstants.BASE_URL + product.getProductImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.itemImage);
        int quantity= (int) product.getQuantity();
        holder.itemPriceTxt.setText("Rs. "+product.getUnitPrice()* quantity + "");
        holder.number_picker.setValue(quantity);
        TextView variants = holder.itemVariants;
        variants.setText(product.getProductVariants());
        holder.number_picker.setVisibility(View.VISIBLE);
        holder.number_picker.setValueChangedListener((value, action) -> {
            holder.itemPriceTxt.setText("Rs. "+product.getUnitPrice() * value + "");
            product.setQuantity(value);
            setProductDetail(product);
            mCallback.onClick(holder.getAdapterPosition());
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (customerDataList.size() > 0) {
                sendIntentToMainActivity(customerDataList.get(holder.getAdapterPosition()).getProductID(),customerDataList.get(holder.getAdapterPosition()).getProductVariants());
                customerDataList.remove(holder.getAdapterPosition());
                filterList(customerDataList);
                mCallback.onClick(holder.getAdapterPosition());
                //  notifyDataSetChanged();
            }

        });
    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }

    private void sendIntentToMainActivity(int id,String variant) {
        Intent intent = new Intent();
        intent.setAction(ApiConstants.ACTION);
        intent.putExtra("dataToPass", "3");
        intent.putExtra("ID", id);
        intent.putExtra("variant", variant);
        mContext.sendBroadcast(intent);
    }

    private void setProductDetail(PurchaseOrder product){
        AsyncTask.execute(() -> {
            int prodCount=0;

            prodCount= DatabaseClient.getInstance(mContext).getAppDatabase().purchaseDao().getProductDetailById(product.getProductID(),product.getProductVariants());
            if(prodCount>0){
                DatabaseClient.getInstance(mContext).getAppDatabase().purchaseDao().updateProductCardQuantity((int)product.getQuantity(),product.getProductID(),product.getProductVariants());

            }else{
                DatabaseClient.getInstance(mContext).getAppDatabase().purchaseDao().insert(product);
            }
            SaveCardListToSomeActivity();

        });

    }
    private void SaveCardListToSomeActivity() {
        Intent intent = new Intent();
        intent.setAction(ApiConstants.ACTION);
        intent.putExtra("dataToPass", "5");
        mContext.sendBroadcast(intent);
    }

}