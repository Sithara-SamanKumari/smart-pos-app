package lk.ijse.dep11.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.pos.db.CustomerDataAccess;
import lk.ijse.dep11.pos.db.ItemDataAccess;
import lk.ijse.dep11.pos.db.OrderDataAccess;
import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;
import lk.ijse.dep11.pos.tm.OrderItem;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
         String[] cols = {"code", "description", "qty", "unitPrice", "total", "btnDelete"};
        for (int i = 0; i < cols.length; i++) {
            tblOrderDetails.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(cols[i]));
        }
        btnPlaceOrder.setDisable(true);
        btnSave.setDisable(true);
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        newOrder();
        try {
            cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((ov,prev,cur)->{
                enablePlaceOrderButton();
                if(cur==null){
                    txtCustomerName.setDisable(true);
                    txtCustomerName.clear();
                }
                else{
                    txtCustomerName.setEditable(false);
                    txtCustomerName.setDisable(false);
                    txtCustomerName.setText(cur.getName());
                }
            });

            cmbItemCode.getSelectionModel().selectedItemProperty().addListener((ov,prev,cur)->{
                if(cur!=null){
                    txtDescription.setText(cur.getDescription());
                    txtQtyOnHand.setText(String.valueOf(cur.getQty()));
                    txtUnitPrice.setText(String.valueOf(cur.getUnit_price()));
                    for (JFXTextField txt: new JFXTextField[]{txtQtyOnHand,txtDescription,txtUnitPrice}) {
                        txt.setDisable(false);
                        txt.setEditable(false);
                    }
                    txtQty.setEditable(true);
                    txtQty.setDisable(false);

                }else{
                    for (JFXTextField txt: new JFXTextField[]{txtQtyOnHand,txtDescription,txtUnitPrice,txtQty}) {
                        txt.setDisable(true);
                        txt.clear();
                    }
                }
            });

            txtQty.textProperty().addListener((ov, prevQty, curQty) -> {
                Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
                btnSave.setDisable(!(curQty.matches("\\d+") && Integer.parseInt(curQty) <= selectedItem.getQty()
                        && Integer.parseInt(curQty) > 0));

            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void newOrder(){
        for (JFXTextField txt: new JFXTextField[]{txtCustomerName,txtQty,txtDescription,txtQtyOnHand,txtUnitPrice}) {
            txt.clear();
            txt.setDisable(true);
            txt.setEditable(false);
        }
        tblOrderDetails.getItems().clear();
        cmbItemCode.getSelectionModel().clearSelection();
        cmbCustomerId.getSelectionModel().clearSelection();
        lblTotal.setText("Total : Rs. 0.00");

        try {
            cmbCustomerId.getItems().clear();
            cmbItemCode.getItems().clear();
            cmbItemCode.getItems().addAll(ItemDataAccess.getAllItems());
            cmbCustomerId.getItems().addAll(CustomerDataAccess.getAllCustomers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            setOrderId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        cmbCustomerId.requestFocus();
    }

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        MainFormController.navigateToMain(root);
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        Customer selectedCustomer = cmbCustomerId.getSelectionModel().getSelectedItem();
        Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();

        Optional<OrderItem> orderItem1 = tblOrderDetails.getItems().stream().filter(e -> selectedItem.getCode().equals(e.getCode())).findFirst();
        if(orderItem1.isEmpty()){
            JFXButton delete = new JFXButton("Delete");
            OrderItem orderItem = new OrderItem(selectedItem.getCode(),
                    selectedItem.getDescription(),
                    Integer.parseInt(txtQty.getText()),
                    selectedItem.getUnit_price(), delete);
            tblOrderDetails.getItems().add(orderItem);
            selectedItem.setQty(selectedItem.getQty()-orderItem.getQty());

            delete.setOnAction(e->{
                tblOrderDetails.getItems().remove(orderItem);
                selectedItem.setQty(selectedItem.getQty()+orderItem.getQty());
                addTotal();
                enablePlaceOrderButton();
            });
        }
        else {
            orderItem1.get().setQty(selectedItem.getQty()+orderItem1.get().getQty());
            tblOrderDetails.refresh();
            selectedItem.setQty(selectedItem.getQty()-Integer.parseInt(txtQty.getText()));
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        addTotal();
        enablePlaceOrderButton();

    }
    private void addTotal(){
        Optional<BigDecimal> total = tblOrderDetails.getItems().stream().map(orderItem -> orderItem.getTotal()).reduce((prev, cur) -> prev.add(cur));
        lblTotal.setText("Total: Rs. "+ total.orElseGet(()->BigDecimal.ZERO).setScale(2));
    }
    private void setOrderId() throws SQLException {
        if(OrderDataAccess.getLastOrderId()==null){
            lblId.setText("Order ID: OD001");
        }else{
            int id = Integer.parseInt(OrderDataAccess.getLastOrderId().substring(2))+1;
            lblId.setText(String.format("Order ID: OD%03d",id));
        }
    }

    private void enablePlaceOrderButton(){
        Customer selectedCustomer = cmbCustomerId.getSelectionModel().getSelectedItem();
        btnPlaceOrder.setDisable(!(selectedCustomer != null && !tblOrderDetails.getItems().isEmpty()));
    }

    public void txtQty_OnAction(ActionEvent actionEvent) {
    }

    public void btnPlaceOrder_OnAction(ActionEvent actionEvent)  {
        ObservableList<OrderItem> items = tblOrderDetails.getItems();
        try {
            OrderDataAccess.saveOrder(
                    items,
                   lblId.getText().replace("Order ID:","").strip(),
                    Date.valueOf(lblDate.getText()),
                    cmbCustomerId.getValue().getId());
            printBill();
            newOrder();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to save the order,try again");

        }
    }

    private void printBill(){

        try {
            JasperDesign jasperDesign = JRXmlLoader
                    .load(getClass().getResourceAsStream("/print/my-pos-bill.jrxml"));

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("id", lblId.getText().replace("Order ID: ", "").strip());
            reportParams.put("date", lblDate.getText());
            reportParams.put("customer-id", cmbCustomerId.getValue().getId());
            reportParams.put("customer-name", cmbCustomerId.getValue().getName());
            reportParams.put("total", lblTotal.getText());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams,
                    new JRBeanCollectionDataSource(tblOrderDetails.getItems()));

            JasperViewer.viewReport(jasperPrint, false);
            // JasperPrintManager.printReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to print the bill").show();
        }


/*        try {
            JasperDesign jasperDesign = JRXmlLoader
                    .load(getClass().getResourceAsStream("/print/pos-bill.jrxml"));

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("id", lblId.getText().replace("Order ID: ", "").strip());
            reportParams.put("date", lblDate.getText());
            reportParams.put("customer-id", cmbCustomerId.getValue().getId());
            reportParams.put("customer-name", cmbCustomerId.getValue().getName());
            reportParams.put("total", lblTotal.getText());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams,
                    new JRBeanCollectionDataSource(tblOrderDetails.getItems()));

            JasperViewer.viewReport(jasperPrint, false);
            // JasperPrintManager.printReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to print the bill").show();
        }*/
    }

}
