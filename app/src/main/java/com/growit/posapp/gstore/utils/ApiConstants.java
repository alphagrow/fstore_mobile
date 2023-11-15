package com.growit.posapp.gstore.utils;

/**
 * Created by Ravi on 29/11/2022.
 * This class is used for all Web services related constants
 */
public class ApiConstants {
    // Local Server - 192.168.18.29:8000
    // Production server dev- 162.246.254.203:8081
    // Production server qc- 162.246.254.203:8069
//HFpY4zcsWKqmAg
    public final static String RazorPayTestKey="rzp_test_rAuo7Wl2Inp0LP";
    public final static String RazorPayProductionKey="rzp_live_5JiuYm4kh7D4fT";
    public final static String BASE_URL = "http://162.246.254.203:8069";
    //public final static String BASE_URL = "http://192.168.18.36:9090";
    public final static String BASE_URL_SMS = "http://msg.jmdinfotek.in/";
    public final static String SIGN_IN = "/api/login";
    public final static String INVOICE_DOWNLOAD = "/pdf/download?pos_order_id=";
    public final static String ADD_CUSTOMER = "/api/customer?";
    public final static String POS_ORDER = "/api/pos/order?";
    public final static String GET_STATES = "/api/get_states?";
    public final static String GET_DISTRICT = "/api/get_district?";
    public final static String GET_TALUKA = "/api/get_taluka?";
    public final static String UPDATE_CUSTOMER = "/api/customer/update/";

    public final static String GET_GIFT_CARDS = "/api/gift_cards?";
    public final static String GET_ALL_CUSTOMER = "/api/customer/list?";
    public final static String GST_API = "/api/get_gst_tax_list?";
    public final static String UPLOAD_PHOTO = "/api/user/photo/upload?";
    public final static String GET_ALL_CROPS = "/api/pos_categories/list?";
    public final static String GET_PRODUCT_DETAIL = "/api/product_details?";
    public final static String GET_PRODUCT_LIST = "/api/pos_category/products/list?";
    public final static String COUNTRY_ID = "104";//This country id has been fixed it can't be changed.
    public static final String ACTION = "com.pos.ACTION_ADDITEM";
    public static final String ACTION_PAYMENT = "com.pos.ACTION_PAYMENT";
    public final static String GET_ORDERS_HISTORY = "/api/pos/order/list?";
    public final static String GET_TRANSACTION_HISTORY = "/pos/payment/transactions?";
    public final static String GET_SMS = "api/mt/SendSMS?";
    public final static String POST_ORDER_DISCOUNT = "/api/get/order-discount?";
    public final static String LOGOUT_REQUEST = "api/close_pos_session?";//session_id
    public final static String GET_STOCK_QUANT = "/api/stock_quant/details?";
}


