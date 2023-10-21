package lk.ijse.dep11.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.pos.db.CustomerDataAccess;
import lk.ijse.dep11.pos.db.ItemDataAccess;
import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;
import lk.ijse.dep11.pos.tm.OrderItem;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlaceOrderFormController {
    public AnchorPane root;
    public JFXTextField txtCustomerName;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSave;
    public TableView<OrderItem> tblOrderDetails;
    public JFXTextField txtUnitPrice;
    public JFXComboBox<Customer> cmbCustomerId;
    public JFXComboBox<Item> cmbItemCode;
    public JFXTextField txtQty;
    public Label lblId;
    public Label lblDate;
    public Label lblTotal;
    public JFXButton btnPlaceOrder;

    public void initialize(){
        btnPlaceOrder.setDisable(true);
        btnSave.setDisable(true);
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        try {
            cmbCustomerId.getItems().clear();
            cmbCustomerId.getItems().addAll(CustomerDataAccess.getAllCustomers());
            cmbItemCode.getItems().clear();
            cmbItemCode.getItems().addAll(ItemDataAccess.getAllItems());

            txtCustomerName.setEditable(false);
            txtDescription.setEditable(false);
            txtQtyOnHand.setEditable(false);
            txtQty.setEditable(false);

            cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((ov,prev,cur)->{
                if(cur!=null){
                    txtCustomerName.setText(cur.getName());
                }
            });

            cmbItemCode.getSelectionModel().selectedItemProperty().addListener((ov,prev,cur)->{
                if(cur!=null){
                    txtDescription.setText(cur.getDescription());
                    txtQtyOnHand.setText(String.valueOf(cur.getQty()));
                    txtUnitPrice.setText(String.valueOf(cur.getUnit_price()));

                }
            });




        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void newOrder(){
        for (JFXTextField txt: new JFXTextField[]{txtCustomerName,txtQty,txtDescription,txtQtyOnHand,txtUnitPrice}) {
            txt.clear();
            txt.setDisable(true);
        }

    }

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
    }

    public void txtQty_OnAction(ActionEvent actionEvent) {
    }

    public void btnPlaceOrder_OnAction(ActionEvent actionEvent) {
    }
}
