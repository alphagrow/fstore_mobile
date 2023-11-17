package com.growit.posapp.fstore.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.growit.posapp.fstore.tables.GST;

import java.util.List;

@Dao
public interface FSTDao {

    @Insert
    void insertGST(List<GST> gst);

    @Query("DELETE FROM GST")
    void delete();

    @Query("SELECT gstValue FROM GST where gstId= :id")
    int getGSTValueById(int id);

    @Query("SELECT SUM(gstValue) FROM GST")
    int getGSTValue();
}

