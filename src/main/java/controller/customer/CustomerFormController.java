package controller.customer;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;

import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CustomerFormController implements Initializable {

    @FXML
    private TableColumn<?, ?> colAddress;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colSalary;

    @FXML
    private TableView<Customer> tblCustomers;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtSalary;

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (txtName.getText().isEmpty() || txtAddress.getText().isEmpty() || txtSalary.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incomplete Information");
            alert.setHeaderText("Please fill in all fields to add the customer...");
            alert.show();
        } else {
            if (CustomerController.getInstance().saveCustomer(new Customer(
                    txtId.getText(),
                    txtName.getText(),
                    txtAddress.getText(),
                    Double.parseDouble(txtSalary.getText())
            ))) {
                clearFields();
                loadTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to Add Customer");
                alert.setContentText("There was an issue adding the customer. Please verify the input data...");
                alert.show();
            }
        }

    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) throws SQLException {
        boolean isExist = false;
        for (Customer customer : CustomerController.getInstance().getAll()) {
            if (customer.getId().equals(txtId.getText())) {
                isExist = true;
                break;
            }
        }
        if (isExist) {
            Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this customer?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alertConfirmation.showAndWait();
            ButtonType buttonType = result.orElse(ButtonType.NO);
            if (buttonType == ButtonType.YES) {
                if (CustomerController.getInstance().deleteCustomer(txtId.getText())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Customer Deleted");
                    alert.setHeaderText("Customer Successfully Deleted...");
                    alert.show();
                    clearFields();
                    loadTable();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Deletion Failed");
                    alert.setHeaderText("An error occurred while deleting the customer...");
                    alert.show();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Customer Not Found");
            alert.setHeaderText("Please enter a valid existing Customer Id...");
            alert.show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        if (txtName.getText().isEmpty() || txtAddress.getText().isEmpty() || txtSalary.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Incomplete Information");
            alert.setHeaderText("Please fill in all fields to update the customer.");
            alert.show();
        } else {
            Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to update this customer?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alertConfirmation.showAndWait();
            ButtonType buttonType = result.orElse(ButtonType.NO);
            if (buttonType == ButtonType.YES) {
                if (CustomerController.getInstance().updateCustomer(new Customer(
                        txtId.getText(),
                        txtName.getText(),
                        txtAddress.getText(),
                        Double.parseDouble(txtSalary.getText())
                ))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Customer Updated");
                    alert.setHeaderText("The customer's details have been successfully updated...");
                    alert.show();
                    clearFields();
                    loadTable();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update Failed");
                    alert.setHeaderText("There was an issue updating the customer's details. Please try again...");
                    alert.show();
                }
            }
        }

    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        boolean isExist = false;
        for (Customer customer : CustomerController.getInstance().getAll()) {
            if (customer.getId().equals(txtId.getText())) {
                isExist = true;
                break;
            }
        }
        if (isExist) {
            Customer customer = CustomerController.getInstance().searchCustomer(txtId.getText());
            if (customer != null) {
                txtName.setText(customer.getName());
                txtAddress.setText(customer.getAddress());
                txtSalary.setText(customer.getSalary().toString());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Customer Not Found");
                alert.setHeaderText("No customer was found with the provided ID.");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Customer Not Found");
            alert.setHeaderText("Please enter a valid existing Customer Id...");
            alert.show();
        }
    }

    private void setTextToValues(Customer customer) {
        txtId.setText(customer.getId());
        txtName.setText(customer.getName());
        txtAddress.setText(customer.getAddress());
        txtSalary.setText(String.valueOf(customer.getSalary()));
    }

    private void loadTable() {


        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));


        CustomerController customercontroller=CustomerController.getInstance();
        List<Customer> all = customercontroller.getAll();

        ObservableList<Customer> customerObservableList= FXCollections.observableArrayList();

        all.forEach(customer -> {
            customerObservableList.add(customer);

        });

        tblCustomers.setItems(customerObservableList);

        tblCustomers.setItems(customerObservableList);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadTable();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldvalue, newValue) -> {
            if (newValue != null){
                setTextToValues((Customer) newValue);
            }
        });
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
    }


}





