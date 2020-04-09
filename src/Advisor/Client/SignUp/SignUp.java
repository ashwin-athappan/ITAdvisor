package Advisor.Client.SignUp;

import Advisor.Admin.AddClient.AddClient.*;
import Advisor.Database.DataBaseHandler;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.ResourceBundle;

public class SignUp implements Initializable {
    public JFXTextField CustomerName;
    public JFXTextField CustomerEmail;
    public JFXTextField CustomerIncome;
    public JFXTextField CustomerPassword;
    public ChoiceBox<String> TypeOfAccount;
    public AnchorPane SignUpPane;
    private ObservableList<String> typeOfAccount = FXCollections.observableArrayList("Personal","Corporate","VIP");
    DataBaseHandler DBH;
    cust c;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TypeOfAccount.setValue("Personal");
        TypeOfAccount.setItems(typeOfAccount);
        DBH = DataBaseHandler.getInstance();
    }

    public void handleSignUp(ActionEvent actionEvent) {
        String name = CustomerName.getText();
        String Income = CustomerIncome.getText();
        String email = CustomerEmail.getText();
        String password = CustomerPassword.getText();

        if(name.isEmpty() || email.isEmpty() || Income.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error Adding");
            alert.setContentText("Don't Leave any field empty");
            alert.showAndWait();
        }else{
            c = new cust();
            c.setClientName(name);
            c.setClientEmail(email);
            c.setIncome(Double.parseDouble(Income));
            c.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
            String AccountType = TypeOfAccount.getValue();
            switch (AccountType){
                case "Personal":
                    c.setAccountType(1);
                    break;
                case "Corporate":
                    c.setAccountType(2);
                    break;
                case  "VIP":
                    c.setAccountType(3);
                    break;
                default:
                    break;
            }

            String checkEmailInRequests = "select * from cust_request where email='" + c.getClientEmail() + "'";
            String checkEmailInCustomers = "select * from cust where email='" + c.getClientEmail() + "'";
            ResultSet rs = DBH.execQuery(checkEmailInRequests);
            ResultSet rs1 = DBH.execQuery(checkEmailInCustomers);
            try {
                if (rs.next() || rs1.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error Adding");
                    alert.setContentText("An account with the give email " + c.getClientEmail() + " already exists");
                    alert.showAndWait();
                } else {
                    String submitRequest = "insert into cust_request values('" + c.getClientName() + "','" + c.getClientEmail() + "'," + c.getIncome() + "," + c.getAccountType() + ",'" + c.getPassword() + "');";
                    DBH.execAction(submitRequest);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Your request was submitted");
                alert.showAndWait();
                Stage stage = (Stage) SignUpPane.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) SignUpPane.getScene().getWindow();
        stage.close();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage) SignUpPane.getScene().getWindow();
        stage.setFullScreen(true);
    }

    public void minimize(ContextMenuEvent mouseEvent) {
        Stage stage = (Stage) SignUpPane.getScene().getWindow();
        stage.setIconified(true);
    }
}
