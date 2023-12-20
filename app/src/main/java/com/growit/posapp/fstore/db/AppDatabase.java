package com.growit.posapp.fstore.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.growit.posapp.fstore.interfaces.CustomerDao;
import com.growit.posapp.fstore.interfaces.FSTDao;
import com.growit.posapp.fstore.interfaces.ProductDao;
import com.growit.posapp.fstore.interfaces.PurchaseDao;
import com.growit.posapp.fstore.interfaces.VendorDao;
import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.tables.Customer;
import com.growit.posapp.fstore.tables.GST;
import com.growit.posapp.fstore.tables.PosOrder;
import com.growit.posapp.fstore.tables.PurchaseOrder;

@Database(entities = {Customer.class, PosOrder.class, PurchaseOrder.class, GST.class, VendorModelList.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();

    public abstract ProductDao productDao();
    public abstract PurchaseDao purchaseDao();
    public abstract FSTDao gstDao();
    public abstract VendorDao getVendorDao();

}