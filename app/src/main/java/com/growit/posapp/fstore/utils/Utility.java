package com.growit.posapp.fstore.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {


    static ProgressDialog progressDialog;
    public static void showDialoge(String message,Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setMessage(message);
        progressDialog.setTitle("GStore");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
    public static void dismissDialoge(){
        progressDialog.cancel();
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("state.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static boolean isValidGSTNo(String str)
    {
        // GST (Goods and Services Tax) number
        String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}"
                + "[A-Z]{1}[1-9A-Z]{1}"
                + "Z[0-9A-Z]{1}$";
        Pattern p = Pattern.compile(regex);
        if (str == null)
        {
            return false;
        }
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }

            } else {

                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();

            }
        }
        return false;
    }


    public static double decimalFormatTaxAmount(double value) {
        DecimalFormat form = new DecimalFormat("0.0000");
        double shortDouble= Double.parseDouble(form.format(Double.valueOf(value)));

        return shortDouble;
    }
    public static double decimalFormat(double value) {
        int temp = (int)(value*100.0);
        double shortDouble = ((double)temp)/100.0;
//        int shortDouble = temp/100;
        return shortDouble;
    }

    public static double Calculate_GST(double totalCost, int gst) {
        // return value after calculate GST%
        double value = ((totalCost) * gst) / 100;
        return value;
    }

    public static double calculateDiscount(double totalCost, int percentage) {
        // return value after calculate GST%
        double discount = ((totalCost) * percentage) / 100;
        return discount;
    }
    public static void whatsupSharing(Context context, String urlToShare) {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
            boolean facebookAppFound = false;
            List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(sendIntent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.whatsapp")) {
                    sendIntent.setPackage(info.activityInfo.packageName);
                    facebookAppFound = true;
                    break;
                }
            }

            if (!facebookAppFound) {
                Toast.makeText(context, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                return;
            }
            context.startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
