package com.growit.posapp.gstore.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.growit.posapp.gstore.tables.GST;

import java.util.List;

@Dao
public interface GSTDao {

    @Insert
    void insertGST(List<GST> gst);

    @Query("DELETE FROM GST")
    void delete();

    @Query("SELECT gstValue FROM GST where gstId= :id")
    int getGSTValueById(int id);

    @Query("SELECT SUM(gstValue) FROM GST")
    int getGSTValue();
}

