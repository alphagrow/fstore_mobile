package com.growit.posapp.fstore.adapters;

import static java.security.AccessController.getContext;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.fstore.R;
import com.growit.posapp.fstore.model.Product;
import com.growit.posapp.fstore.model.Purchase.PurchaseOrderLine;
import com.growit.posapp.fstore.ui.fragments.UpdateUserActivity;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class PurchaseOrderDetailAdapter extends RecyclerView.Adapter<PurchaseOrderDetailAdapter.ViewHolder> {

    private List<PurchaseOrderLine> customerDataList;
    private Context mContext;

    public PurchaseOrderDetailAdapter(Context context, List<PurchaseOrderLine> contacts) {
        customerDataList = contacts;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView qty, total_amount, pac_text, crop_text;
        ImageView product_Image;
        ShowMoreTextView textView;
        public  ShowMoreTextView product_name;
        EditText edi_qut_text;
        EditText mfd_date;

        public ViewHolder(View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            total_amount = itemView.findViewById(R.id.total);
            qty = itemView.findViewById(R.id.qut_text);
            product_Image = itemView.findViewById(R.id.product_Image);
            pac_text = itemView.findViewById(R.id.pac_text);
            crop_text = itemView.findViewById(R.id.crop_text);
            edi_qut_text = itemView.findViewById(R.id.edi_qut_text);
            mfd_date = itemView.findViewById(R.id.edi_mfd_date);
        }
    }

    @Override
    public PurchaseOrderDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.pur_order_detail_layout, parent, false);
        PurchaseOrderDetailAdapter.ViewHolder viewHolder = new PurchaseOrderDetailAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull PurchaseOrderDetailAdapter.ViewHolder holder, int position) {
        PurchaseOrderLine orders = customerDataList.get(position);
        holder.product_name.setText(orders.getProductName());

//        holder.pac_text.setText(orders.getProduct_package());
//        if (!orders.getCrop_name().equalsIgnoreCase("false")) {
//            holder.crop_text.setText(orders.getCrop_name());
//        }

        holder.edi_qut_text.setText(String.valueOf(orders.getQuantity()));
        holder.product_name.setText(orders.getProductName());
        holder.product_name.setShowingChar(70);
        holder.product_name.setShowingLine(2);
        holder.product_name.addShowMoreText("");
        holder.product_name.addShowLessText("Less");
        holder.product_name.setShowMoreColor(Color.BLACK); // or other color
        holder.product_name.setShowLessTextColor(Color.RED); // or other color
        holder.qty.setText("Qty : " + orders.getQuantity());
        holder.total_amount.setText("Rs. "+orders.getPrice() + "");
//holder.edi_qut_text.addTextChangedListener(new TextWatcher() {
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if(!s.toString().isEmpty()) {
//            if(Double.parseDouble(s.toString())<=orders.getQuantity()){
//
//            }else {
//                Toast.makeText(mContext, "invalid Qty", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//
//    }
//});
        holder.mfd_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                holder.mfd_date.setText(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth );

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });



//        Picasso.with(mContext).load(ApiConstants.BASE_URL + orders.getProductImageUrl())
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.no_image)
//                .into(holder.product_Image);


    }


    @Override
    public int getItemCount() {
        return customerDataList.size();
    }
}