package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Order;
import lk.ijse.dep11.pos.tm.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDataAccess {
    private static final PreparedStatement STM_EXISTS_BY_CUSTOMER_ID;
    private static final PreparedStatement STM_EXISTS_BY_ITEM_CODE;
    private static final PreparedStatement STM_GET_LAST_ORDER_ID;

    private static final PreparedStatement STM_INSERT_ORDER_ITEM;
    private static final PreparedStatement STM_INSERT_ORDER;

    private static final PreparedStatement STM_UPDATE_ITEM_TABLE;

    private static final PreparedStatement STM_FIND_ORDER;

    static {
        Connection connection = SingleDatabaseConnection.getInstance().getConnection();
        try {
            STM_EXISTS_BY_CUSTOMER_ID = connection.prepareStatement("SELECT * FROM \"order\" WHERE customer_id=?");
            STM_EXISTS_BY_ITEM_CODE = connection.prepareStatement("SELECT * FROM order_item WHERE item_code=?");
            STM_GET_LAST_ORDER_ID = connection.prepareStatement("SELECT id FROM \"order\" ORDER BY id DESC FETCH FIRST ROWS ONLY ");
            STM_INSERT_ORDER_ITEM = connection.prepareStatement("INSERT INTO order_item(order_id, item_code, qty, unit_price) VALUES (?,?,?,?)");
            STM_INSERT_ORDER = connection.prepareStatement("INSERT INTO \"order\"(id,date,customer_id) VALUES (?,?,?)");
            STM_UPDATE_ITEM_TABLE = connection.prepareStatement("UPDATE item SET qty=? WHERE code=?");
            STM_FIND_ORDER  = connection.prepareStatement("SELECT o.*, c.name, CAST(\"order_total\".total AS DECIMAL(8, 2))\n" +
                    "FROM \"order\" AS o\n" +
                    "         INNER JOIN customer AS c ON c.id = o.\"customer_id\"\n" +
                    "         INNER JOIN customer c2 on c2.id = o.customer_id\n" +
                    "         INNER JOIN\n" +
                    "     (SELECT oi.order_id, SUM(oi.qty * \"oi\".\"unit_price\") AS total\n" +
                    "      FROM order_item AS oi\n" +
                    "               INNER JOIN \"order\" AS i ON oi.order_id = i.id\n" +
                    "      GROUP BY oi.order_id) AS order_total ON order_total.order_id = o.id\n" +
                    "WHERE o.id LIKE ?\n" +
                    "   OR CAST(o.date AS VARCHAR(20)) LIKE ?\n" +
                    "   OR o.customer_id LIKE ?\n" +
                    "   OR c.name LIKE ?\n" +
                    "ORDER BY id");
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
    public static void saveOrder(List<OrderItem> oi, String orderId, Date date, String customerId) throws SQLException {
        SingleDatabaseConnection.getInstance().getConnection().setAutoCommit(false);
        try{
            /*Save order items */
            STM_INSERT_ORDER.setString(1, orderId);
            STM_INSERT_ORDER.setDate(2, date);
            STM_INSERT_ORDER.setString(3, customerId);
            STM_INSERT_ORDER.executeUpdate();

            /* 2. Save Order Item List */
            /* 3. Update the Stock of each Order Item */
            for (OrderItem orderItem : oi) {
                STM_INSERT_ORDER_ITEM.setString(1, orderId);
                STM_INSERT_ORDER_ITEM.setString(2, orderItem.getCode());
                STM_INSERT_ORDER_ITEM.setInt(3, orderItem.getQty());
                STM_INSERT_ORDER_ITEM.setBigDecimal(4, orderItem.getUnitPrice());
                STM_INSERT_ORDER_ITEM.executeUpdate();

                STM_UPDATE_ITEM_TABLE.setInt(1, orderItem.getQty());
                STM_UPDATE_ITEM_TABLE.setString(2, orderItem.getCode());
                STM_UPDATE_ITEM_TABLE.executeUpdate();
            }

            SingleDatabaseConnection.getInstance().getConnection().commit();
        }catch (Exception e){
            SingleDatabaseConnection.getInstance().getConnection().rollback();
            e.printStackTrace();
        }finally {
            SingleDatabaseConnection.getInstance().getConnection().setAutoCommit(true);
        }

    }

    public static  ArrayList<Order> findOrder(String keyWord) throws SQLException {
        ArrayList<Order> orderList = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            STM_FIND_ORDER.setString(i, "%"+keyWord+"%");
        }
            ResultSet rst = STM_FIND_ORDER.executeQuery();
            while (rst.next()){
                String orderId = rst.getString("id");
                Date orderDate = rst.getDate("date");
                String customerId = rst.getString("customer_id");
                String customerName = rst.getString("name");
                BigDecimal orderTotal = rst.getBigDecimal("total");
                orderList.add(new Order(orderId,orderDate,customerId,customerName,orderTotal));
            }

        return orderList;
    }

}
