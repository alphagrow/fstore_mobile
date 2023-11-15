package com.growit.posapp.gstore.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.growit.posapp.gstore.tables.Customer;

import java.util.List;

@Dao
public interface CustomerDao {

    @Query("SELECT * FROM Customer")
    List<Customer> getAll();

    @Query("SELECT * FROM Customer where customer_type= :type")
    List<Customer> getAllCustomerByType(int type);

    @Query("SELECT * FROM Customer WHERE name LIKE :search_query || '%' OR mobile LIKE :search_query || '%'")
    List<Customer> searchCustomer(String search_query);

    @Insert
    void insert(Customer task);

    @Insert
    void insertAllCustomers(List<Customer> task);

    @Query("DELETE FROM Customer")
    void delete();

    @Update
    void update(Customer task);

    @Query("SELECT * FROM Customer WHERE mobile = :mobileNo")
    boolean isDataExist(String mobileNo);

    @Query("SELECT discounts FROM Customer where customer_type= :type")
    int getCustomerLineDiscountByType(int type);
}
