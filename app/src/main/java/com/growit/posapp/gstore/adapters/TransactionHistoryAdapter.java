package com.growit.posapp.gstore.adapters;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growit.posapp.gstore.R;
import com.growit.posapp.gstore.model.Product;
import com.growit.posapp.gstore.model.Transaction;
import com.growit.posapp.gstore.utils.ApiConstants;
import com.growit.posapp.gstore.utils.SessionManagement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    private List<Transaction> transactionDataList;
    private Context mContext;
    SessionManagement sm;
    public TransactionHistoryAdapter(Context context, List<Transaction> contacts) {
        transactionDataList = contacts;
        mContext = context;
        sm=new SessionManagement(mContext);
    }
    public void filterList(ArrayList<Transaction> filterer) {
        transactionDataList = filterer;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTxt, trnNOTxt, amountTxt, dateTxt;
        public ImageView invoice;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            trnNOTxt = itemView.findViewById(R.id.trnNOTxt);
            amountTxt = itemView.findViewById(R.id.amountTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            invoice = itemView.findViewById(R.id.invoice);

        }
    }

    @Override
    public TransactionHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.transaction_item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionHistoryAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactionDataList.get(position);
        TextView textView = holder.nameTxt;
        textView.setText(transaction.getCustomerName());
        holder.trnNOTxt.setText(transaction.getPosOrder() + " [" + transaction.getPaymentMethod() + "]");
        DecimalFormat form = new DecimalFormat("0.00");
        holder.amountTxt.setText("₹ "+String.valueOf(form.format(Double.valueOf(transaction.getAmount()))));
        holder.dateTxt.setText(transaction.getPaymentDate());
        holder.invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPDF(transaction.getPos_order_id());
            }
        });
    }


    @Override
    public int getItemCount() {
        return transactionDataList.size();
    }



    public void showPDF(String orderno) {
        try {
            String url= ApiConstants.BASE_URL +ApiConstants.INVOICE_DOWNLOAD+orderno+"&user_id="+sm.getUserID()+"&token="+sm.getJWTToken();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "No Application available to view PDF", Toast.LENGTH_SHORT).show();

        }
    }
}