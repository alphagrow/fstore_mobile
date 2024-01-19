package com.growit.posapp.fstore.utils;

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
 //   public final static String BASE_URL = "http://162.246.254.203:9090";
  //  public final static String BASE_URL = "http://192.168.18.36:9090";
// public final static String BASE_URL = "http://162.246.254.203:9090";
  //dev_fstore
  public final static String BASE_URL = "http://162.246.254.203:8000";
    public final static String BASE_URL_SMS = "http://msg.jmdinfotek.in/";
    public final static String SIGN_IN = "/api/login";
    public final static String INVOICE_DOWNLOAD = "/pdf/download?pos_order_id=";
    public final static String ADD_CUSTOMER = "/api/customer?";
    public final static String POS_ORDER = "/api/pos/order?";
    public final static String ADD_EXTRAPRICE = "/api/create_extra_pricelist?";
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
    public final static String GET_EXTRA_PRICE = "/api/product/variant/list?";
    public final static String GET_TRANSACTION_HISTORY = "/pos/payment/transactions?";
    public final static String GET_SMS = "api/mt/SendSMS?";
    public final static String POST_ORDER_DISCOUNT = "/api/get/order-discount?";
    public final static String LOGOUT_REQUEST = "api/close_pos_session?";//session_id
    public final static String POST_ADD_PRODUCT = "/api/create_product?";
    public final static String DELETE_PRODUCT = "/api/delete_product?";
    public final static String PUT_UPDATE_PRODUCT = "/api/update_product?";
    public final static String POST_CREATE_POS_CATEGORY = "/api/create_pos_category?";
    public final static String DELETE_POS_CATEGORY = "/api/delete_pos_category?";
    public final static String PUT_UPDATE_POS_CATEGORY = "/api/update_pos_category?";
    public final static String POST_CREATE_ATTRIBUTE_VALUE = "/api/create_attribute_and_values?";
    public final static String GET_ATTRIBUTES_LIST = "/api/list_attributes_and_values?";
    public final static String DELETE_ATTRIBUTE_VALUES = "/api/delete_attribute_and_values?";
    public final static String PUT_UPDATE_ATTRIBUTE = "/api/update_attribute_and_values?";
    public final static String GET_VENDOR_LIST = "/api/list_vendors?";
    public final static String ADD_VENDOR = "/api/create_vendor?";
    public final static String DELETE_ARCHIVE_VENDOR = "/api/vendor/state?";

    public final static String UPDATE_Vendor = "/api/update_vendor/?";
    public final static String POST_CREATE_PURCHASE_ORDER = "/api/create_purchase_order?";
    public final static String POST_PURCHASE_ORDER_LIST = "/api/purchase_order_list?";
    public final static String POST_RECEIVE_PRODUCTS = "/api/receive_products?";
    public final static String POST_CANCEL_PURCHASE_ORDER = "/api/cancel_purchase_order?";
    public final static String GET_PURCHASE_ORDER_DOWNLOAD = "/api/purchase_order/download?";
    public final static String CREATE_WAREHOUSE = "/api/create_warehouse?";
    public final static String GET_COMPANIES = "/api/get_companies?";
    public final static String ADD_COMPANY = "/api/user/company?";
    public final static String GET_WareHouses = "/api/get_warehouses?";
    public final static String UPDATE_WAREHOUSE = "/api/update_warehouse?";
    public final static String GET_ALL_PRODUCT_LIST = "/api/products/list?";
    public final static String GET_STOCK_Detail = "/api/stock_quant/details?";
    public final static String UPDATE_COMPANY = "/api/user/company/update?";
    public final static String GET_STOCK_QUANT = "/api/products/list?";
    public final static String GET_OPERATION_LIST = "/api/operation_types?";
    public final static String GET_LOCATION_LIST = "/api/location/list?";
    public final static String GET_TRANSFER_LIST = "/api/transfer/list?";
    public final static String GET_OPERATION_TYPE_LIST = "/api/operation_types?";
   public final static String POST_TRANSFER_ORDER = "/api/stock_transfer?";
  public final static String GET_DELIVERY_DOWNLOAD = "/api/transfer/download_delivery_slip?";
  public final static String GET_CUSTOMER_DISCOUNT_LIST = "/api/customer/discount/list?";
  public final static String POST_CREATE_DISCOUNT = "/api/customer/discount?";
  public final static String PUT_DISCOUNT_UPDATE = "/api/customer/discount/update?";
  public final static String GET_LIST_SHOPS = "/api/list/shops?";
  public final static String POST_CREATE_SHOPS = "/api/create/shop?";
  public final static String PUT_UPDATE_SHOPS = "/api/update/shop?";
    public final static String GET_USER_PROFILE = "/api/user_profile?";

  public final static String PUT_UPDATE_UOM = "/api/update/uom?";
  public final static String POST_CREATE_UOM = "/api/create/uom?";
  public final static String GET_UOM_LIST = "/api/list/uoms?";
  public final static String POST_CREATE_DISTRICT= "/api/create_district?";
  public final static String POST_CREATE_TALUKA= "/api/create_taluka?";
}


