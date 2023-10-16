package lk.ijse.dep11.pos.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SingleDatabaseConnectionTest {
    @BeforeEach
    void setUp() throws SQLException {
        SingleDatabaseConnection.getInstance().getConnection().setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        SingleDatabaseConnection.getInstance().getConnection().rollback();
        SingleDatabaseConnection.getInstance().getConnection().setAutoCommit(true);
    }

    @org.junit.jupiter.api.Test
    void getConnection() {
        Connection connection1 = SingleDatabaseConnection.getInstance().getConnection();
        Connection connection2 = SingleDatabaseConnection.getInstance().getConnection();
        Connection connection3 = SingleDatabaseConnection.getInstance().getConnection();
        assertEquals(connection2,connection1);
        assertEquals(connection1,connection3);
    }

    @Test
    void generateSchema() throws SQLException {
        Connection connection = SingleDatabaseConnection.getInstance().getConnection();
        assertDoesNotThrow(()->{
            connection.createStatement().executeUpdate(
                    "INSERT INTO customer(id,name,address) VALUES ('1','Sandun','Pandura')");
        });





    }
}