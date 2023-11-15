package com.growit.posapp.gstore;

import static com.growit.posapp.gstore.utils.ApiConstants.RazorPayProductionKey;
import static com.growit.posapp.gstore.utils.ApiConstants.RazorPayTestKey;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.growit.posapp.gstore.db.DatabaseClient;
import com.growit.posapp.gstore.interfaces.RozarPayCallBack;
import com.growit.posapp.gstore.ui.fragments.ItemCartFragment;
import com.growit.posapp.gstore.ui.fragments.ProductListFragment;
import com.growit.posapp.gstore.utils.ApiConstants;
import com.growit.posapp.gstore.utils.Utility;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private AlertDialog.Builder alertDialogBuilder;
    public RozarPayCallBack rozarPayListener;
    double paidAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        if (intent != null) {
            paidAmount = Utility.decimalFormat(intent.getDoubleExtra("amount", 0.0));
//            Log.i("Payments",paidAmount+"");
        }

        if (this instanceof RozarPayCallBack)
            rozarPayListener = (RozarPayCallBack) this;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            //do nothing
        });
        startPayment();
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            sendIntentToMainActivity(s);
            Log.i("Payments", "Payment Successful :\nPayment ID: " + s + "\nPayment Data: " + paymentData.getData());
            finish();
//            alertDialogBuilder.setMessage("Payment Successful :\nPayment ID: " + s + "\nPayment Data: " + paymentData.getData());
//            alertDialogBuilder.show();
//            DatabaseClient.getInstance(PaymentActivity.this).getAppDatabase()
//                    .productDao()
//                    .delete();
//            sendIntentToMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendIntentToMainActivity(String trns) {
        Intent intent = new Intent();
        intent.setAction(ApiConstants.ACTION_PAYMENT);
        intent.putExtra("dataToPass", "4");
        intent.putExtra("transactionid", trns);
        sendBroadcast(intent);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try {
            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: " + paymentData.getData());
            alertDialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPayment() {
        final Checkout co = new Checkout();
        co.setKeyID(RazorPayTestKey);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Growit India Pvt. Ltd.");
            options.put("description", "Growit Payments");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://images.yourstory.com/cs/images/companies/GrowIt-1649686899317.jpg");
            options.put("currency", "INR");
            options.put("amount", paidAmount * 100.00);
            JSONObject preFill = new JSONObject();
//            preFill.put("email", "ravi@alpha.co.in");
//            preFill.put("contact", "8447408228");
            options.put("prefill", preFill);
            co.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

}