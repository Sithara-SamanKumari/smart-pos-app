package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ItemDataAccessTest {
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
    void getAllItems() throws SQLException {
        Item item1 = new Item("1234", "Pen", 200, new BigDecimal("25.45"));
        Item item2 = new Item("3456", "pencil", 100, new BigDecimal("25"));
        ItemDataAccess.saveItem(item1);
        ItemDataAccess.saveItem(item2);
        assertDoesNotThrow(()->{
           ItemDataAccess.getAllItems();
           assertTrue(ItemDataAccess.getAllItems().size()>=2);
        });

    }

    @Test
    void saveItem() {
        Item item1 = new Item("1234", "Pen", 200, new BigDecimal("25.45"));
        Item item2 = new Item("3456", "pencil", 100, new BigDecimal("25"));
        Item item3 = new Item("3456", "pencil", 100, new BigDecimal("25"));

        assertDoesNotThrow(()->{
            ItemDataAccess.saveItem(item1);
            ItemDataAccess.saveItem(item2);
        });
        assertThrows(Exception.class,()->{
            ItemDataAccess.saveItem(item3);
            ItemDataAccess.saveItem(item2);
        });

    }

    @Test
    void updateItem() throws SQLException {
        Item item1 = new Item("1234", "Pen", 200, new BigDecimal("25.45"));
        ItemDataAccess.saveItem(item1);
        assertDoesNotThrow(()->{
            ItemDataAccess.updateItem(new Item("1234", "Pen", 300, new BigDecimal("30.45")));
        });
    }

    @Test
    void deleteItem() throws SQLException {
        Item item1 = new Item("1234", "Pen", 200, new BigDecimal("25.45"));
        Item item2 = new Item("3456", "pencil", 100, new BigDecimal("25"));

        ItemDataAccess.saveItem(item1);
        ItemDataAccess.saveItem(item2);

        int size = ItemDataAccess.getAllItems().size();
        assertDoesNotThrow(()->{
            ItemDataAccess.deleteItem("1234");
        });
        assertTrue(size-1== ItemDataAccess.getAllItems().size());


    }
}