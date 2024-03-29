package com.growit.posapp.fstore.ui.fragments.Inventory;

import android.content.Context;
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
import com.growit.posapp.fstore.adapters.PurchaseItemListAdapter;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.interfaces.ItemClickListener;
import com.growit.posapp.fstore.tables.PurchaseOrder;
import com.growit.posapp.fstore.tables.TransferOrder;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.List;

public class TransferItemListAdapter extends RecyclerView.Adapter<TransferItemListAdapter.ViewHolder> {

    private List<TransferOrder> customerDataList;
    private Context mContext;
    ItemClickListener mCallback;

    String product_type;

    public TransferItemListAdapter(Context context, List<TransferOrder> contacts) {
        customerDataList = contacts;
        mContext = context;

    }

    public void setOnClickListener(ItemClickListener mCallback) {
        this.mCallback = mCallback;
    }


    public void filterList(List<TransferOrder> filterer) {
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
            itemVariants = itemView.findViewById(R.id.itemVariants);
            itemImage = itemView.findViewById(R.id.itemImage);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            unit_price = itemView.findViewById(R.id.unit_price);
            number_picker.setMax(10000);
            number_picker.setMin(1);
            number_picker.setUnit(1);
            number_picker.setValue(1);

        }
    }

    @Override
    public TransferItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.trnsf_itemlist_row, parent, false);
        TransferItemListAdapter.ViewHolder viewHolder = new TransferItemListAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TransferItemListAdapter.ViewHolder holder, int position) {
        TransferOrder product = customerDataList.get(position);
        TextView textView = holder.itemName;
        textView.setText(product.getProductName());
        holder.number_picker.setMax(1000000000);

        Glide.with(mContext)
                .load(ApiConstants.BASE_URL + product.getProductImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.itemImage);
        int quantity = (int) product.getQuantity();
        holder.number_picker.setValue(quantity);
        TextView variants = holder.itemVariants;
        variants.setText(product.getProductVariants());

        holder.number_picker.setVisibility(View.VISIBLE);
        holder.number_picker.setValueChangedListener((value, action) -> {

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
                    .transferDao()
                    .deleteItem(id, variant);
        });

    }

    private void setProductDetail(TransferOrder product) {
        AsyncTask.execute(() -> {
            int prodCount = 0;

            prodCount = DatabaseClient.getInstance(mContext).getAppDatabase().transferDao().getProductDetailById(product.getProductID(), product.getProductVariants(),product.getUnitPrice());
            if (prodCount > 0) {
                DatabaseClient.getInstance(mContext).getAppDatabase().transferDao().updateProductCardQuantity((int) product.getQuantity(), product.getProductID(),product.getProductVariants(),product.getUnitPrice());

            } else {
                DatabaseClient.getInstance(mContext).getAppDatabase().transferDao().insert(product);
            }
            //  SaveCardListToSomeActivity();

        });

    }

}
