package lk.ijse.dep11.pos.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.pos.tm.Order;

import java.io.IOException;
import java.net.URL;

public class SearchOrdersFormController {
    public TextField txtSearch;
    public TableView<Order> tblOrders;
    public AnchorPane root;

    public void initialize(){
        String[] cols = {"orderId","orderDate","customerId","customerName","total"};
        for (int i = 0; i <cols.length ; i++) {
            tblOrders.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(cols[i]));

        }
    }

    public void tblOrders_OnMouseClicked(MouseEvent mouseEvent) {
    }

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        MainFormController.navigateToMain(root);
    }
}
