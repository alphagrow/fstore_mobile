package com.growit.posapp.gstore.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.growit.posapp.gstore.interfaces.CustomerDao;
import com.growit.posapp.gstore.interfaces.GSTDao;
import com.growit.posapp.gstore.interfaces.ProductDao;
import com.growit.posapp.gstore.tables.Customer;
import com.growit.posapp.gstore.tables.GST;
import com.growit.posapp.gstore.tables.PosOrder;

@Database(entities = {Customer.class, PosOrder.class, GST.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();

    public abstract ProductDao productDao();

    public abstract GSTDao gstDao();

}