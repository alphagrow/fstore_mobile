package com.growit.posapp.fstore.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.growit.posapp.fstore.model.VendorModelList;
import com.growit.posapp.fstore.tables.Customer;

import java.util.List;

@Dao
public interface VendorDao {
//    @Query("SELECT * FROM VendorModelList")
//    List<VendorModelList> getAll();



    @Insert
    void insert(VendorModelList task);
    @Insert
    void insertAllVendor(List<VendorModelList> task);


//    @Query("DELETE FROM VendorModelList")
//    void delete();

    @Update
    void update(VendorModelList task);
}
