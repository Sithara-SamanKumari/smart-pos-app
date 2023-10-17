package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDataAccessTest {
    @BeforeEach
    void setUp() throws SQLException {
        SingleDatabaseConnection.getInstance().getConnection().setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        SingleDatabaseConnection.getInstance().getConnection().rollback();
        SingleDatabaseConnection.getInstance().getConnection().setAutoCommit(true);
    }

    @Test
    void saveCustomer() {
        Customer customer = new Customer("123","Udara","galle");
        Customer customer1 = new Customer("123","Udari","galle");
        Customer customer2 = new Customer("124","Udari","galle");
        assertDoesNotThrow(()->{
            CustomerDataAccess.saveCustomer(customer);
            CustomerDataAccess.saveCustomer(customer2);
        });
        assertThrows(Exception.class,()-> {
            CustomerDataAccess.saveCustomer(customer1);
        });

    }

    @Test
    void deleteCustomer() {
        Customer customer = new Customer("123","Udara","galle");
        Customer customer2 = new Customer("124","Udara","galle");
        CustomerDataAccess.saveCustomer(customer);
        CustomerDataAccess.saveCustomer(customer2);
        int size = CustomerDataAccess.getAllCustomers().size();
        assertDoesNotThrow(()->{
            CustomerDataAccess.deleteCustomer(customer.getId());
            assertTrue(CustomerDataAccess.getAllCustomers().size()==size-1);

        });

    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer("123","Udara","galle");
        CustomerDataAccess.saveCustomer(customer);
        assertDoesNotThrow(()->{
            customer.setName("Uma");
            customer.setAddress("Badulla");
            CustomerDataAccess.updateCustomer(customer);

        });
    }

    @Test
    void getAllCustomers() {
        Customer customer = new Customer("123","Udara","galle");
        Customer customer1 = new Customer("122","Udari","galle");
        Customer customer2 = new Customer("124","Udari","galle");
        CustomerDataAccess.saveCustomer(customer);
        CustomerDataAccess.saveCustomer(customer1);
        CustomerDataAccess.saveCustomer(customer2);
        assertDoesNotThrow(()->{
            ArrayList<Customer> allCustomers = CustomerDataAccess.getAllCustomers();
            assertTrue(allCustomers.size()>=3);

        });
    }
}