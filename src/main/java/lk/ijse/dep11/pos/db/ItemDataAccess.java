package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDataAccess {

    private static final PreparedStatement STM_INSERT_ITEM;
    private static final PreparedStatement STM_UPDATE_ITEM;
    private static final PreparedStatement STM_DELETE_ITEM;
    private static final PreparedStatement STM_GET_ALL_ITEMS;

    static {
        try {
            Connection connection = SingleDatabaseConnection.getInstance().getConnection();
            STM_GET_ALL_ITEMS = connection.prepareStatement("SELECT * FROM item ORDER BY code");
            STM_INSERT_ITEM = connection
                    .prepareStatement("INSERT INTO item (code, description, qty, unit_price) VALUES (?, ?, ?,?)");
            STM_UPDATE_ITEM = connection
                    .prepareStatement("UPDATE item SET description=?,qty=?, unit_price=? WHERE code=?");
            STM_DELETE_ITEM = connection.prepareStatement("DELETE FROM item WHERE code=?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Item> getAllItems() throws SQLException {
        ResultSet rst = STM_GET_ALL_ITEMS.executeQuery();
        List<Item> itemList = new ArrayList<>();
        while (rst.next()) {
            String code = rst.getString("code");
            String description = rst.getString("description");
            int qty = rst.getInt("qty");
            BigDecimal unitPrice = rst.getBigDecimal("unit_price");
            itemList.add(new Item(code,description,qty,unitPrice));
        }
        return itemList;
    }

    public static void saveItem(Item item) throws SQLException {
        STM_INSERT_ITEM.setString(1, item.getCode());
        STM_INSERT_ITEM.setString(2, item.getDescription());
        STM_INSERT_ITEM.setInt(3, item.getQty());
        STM_INSERT_ITEM.setBigDecimal(4, item.getUnit_price());
        STM_INSERT_ITEM.executeUpdate();
    }

    public static void updateItem(Item item) throws SQLException {
        STM_UPDATE_ITEM.setString(4, item.getCode());
        STM_UPDATE_ITEM.setString(1, item.getDescription());
        STM_UPDATE_ITEM.setInt(2, item.getQty());
        STM_UPDATE_ITEM.setBigDecimal(3, item.getUnit_price());
        STM_UPDATE_ITEM.executeUpdate();
    }

    public static void deleteItem(String itemCode) throws SQLException {
        STM_DELETE_ITEM.setString(1, itemCode);
        STM_DELETE_ITEM.executeUpdate();
    }

}
