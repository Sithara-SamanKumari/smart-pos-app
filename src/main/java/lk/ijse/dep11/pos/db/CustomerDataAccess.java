package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDataAccess {
    private static final PreparedStatement STM_UPDATE_CUSTOMER;
    private static final PreparedStatement STM_INSERT_CUSTOMER;
    private static final PreparedStatement STM_DELETE_CUSTOMER;
    private static final PreparedStatement STM_GET_ALL_CUSTOMERS;

    static {
        try {
            Connection connection = SingleDatabaseConnection.getInstance().getConnection();

            STM_GET_ALL_CUSTOMERS = connection.prepareStatement("SELECT * FROM customer");
            STM_UPDATE_CUSTOMER = connection.prepareStatement("UPDATE customer SET name=? ,address=? WHERE id=?");
            STM_INSERT_CUSTOMER = connection.prepareStatement("INSERT INTO customer(id, name, address) VALUES (?,?,?)");
            STM_DELETE_CUSTOMER = connection.prepareStatement("DELETE FROM customer WHERE id=?");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCustomer(Customer customer){
        try {
            STM_INSERT_CUSTOMER.setString(1,customer.getId());
            STM_INSERT_CUSTOMER.setString(2,customer.getName());
            STM_INSERT_CUSTOMER.setString(3,customer.getAddress());
            STM_INSERT_CUSTOMER.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteCustomer(String id){
        try {
            STM_DELETE_CUSTOMER.setString(1,id);
            STM_DELETE_CUSTOMER.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomer(Customer customer){
        try {
            STM_UPDATE_CUSTOMER.setString(1,customer.getName());
            STM_UPDATE_CUSTOMER.setString(2,customer.getAddress());
            STM_UPDATE_CUSTOMER.setString(3,customer.getId());
            STM_UPDATE_CUSTOMER.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Customer> getAllCustomers(){
        ArrayList<Customer> customerList = new ArrayList<>();
                try {
            ResultSet rst = STM_GET_ALL_CUSTOMERS.executeQuery();
            while (rst.next()){
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");

                Customer customer = new Customer(id, name, address);
                customerList.add(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customerList;
    }
}
