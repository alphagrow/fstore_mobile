package com.growit.posapp.fstore.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.growit.posapp.fstore.tables.PosOrder;
import com.growit.posapp.fstore.tables.PurchaseOrder;
import com.growit.posapp.fstore.tables.TransferOrder;

import java.util.List;
@Dao

public interface TransferDao {
    @Query("SELECT * FROM TransferOrder")
    List<TransferOrder> getPurchaseOrder();

    @Insert
    void insert(TransferOrder task);

    @Query("SELECT COUNT(productName) FROM TransferOrder")
    int getRowCount();

    @Query("DELETE FROM TransferOrder")
    void delete();

    @Query("DELETE FROM TransferOrder where productID= :id AND productVariants=:variants")
    void deleteItem(int id,String variants);
    @Query("UPDATE TransferOrder SET quantity = :qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
    int updateProductCardQuantity(int qty,int id,String variants,double price);

    //        @Query("UPDATE TransferOrder SET quantity = quantity+:qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
//        int updateProductQuantity(int qty,int id,String variants,double price);
    @Query("UPDATE TransferOrder SET quantity = quantity+:qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
    int updateProductQuantity(int qty,int id,String variants,double price);
//
//        @Query("SELECT SUM(quantity) FROM TransferOrder")
//        int getTotalQuantity();

    @Query("SELECT COUNT(*) FROM TransferOrder where productID= :id AND productVariants=:variants AND unitPrice=:price")

    int getProductDetailById(int id,String variants,double price);




    //   ************************

//        @Query("UPDATE TransferOrder SET quantity = :qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
//        int updateProductCardQuantity(int qty,int id,String variants,double price);
//
//        @Query("UPDATE TransferOrder SET quantity = quantity+:qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
//        int updateProductQuantity(int qty,int id,String variants,double price);


}
