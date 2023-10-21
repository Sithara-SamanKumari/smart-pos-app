package lk.ijse.dep11.pos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDataAccess {
    private static final PreparedStatement STM_EXISTS_BY_CUSTOMER_ID;
    private static final PreparedStatement STM_EXISTS_BY_ITEM_CODE;
    static {
        Connection connection = SingleDatabaseConnection.getInstance().getConnection();
        try {
            STM_EXISTS_BY_CUSTOMER_ID = connection.prepareStatement("SELECT * FROM \"order\" WHERE customer_id=?");
            STM_EXISTS_BY_ITEM_CODE = connection.prepareStatement("SELECT * FROM order_item WHERE item_code=?");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existOrderByCustomerId(String customer_id) throws SQLException {
        STM_EXISTS_BY_CUSTOMER_ID.setString(1,customer_id);
        ResultSet rst = STM_EXISTS_BY_CUSTOMER_ID.executeQuery();
        return (rst.next());
    }
    public static boolean existOrderByItemCode(String item_code) throws SQLException {
        STM_EXISTS_BY_ITEM_CODE.setString(1,item_code);
        ResultSet rst = STM_EXISTS_BY_ITEM_CODE.executeQuery();
        return (rst.next());
    }

}
