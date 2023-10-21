package lk.ijse.dep11.pos.db;

import com.jfoenix.controls.JFXButton;
import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;
import lk.ijse.dep11.pos.tm.OrderItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDataAccessTest {
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
    void existOrderByCustomerId() throws SQLException {
       assertDoesNotThrow(()-> assertFalse(OrderDataAccess.existOrderByCustomerId("ABC")));
        CustomerDataAccess.saveCustomer(new Customer("ABC", "Crazy", "Panadura"));
        SingleDatabaseConnection.getInstance().getConnection().createStatement()
                .executeUpdate("INSERT INTO \"order\" (id, customer_id) VALUES ('111111', 'ABC')");
        assertDoesNotThrow(()-> assertTrue(OrderDataAccess.existOrderByCustomerId("ABC")));

    }

    @Test
    void existOrderByItemCode() throws SQLException {
        assertDoesNotThrow(()->assertFalse(OrderDataAccess.existOrderByItemCode("Crazy Item Code")));

        ItemDataAccess.saveItem(new Item("II12345678", "Crazy Item", 5, new BigDecimal("1250")));
        CustomerDataAccess.saveCustomer(new Customer("ABC", "Crazy", "Panadura"));
        SingleDatabaseConnection.getInstance().getConnection().createStatement()
                .executeUpdate("INSERT INTO \"order\" (id, customer_id) VALUES ('111111', 'ABC')");
        SingleDatabaseConnection.getInstance().getConnection().createStatement()
                .executeUpdate("INSERT INTO order_item (order_id, item_code, qty, unit_price) VALUES ('111111', 'II12345678', 2, 1250.00)");
        assertTrue(OrderDataAccess.existOrderByItemCode("II12345678"));


    }



}