package com.growit.posapp.fstore.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.growit.posapp.fstore.tables.PurchaseOrder;

import java.util.List;

@Dao

public interface PurchaseDao {
        @Query("SELECT * FROM PurchaseOrder")
        List<PurchaseOrder> getPurchaseOrder();

        @Insert
        void insert(PurchaseOrder task);

        @Query("SELECT COUNT(productName) FROM PurchaseOrder")
        int getRowCount();

        @Query("DELETE FROM PurchaseOrder")
        void delete();

        @Query("DELETE FROM PurchaseOrder where productID= :id AND productVariants=:variants")
        void deleteItem(int id,String variants);
        @Query("UPDATE PurchaseOrder SET quantity = :qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
        int updateProductCardQuantity(int qty,int id,String variants,double price);

//        @Query("UPDATE PurchaseOrder SET quantity = quantity+:qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
//        int updateProductQuantity(int qty,int id,String variants,double price);
        @Query("UPDATE PurchaseOrder SET quantity = quantity+:qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
        int updateProductQuantity(int qty,int id,String variants,double price);
//
//        @Query("SELECT SUM(quantity) FROM PurchaseOrder")
//        int getTotalQuantity();

        @Query("SELECT COUNT(*) FROM PurchaseOrder where productID= :id AND productVariants=:variants AND unitPrice=:price")

        int getProductDetailById(int id,String variants,double price);




        //   ************************

//        @Query("UPDATE PurchaseOrder SET quantity = :qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
//        int updateProductCardQuantity(int qty,int id,String variants,double price);
//
//        @Query("UPDATE PurchaseOrder SET quantity = quantity+:qty WHERE productID = :id AND productVariants=:variants AND unitPrice=:price")
//        int updateProductQuantity(int qty,int id,String variants,double price);


}
