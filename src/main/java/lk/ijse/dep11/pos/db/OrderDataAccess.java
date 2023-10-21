package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDataAccess {
    private static final PreparedStatement STM_EXISTS_BY_CUSTOMER_ID;
    private static final PreparedStatement STM_EXISTS_BY_ITEM_CODE;
    private static final PreparedStatement STM_GET_LAST_ORDER_ID;

//    private static final PreparedStatement STM_INSERT_ORDER_ITEM;

    static {
        Connection connection = SingleDatabaseConnection.getInstance().getConnection();
        try {
            STM_EXISTS_BY_CUSTOMER_ID = connection.prepareStatement("SELECT * FROM \"order\" WHERE customer_id=?");
            STM_EXISTS_BY_ITEM_CODE = connection.prepareStatement("SELECT * FROM order_item WHERE item_code=?");
            STM_GET_LAST_ORDER_ID = connection.prepareStatement("SELECT id FROM \"order\" ORDER BY id DESC FETCH FIRST ROWS ONLY ");
//            STM_INSERT_ORDER_ITEM = connection.prepareStatement("INSERT INTO order_item(order_id, item_code, qty, unit_price) VALUES (?,?,?,?)");
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
    public static String getLastOrderId() throws SQLException {
        ResultSet rst = STM_GET_LAST_ORDER_ID.executeQuery();
        if(rst.next()){
            return rst.getString("id");
        }
        return null;
    }
//    public static void saveOrder(OrderItem oi,String orderId) throws SQLException {
//        STM_INSERT_ORDER_ITEM.setString(1,orderId);
//        STM_INSERT_ORDER_ITEM.setString(2,oi.getCode());
//        STM_INSERT_ORDER_ITEM.setInt(3,oi.getQty());
//        STM_INSERT_ORDER_ITEM.setBigDecimal(4,oi.getUnitPrice());
//    }

}
