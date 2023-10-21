package lk.ijse.dep11.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.pos.db.CustomerDataAccess;
import lk.ijse.dep11.pos.db.ItemDataAccess;
import lk.ijse.dep11.pos.db.OrderDataAccess;
import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;

public class ManageItemFormController {
    public AnchorPane root;
    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView<Item> tblItems;
    public JFXTextField txtUnitPrice;
    public JFXButton btnNewItem
            ;

    public void initialize(){
        String[] columns = {"code","description","qty","unit_price"};
        for (int i = 0; i < columns.length; i++) {
            tblItems.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(columns[i]));
        }
        btnDelete.setDisable(true);
        btnSave.setDefaultButton(true);
        btnNewItem.fire();
        try {
            tblItems.getItems().addAll(ItemDataAccess.getAllItems());
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load items, try later!").show();
        }
        tblItems.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur) ->{
            if (cur != null){
                btnSave.setText("UPDATE");
                btnDelete.setDisable(false);
                txtCode.setText(cur.getCode());
                txtDescription.setText(cur.getDescription());
                txtQtyOnHand.setText(String.valueOf(cur.getQty()));
                txtUnitPrice.setText(cur.getUnit_price().toString());
            }else{
                btnSave.setText("Save");
                btnDelete.setDisable(true);
            }
        });
        Platform.runLater(txtCode::requestFocus);
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

    public void btnAddNew_OnAction(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtUnitPrice, txtDescription, txtQtyOnHand,txtUnitPrice})
            textField.clear();
        tblItems.getSelectionModel().clearSelection();
        txtCode.requestFocus();

    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        if (!isItemValid()) return;
        String code = txtCode.getText().strip();
        String description = txtDescription.getText().strip();
        int qty = Integer.parseInt(txtQtyOnHand.getText().strip());
        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText());

       Item item= new Item( code,description,qty,unitPrice);
       try {
            if (btnSave.getText().equals("Save")){
                ItemDataAccess.saveItem(item);
                tblItems.getItems().add(item);
            }else{
                ItemDataAccess.updateItem(item);
                ObservableList<Item> itemList = tblItems.getItems();
                Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
                itemList.set(itemList.indexOf(selectedItem),item);
                tblItems.refresh();
            }
            btnNewItem.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the Item, try again").show();
        }
    }
    private boolean isItemValid() {
        String code = txtCode.getText().strip();
        String description = txtDescription.getText().strip();
        String qty = txtQtyOnHand.getText().strip();
        String unitPrice = txtUnitPrice.getText().strip();

        if (!code.matches("\\d{4,}")) {
            txtCode.requestFocus();
            txtCode.selectAll();
            return false;
        } else if (!description.matches("[a-zA-Z0-9]{1,}")) {
            txtDescription.requestFocus();
            txtDescription.selectAll();
            return false;
        }else if (!qty.matches("\\d+") || Integer.parseInt(qty)<=0) {
            txtQtyOnHand.requestFocus();
            txtQtyOnHand.selectAll();
            return false;
        }else if (!(Double.parseDouble(unitPrice)>0)) {
            txtUnitPrice.requestFocus();
            txtUnitPrice.selectAll();
            return false;
        }
        return true;
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
        try {
            if(OrderDataAccess.existOrderByItemCode(selectedItem.getCode())){
                new Alert(Alert.AlertType.ERROR,
                        "Unable to delete this customer, already associated with an order").show();
            }else{
                ItemDataAccess.deleteItem(selectedItem.getCode());
                tblItems.getItems().remove(selectedItem);
                if (tblItems.getItems().isEmpty()) btnNewItem.fire();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
