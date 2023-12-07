package com.growit.posapp.fstore;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.growit.posapp.fstore.db.DatabaseClient;
import com.growit.posapp.fstore.tables.PosOrder;
import com.growit.posapp.fstore.ui.LoginActivity;
import com.growit.posapp.fstore.ui.MyProfileActivity;
import com.growit.posapp.fstore.ui.fragments.AboutFragment;
import com.growit.posapp.fstore.ui.fragments.AddCustomerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.growit.posapp.fstore.ui.fragments.AddProduct.AttributeListFragment;
import com.growit.posapp.fstore.ui.fragments.AddProduct.AddProductFragment;
import com.growit.posapp.fstore.ui.fragments.ContactUsFragment;
import com.growit.posapp.fstore.ui.fragments.CustomerRecyclerViewFragment;
import com.growit.posapp.fstore.ui.fragments.Inventory.CreateWarehouseFragment;
import com.growit.posapp.fstore.ui.fragments.ItemCartFragment;
import com.growit.posapp.fstore.ui.fragments.OrderHistoryFragment;
import com.growit.posapp.fstore.ui.fragments.POSCategory.POSCategoryListFragment;
import com.growit.posapp.fstore.ui.fragments.ProductListFragment;
import com.growit.posapp.fstore.ui.fragments.PurchaseOrder.CreatePurchaseOrderFragment;
import com.growit.posapp.fstore.ui.fragments.SaleManagement.VendorListFragment;
import com.growit.posapp.fstore.ui.fragments.Inventory.StoreInventoryFragment;
import com.growit.posapp.fstore.ui.fragments.TransactionHistoryFragment;
import com.growit.posapp.fstore.utils.ApiConstants;
import com.growit.posapp.fstore.utils.SessionManagement;
import com.growit.posapp.fstore.volley.VolleyCallback;
import com.growit.posapp.fstore.volley.VolleyRequestHandler;
import com.razorpay.Checkout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer_layout;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    TextView titleTxt, cartBtn;
    SessionManagement sm;
    private MyBroadcastReceiver receiver;
    private LinearLayout toolbar_image_lay;
    TextView home_text,customer_text,cart_text,transaction_text,order_text;
    ImageView home_icon,customer_icon,transaction_icon,order_icon;
    LinearLayout  sale_menu_lay;
    LinearLayout inventory_menu;
    TextView vendor,purchase,inventory_text,warehouses;
    boolean isClicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant));
        setContentView(R.layout.activity_main);
        titleTxt = findViewById(R.id.titleTxt);
        Checkout.preload(getApplicationContext());
        receiver = new MyBroadcastReceiver();
        this.registerReceiver(receiver, new IntentFilter(ApiConstants.ACTION));
        sm = new SessionManagement(MainActivity.this);
        titleTxt.setText("Business Application");
        LinearLayout storeTab = findViewById(R.id.tab_store);
        LinearLayout orderTab = findViewById(R.id.tab_order);
        LinearLayout transactionTab = findViewById(R.id.tab_transaction);
        toolbar_image_lay = findViewById(R.id.toolbar_image_lay);
        LinearLayout customer = findViewById(R.id.customer);

        /////////////**********
        home_text = findViewById(R.id.home_text);
        customer_text = findViewById(R.id.customer_text);
        cart_text = findViewById(R.id.cart_text);
        transaction_text = findViewById(R.id.transaction_text);
        order_text = findViewById(R.id.order_text);


        //////////////**************** icon color

        home_icon = findViewById(R.id.home_icon);
        home_icon.setImageResource(R.drawable.home);
        customer_icon = findViewById(R.id.customer_icon);
        transaction_icon = findViewById(R.id.transaction_icon);
        order_icon = findViewById(R.id.order_icon);


        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customer_icon.setImageResource(R.drawable.profile_customer_color);
                home_icon.setImageResource(R.drawable.home_menu);
                transaction_icon.setImageResource(R.drawable.transaction);
                order_icon.setImageResource(R.drawable.order);

                /////****
                customer_text.setTextColor(getResources().getColor(R.color.colorPrimary));
                home_text.setTextColor(getResources().getColor(R.color.black));
                cart_text.setTextColor(getResources().getColor(R.color.black));
                transaction_text.setTextColor(getResources().getColor(R.color.black));
                order_text.setTextColor(getResources().getColor(R.color.black));
                toolbar.setVisibility(View.GONE);
                toolbar_image_lay.setVisibility(View.GONE);
                titleTxt.setVisibility(View.VISIBLE);
                titleTxt.setText("Customer");
                Fragment fragment = CustomerRecyclerViewFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });
        transactionTab.setOnClickListener(v -> {
            customer_icon.setImageResource(R.drawable.profile_customer);
            home_icon.setImageResource(R.drawable.home_menu);
            transaction_icon.setImageResource(R.drawable.transaction_color);
            order_icon.setImageResource(R.drawable.order);
            ///*****
            customer_text.setTextColor(getResources().getColor(R.color.black));
            home_text.setTextColor(getResources().getColor(R.color.black));
            cart_text.setTextColor(getResources().getColor(R.color.black));
            transaction_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            order_text.setTextColor(getResources().getColor(R.color.black));
            ///toolbar vistble
            toolbar.setVisibility(View.VISIBLE);
            toolbar_image_lay.setVisibility(View.GONE);
            titleTxt.setVisibility(View.VISIBLE);
            titleTxt.setText(R.string.Transaction);

            Fragment fragment = TransactionHistoryFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        });
        orderTab.setOnClickListener(v -> {
            customer_icon.setImageResource(R.drawable.profile_customer);
            home_icon.setImageResource(R.drawable.home_menu);
            transaction_icon.setImageResource(R.drawable.transaction);
            order_icon.setImageResource(R.drawable.order_color);
            ///***********
            customer_text.setTextColor(getResources().getColor(R.color.black));
            home_text.setTextColor(getResources().getColor(R.color.black));
            cart_text.setTextColor(getResources().getColor(R.color.black));
            transaction_text.setTextColor(getResources().getColor(R.color.black));
            order_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            ///***********///
            ///toolbar vistble
            toolbar.setVisibility(View.VISIBLE);
            toolbar_image_lay.setVisibility(View.GONE);
            titleTxt.setVisibility(View.VISIBLE);
            titleTxt.setText("Order History");
            Fragment fragment = OrderHistoryFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        });

        storeTab.setOnClickListener(v -> {
            customer_icon.setImageResource(R.drawable.profile_customer);
            home_icon.setImageResource(R.drawable.home);
            transaction_icon.setImageResource(R.drawable.transaction);
            order_icon.setImageResource(R.drawable.order);
            ///***********
            ///toolbar vistble
            toolbar.setVisibility(View.VISIBLE);
            customer_text.setTextColor(getResources().getColor(R.color.black));
            home_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            cart_text.setTextColor(getResources().getColor(R.color.black));
            transaction_text.setTextColor(getResources().getColor(R.color.black));
            order_text.setTextColor(getResources().getColor(R.color.black));
            ///***********
            callProductFragment();
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        drawer_layout = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        cartBtn = findViewById(R.id.cartBtn);
        FrameLayout card = findViewById(R.id.card);
        card.setOnClickListener(v ->
        {
            customer_text.setTextColor(getResources().getColor(R.color.black));
            home_text.setTextColor(getResources().getColor(R.color.black));
            cart_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            transaction_text.setTextColor(getResources().getColor(R.color.black));
            order_text.setTextColor(getResources().getColor(R.color.black));
            ///toolbar vistble
            toolbar.setVisibility(View.VISIBLE);
            titleTxt.setText(R.string.Item_Cart);
            Fragment fragment = ItemCartFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        });

//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.setHomeAsUpIndicator(R.drawable.menu);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        drawer_layout.addDrawerListener(drawerToggle);
        nvDrawer = findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    setupDrawerContent(nvDrawer);
                    drawer.openDrawer(GravityCompat.START);

                }
            }
        });
        try {
            setupNavigationHeader();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
//        try {
//            setupNavigationHeader();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        callProductFragment();
        getCartItems();
    }

    private void getCartItems() {
        class GetTasks extends AsyncTask<Void, Void, List<PosOrder>> {

            @Override
            protected List<PosOrder> doInBackground(Void... voids) {
                List<PosOrder> list = DatabaseClient.getInstance(MainActivity.this).getAppDatabase().productDao().getOrder();
                return list;
            }

            @Override
            protected void onPostExecute(List<PosOrder> tasks) {
                super.onPostExecute(tasks);
                cartBtn.setText(tasks.size() + "");
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("dataToPass");
            String test = intent.getStringExtra("dataToPass");
            String str_variant = intent.getStringExtra("variant");
            int id = intent.getIntExtra("ID", 0);
            int deleteProdId = intent.getIntExtra("deleteProdId", 0);
            List<PosOrder> post_order = (List<PosOrder>) getIntent().getSerializableExtra("cart_list");

            if (test.equalsIgnoreCase("1")) {
                getCartItems();
            } else if (test.equalsIgnoreCase("2")) {
                cartBtn.setText("0");
            } else if (test.equalsIgnoreCase("3")) {
                AsyncTask.execute(() -> {
                    DatabaseClient.getInstance(context).getAppDatabase()
                            .productDao()
                            .deleteItem(id, str_variant);
                });
                getCartItems();
            } else if (test.equalsIgnoreCase("4")) {
                Bundle bundle = new Bundle();
                bundle.putString("payment", "success");
                Fragment fragment = ItemCartFragment.newInstance();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            } else if (test.equalsIgnoreCase("5")) {
                getCartItems();
            }
        }
    }

    private void callProductFragment() {
        titleTxt.setText(R.string.products);
        titleTxt.setVisibility(View.GONE);
        toolbar_image_lay.setVisibility(View.VISIBLE);
        Fragment fragment = ProductListFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit from the app?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        if (menuItem.getItemId() == R.id.home_fragment) {
            ///toolbar vistble
            toolbar.setVisibility(View.VISIBLE);
            toolbar_image_lay.setVisibility(View.VISIBLE);
            titleTxt.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            // titleTxt.setText("Products");
            fragment = ProductListFragment.newInstance();
        } else if (menuItem.getItemId() == R.id.cst_fragment) {
            toolbar.setVisibility(View.GONE);

            fragment = AddCustomerFragment.newInstance();
        } else if (menuItem.getItemId() == R.id.Viewcst_fragment) {
            ///toolbar gone
            toolbar.setVisibility(View.GONE);
            toolbar_image_lay.setVisibility(View.GONE);
            titleTxt.setVisibility(View.VISIBLE);
            titleTxt.setText("View Customer");
            fragment = CustomerRecyclerViewFragment.newInstance();
        } else if (menuItem.getItemId() == R.id.ViewInventory_fragment) {
            ///toolbar vistble
            toolbar.setVisibility(View.VISIBLE);
            toolbar_image_lay.setVisibility(View.GONE);
            titleTxt.setVisibility(View.VISIBLE);
            titleTxt.setText(R.string.view_inventory);
            fragment = StoreInventoryFragment.newInstance();
        } else if (menuItem.getItemId() == R.id.about_fragment) {
            ///toolbar vistble
            toolbar.setVisibility(View.GONE);

            fragment = AboutFragment.newInstance();
        } else if (menuItem.getItemId() == R.id.contact_fragment) {
            toolbar.setVisibility(View.GONE);
            ///toolbar vistble

//            toolbar.setBackgroundResource(R.color.colorPrimary);
//            toolbar_image_lay.setVisibility(View.GONE);
            fragment = ContactUsFragment.newInstance();

        }  else if (menuItem.getItemId() == R.id.add_product) {
            toolbar.setVisibility(View.GONE);
            fragment = AddProductFragment.newInstance();

        }else if (menuItem.getItemId() == R.id.create_attribute) {
            toolbar.setVisibility(View.GONE);
            fragment = AttributeListFragment.newInstance();

        }
        else if (menuItem.getItemId() == R.id.add_pos_category) {
            toolbar.setVisibility(View.GONE);
            fragment = POSCategoryListFragment.newInstance();

        }
        else if (menuItem.getItemId() == R.id.logout_id) {
            logoutAlert();
//            fragment = ProductListFragment.newInstance();
        }

        // Insert the fragment by replacing any existing fragment
        if(fragment!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        drawer_layout.closeDrawers();
    }

    private void setupNavigationHeader() throws JSONException {
        View headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        TextView ivHeaderPhoto = headerLayout.findViewById(R.id.avatorIamge);
        TextView name = headerLayout.findViewById(R.id.nameTxtHeaderView);

        View view_inventory = nvDrawer.inflateHeaderView(R.layout.inventory);
        inventory_menu = view_inventory.findViewById(R.id.inventory_menu_lay);
        inventory_text = view_inventory.findViewById(R.id.inventory_text);
        warehouses = view_inventory.findViewById(R.id.warehouses);

        View view_sale = nvDrawer.inflateHeaderView(R.layout.menu_button);
        sale_menu_lay = view_sale.findViewById(R.id.sale_menu_lay);
        vendor = view_sale.findViewById(R.id.vendor);
        purchase = view_sale.findViewById(R.id.purchase);

        name.setText(sm.getUserName());
        headerLayout.setOnClickListener(view -> {
            Intent profileIntent = new Intent(MainActivity.this, MyProfileActivity.class);
            startActivity(profileIntent);
        });
        inventory_menu.setOnClickListener(view -> {
            if (!isClicked) {
                inventory_text.setVisibility(View.VISIBLE);
                warehouses.setVisibility(View.VISIBLE);
                isClicked = true;
            }else {
                inventory_text.setVisibility(View.GONE);
                warehouses.setVisibility(View.GONE);
                isClicked = false;
            }

        });

        sale_menu_lay.setOnClickListener(view -> {
            if (!isClicked) {
                purchase.setVisibility(View.VISIBLE);
                vendor.setVisibility(View.VISIBLE);
                isClicked = true;
            }else {
                purchase.setVisibility(View.GONE);
                vendor.setVisibility(View.GONE);
                isClicked = false;
            }

        });

        vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.GONE);
                Fragment fragment = VendorListFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                drawer_layout.close();
            }
        });

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.GONE);
                Fragment fragment = CreatePurchaseOrderFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                drawer_layout.close();
            }
        });

        inventory_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.GONE);
                Fragment fragment = StoreInventoryFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                drawer_layout.close();
            }
        });
        warehouses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility(View.GONE);
                Fragment fragment = CreateWarehouseFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                drawer_layout.close();
            }
        });

    }

    private void logoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to logout from the app?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            sm.logoutUser();
            if (sm.getSessionID() != null && !sm.getSessionID().equalsIgnoreCase("")) {
                closeSession(sm.getSessionID());
            } else {
                sm.logoutUser();
                Intent profileIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(profileIntent);
                finish();
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vendor.setVisibility(View.GONE);
    }
    private void closeSession(String session) {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait while a moment...");
        progressDialog.setTitle("Session Close");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("session_id", session);
        new VolleyRequestHandler(MainActivity.this, params).createRequest(ApiConstants.LOGOUT_REQUEST, new VolleyCallback() {

            @Override
            public void onSuccess(Object result) throws JSONException {
                Log.v("Response", result.toString());
                JSONObject obj = new JSONObject(result.toString());
                int statusCode = obj.optInt("statuscode");
                String status = obj.optString("status");
                if (statusCode == 200 && status.equalsIgnoreCase("success")) {
                    progressDialog.cancel();
                    sm.logoutUser();
                    Intent profileIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(profileIntent);
                    finish();
                }
            }

            @Override
            public void onError(String result) {
                Log.v("Response", result.toString());
                progressDialog.cancel();
                sm.logoutUser();
                Intent profileIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(profileIntent);
                finish();
            }
        });
    }

}