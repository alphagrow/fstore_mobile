package com.growit.posapp.fstore.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.growit.posapp.fstore.db.DatabaseClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SessionManagement {

    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "GDBROS";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_LOGIN = "LOGIN";
    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String KEY_MOBILE = "MOBILE";
    public static final String KEY_USERPROFILE = "USER_PROFILE";
    public static final String KEY_PUSH_TOKEN = "PUSH_TOKEN";

    public static final String KEY_JWT_TOKEN = "JWT_TOKEN";
    public static final String KEY_COMPANY_ID = "COMPANY_ID";
    public static final String KEY_DEVICE_ID = "DEVICE_ID";

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(boolean isLogin, String name,String login,String password,String token) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_JWT_TOKEN, token);
        // commit changes
        editor.commit();
    }

    public void saveSessionID(String _id) {
        editor.putString("session_id", _id);
        // commit changes
        editor.commit();
    }
    public int getCompanyID() {
        int id=pref.getInt(KEY_COMPANY_ID,-1);
        // return user
        return id;
    }

    public void saveCompanyId(int user_id) {
        editor.putInt(KEY_COMPANY_ID, user_id);
        // commit changes
        editor.commit();
    }
    public void saveCrop(String _cropName) {
        editor.putString("cropName", _cropName);
        // commit changes
        editor.commit();
    }
    public String getCropName() {
        String name=pref.getString("cropName","");
        return name;
    }
    public String getSessionID() {
        String id=pref.getString("session_id","");
        // return user
        return id;
    }
    public String getLogin() {
        String login=pref.getString(KEY_LOGIN, null);
        // return user
        return login;
    }
    public String getPassword() {
        String password=pref.getString(KEY_PASSWORD, null);
        // return user
        return password;
    }

    public String getJWTToken() {
        String password=pref.getString(KEY_JWT_TOKEN, null);
        // return user
        return password;
    }


    public static void setDynamicHeight(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 220;
        int items = gridViewAdapter.getCount();

        totalHeight *= items;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    public static int getNotificationCount(Context context) {
        SharedPreferences settings;
        int pos;
        settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); //1
        pos = settings.getInt("NotificationCount", 0); //2
        System.out.println("===" + pos);
        return pos;
    }


    public String getUserName() {
        String name=pref.getString(KEY_NAME, null);
        // return user
        return name;
    }
    public int getShopID() {
        int id=pref.getInt("shop_id",-1);
        // return user
        return id;
    }
    public void saveShopId(int shop_id) {
        editor.putInt("shop_id", shop_id);
        // commit changes
        editor.commit();
    }
    public int getUserID() {
        int id=pref.getInt("user_id",-1);
        // return user
        return id;
    }

    public void saveUserId(int user_id) {
        editor.putInt("user_id", user_id);
        // commit changes
        editor.commit();
    }
    public int getCustomerType() {
        int id=pref.getInt("Type",1);
        // return user
        return id;
    }

    public void saveCustomerType(int count) {
        editor.putInt("Type", count);
        // commit changes
        editor.commit();
    }


    public void saveUserProfileName(String content) {
        editor.putString(KEY_USERPROFILE, content);
        // commit changes
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); //1
        text = settings.getString(KEY_NAME, ""); //2
        return text;
    }

    public static String getUserMObile(Context context) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); //1
        text = settings.getString(KEY_MOBILE, ""); //2
        return text;
    }


    public static String getUserProfile(Context context) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); //1
        text = settings.getString(KEY_USERPROFILE, ""); //2
        return text;
    }

    public boolean saveReporterConfig(String[] array, String arrayName) {
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putString(arrayName + "" + i, array[i]);
        return editor.commit();
    }

    public String[] loadReporterConfig(String arrayName) {
        int size = pref.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = pref.getString(arrayName + "" + i, null);
        return array;
    }


    public static int dpToPx(Context _context, int dp) {
        float density = _context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        totalHeight += 120;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static boolean getToken(Context context) {
        SharedPreferences settings;
        boolean login;
        settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        login = settings.getBoolean("IsLoggedIn",false );
        return login;
    }

    public String loadJSONFromAsset(Context _context) {
        String json = null;
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(_context.getAssets().open(
                    "test.json")));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        json = sb.toString();
        return json;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            return false;

        } else {
            return true;
        }
    }

    public String getDeviceId() {
        return pref.getString(KEY_DEVICE_ID, null);
    }

    public String getDeviceIdForIReporter() {
        return pref.getString(KEY_DEVICE_ID, "0");
    }


    public void setDeviceID(Context context) {
        editor.putString(KEY_DEVICE_ID, getMacAddr(context));
        // commit changes
        editor.commit();
    }

    public static String getMacAddr(Context context) {
        String id = "";
        try {

            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            System.out.println("---Deviceid" + id);
        } catch (Exception ex) {
            //handle exception
        }
        return id;
    }


    public String getPushToken() {
        return pref.getString(KEY_PUSH_TOKEN, null);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        AsyncTask.execute(() -> {
            DatabaseClient.getInstance(_context).getAppDatabase()
                    .customerDao().delete();
        });
        editor.clear();
        editor.commit();

    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public static String getFB(Context context) {
        SharedPreferences settings;
        String value;
        settings = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); //1
        value = settings.getString("FB", "1"); //2
        return value;
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

    public static void facebookShare(Context context, String urlToShare) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        boolean facebookAppFound = false;
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        if (!facebookAppFound) {
            createChooser(context, urlToShare);
            return;
        }
        context.startActivity(intent);
    }

    public static void createChooser(Context context, String content) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing News Nation Content...");
        context.startActivity(Intent.createChooser(sharingIntent, "Sharing News Nation Content...").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}