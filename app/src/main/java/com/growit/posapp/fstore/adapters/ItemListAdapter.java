package com.growit.posapp.fstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.growit.posapp.fstore.utils.ApiConstants;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder>{

    private List<PosOrder> customerDataList;
    private Context mContext;
    ItemClickListener mCallback;

    String product_type;
    public ItemListAdapter(Context context, List<PosOrder> contacts) {
        customerDataList = contacts;
        mContext = context;

    }

    public void setOnClickListener(ItemClickListener mCallback) {
        this.mCallback = mCallback;
    }


    public void filterList(List<PosOrder> filterer) {
        customerDataList = filterer;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemPriceTxt, itemVariants,item_Txt;
        EditText discount_per;
        public ImageView itemImage, deleteBtn;
        public NumberPicker number_picker;
        public ViewHolder(View itemView) {
            super(itemView);
            number_picker= itemView.findViewById(R.id.number_picker);
            itemName = itemView.findViewById(R.id.itemName);
            itemPriceTxt = itemView.findViewById(R.id.unit_price);
            itemVariants = itemView.findViewById(R.id.itemVariants);
            itemImage = itemView.findViewById(R.id.itemImage);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            discount_per = itemView.findViewById(R.id.discount_per);
            item_Txt = itemView.findViewById(R.id.item_Txt);
        //    number_picker.setMax(100);
            number_picker.setMin(1);
            number_picker.setUnit(1);
            number_picker.setValue(1);
        }
    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.itemlist_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        PosOrder product = customerDataList.get(position);
        holder.item_Txt.setText("Tax : "+String.valueOf(product.getGst())+" %");
        holder.discount_per.setText(String.valueOf(product.getDiscount_per()));
        holder.discount_per.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!s.toString().isEmpty()) {
//                    holder.discount_per.setText(String.valueOf(product.getDiscount_per()));
                    product.setDiscount_per(Double.parseDouble(s.toString()));

                    Log.d("discount_f", s.toString());
                    AsyncTask.execute(() -> {
                        DatabaseClient.getInstance(mContext).getAppDatabase().productDao().updateProductCardDiscount(product.getDiscount_per(),product.getProductID(),product.getProductVariants(),(int)product.getQuantity());
                        SaveCardListToSomeActivity();
                    });
                    mCallback.onClick(holder.getAdapterPosition());

                }else {

                    product.setDiscount_per(0.0);
                    AsyncTask.execute(() -> {
                        DatabaseClient.getInstance(mContext).getAppDatabase().productDao().updateProductCardDiscount(product.getDiscount_per(),product.getProductID(),product.getProductVariants(),(int)product.getQuantity());
                        SaveCardListToSomeActivity();
                    });
                    mCallback.onClick(holder.getAdapterPosition());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private void setProductDetail(PosOrder product){
        AsyncTask.execute(() -> {
            int prodCount=0;

            prodCount=DatabaseClient.getInstance(mContext).getAppDatabase().productDao().getProductDetailById(product.getProductID(),product.getProductVariants());
            if(prodCount>0){
                DatabaseClient.getInstance(mContext).getAppDatabase().productDao().updateProductCardQuantity((int)product.getQuantity(),product.getProductID(),product.getProductVariants());

            }else{
                DatabaseClient.getInstance(mContext).getAppDatabase().productDao().insert(product);
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