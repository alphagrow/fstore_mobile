package com.growit.posapp.fstore.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.growit.posapp.fstore.tables.PosOrder;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM PosOrder")
    List<PosOrder> getOrder();

    @Insert
    void insert(PosOrder task);

    @Query("SELECT COUNT(productName) FROM PosOrder")
    int getRowCount();

    @Query("DELETE FROM PosOrder")
    void delete();

    @Query("DELETE FROM PosOrder where productID= :id AND productVariants=:variants")
    void deleteItem(int id,String variants);
    @Query("UPDATE PosOrder SET quantity = :qty WHERE productID = :id AND productVariants=:variants")
    int updateProductCardQuantity(int qty,int id,String variants);

    @Query("UPDATE PosOrder SET quantity = quantity+:qty WHERE productID = :id AND productVariants=:variants")
    int updateProductQuantity(int qty,int id,String variants);

    @Query("SELECT SUM(quantity) FROM PosOrder")
    int getTotalQuantity();

    @Query("SELECT COUNT(*) FROM PosOrder where productID= :id AND productVariants=:variants")

    int getProductDetailById(int id,String variants);
    @Query("UPDATE PosOrder SET discount_per = :discount WHERE productID = :id AND productVariants=:variants AND quantity=:qty")
    int updateProductCardDiscount(double discount,int id,String variants,int qty);
}
