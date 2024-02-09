package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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

public class PurchaseItemListAdapter extends RecyclerView.Adapter<PurchaseItemListAdapter.ViewHolder> {

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
        public TextView itemName, itemPriceTxt, itemVariants, item_Taxt, unit_price, item_total_amount;
        public ImageView itemImage, deleteBtn;
        public NumberPicker number_picker;

        public ViewHolder(View itemView) {
            super(itemView);
            number_picker = itemView.findViewById(R.id.number_picker);
            itemName = itemView.findViewById(R.id.itemName);
            itemPriceTxt = itemView.findViewById(R.id.itemPriceTxt);
            itemVariants = itemView.findViewById(R.id.itemVariants);
            itemImage = itemView.findViewById(R.id.itemImage);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            item_Taxt = itemView.findViewById(R.id.item_Txt);
            unit_price = itemView.findViewById(R.id.unit_price);
//            item_total_amount = itemView.findViewById(R.id.item_total_amount);
            number_picker.setMax(100);
            number_picker.setMin(1);
            number_picker.setUnit(1);
//            number_picker.setValue(1);

        }
    }

    @Override
    public PurchaseItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_purch_list_row, parent, false);
        PurchaseItemListAdapter.ViewHolder viewHolder = new PurchaseItemListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull PurchaseItemListAdapter.ViewHolder holder, int position) {
        PurchaseOrder product = customerDataList.get(position);
        TextView textView = holder.itemName;
        textView.setText(product.getProductName());
        holder.item_Taxt.setText("Tax : " + product.getTaxName()+"%");
        holder.number_picker.setMax(1000000000);

        Glide.with(mContext)
                .load(ApiConstants.BASE_URL + product.getProductImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.itemImage);
        int quantity = (int) product.getQuantity();
        holder.unit_price.setText("Rs. " + product.getUnitPrice() * quantity + "");
//        double total_amount = (product.getUnitPrice() * quantity) * (product.getTaxID()) / 100;
//        double total = (product.getUnitPrice() * quantity) + total_amount;
//        holder.item_total_amount.setText("Rs. " + String.valueOf(total));

        holder.number_picker.setValue(quantity);

        TextView variants = holder.itemVariants;
        variants.setText(product.getProductVariants());

        holder.number_picker.setVisibility(View.VISIBLE);
        holder.number_picker.setValueChangedListener((value, action) -> {
            holder.unit_price.setText("Rs. " + product.getUnitPrice() * value + "");
//            double total_amou = (product.getUnitPrice() * value) * (product.getTaxID()) / 100;
//            double total_item_amount = (product.getUnitPrice() * value) + total_amou;
//            holder.item_total_amount.setText("Rs. " + String.valueOf(total_item_amount));

            product.setQuantity(value);
            setProductDetail(product);
            mCallback.onClick(holder.getAdapterPosition());
        });
        holder.deleteBtn.setOnClickListener(v -> {
            if (customerDataList.size() > 0) {
                sendIntentToMainActivity(customerDataList.get(holder.getAdapterPosition()).getProductID(), customerDataList.get(holder.getAdapterPosition()).getProductVariants());
                customerDataList.remove(holder.getAdapterPosition());
                filterList(customerDataList);
                mCallback.onClick(holder.getAdapterPosition());
                //  notifyDataSetChanged();
            }

        });


//        holder.deleteBtn.setOnClickListener(v -> {
//            if (customerDataList.size() > 0) {
//                sendIntentToMainActivity(customerDataList.get(holder.getAdapterPosition()).getProductID(),customerDataList.get(holder.getAdapterPosition()).getProductVariants());
//                customerDataList.remove(holder.getAdapterPosition());
//                filterList(customerDataList);
//                mCallback.onClick(holder.getAdapterPosition());
//                //  notifyDataSetChanged();
//            }
//
//        });
    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }

    private void sendIntentToMainActivity(int id, String variant) {
        AsyncTask.execute(() -> {

            DatabaseClient.getInstance(mContext).getAppDatabase()
                    .purchaseDao()
                    .deleteItem(id, variant);
        });

    }

    private void setProductDetail(PurchaseOrder product) {
        AsyncTask.execute(() -> {
            int prodCount = 0;

            prodCount = DatabaseClient.getInstance(mContext).getAppDatabase().purchaseDao().getProductDetailById(product.getProductID(), product.getProductVariants(),product.getUnitPrice());
            if (prodCount > 0) {
                DatabaseClient.getInstance(mContext).getAppDatabase().purchaseDao().updateProductCardQuantity((int) product.getQuantity(), product.getProductID(),product.getProductVariants(),product.getUnitPrice());

            } else {
                DatabaseClient.getInstance(mContext).getAppDatabase().purchaseDao().insert(product);
            }
            //  SaveCardListToSomeActivity();

        });

    }

}