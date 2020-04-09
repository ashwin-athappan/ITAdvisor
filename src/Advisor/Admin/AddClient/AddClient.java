package Advisor.Admin.AddClient;

import Advisor.Database.DataBaseHandler;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;
import java.util.ResourceBundle;

public class AddClient implements Initializable {
    public JFXTextField CustomerName;
    public JFXTextField CustomerEmail;
    public ChoiceBox<String> TypeOfAccount;
    public AnchorPane AddClientPane;
    public JFXTextField CustomerIncome;
    public JFXPasswordField CustomerPassword;
    public HBox Box;

    Stage parentStage;

    private ObservableList<String> typeOfAccount = FXCollections.observableArrayList("Personal","Corporate","VIP");
    cust c;
    DataBaseHandler DBH;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TypeOfAccount.setValue("Personal");
        TypeOfAccount.setItems(typeOfAccount);
        DBH = DataBaseHandler.getInstance();
    }

    public void init(Stage s){
        parentStage = s;
    }

    public void handleAdd(ActionEvent actionEvent) {
        ResultSet rs;
        int customerID = 0;
        String name = CustomerName.getText();
        String email = CustomerEmail.getText();
        String Income = CustomerIncome.getText();
        if(name.isEmpty() || email.isEmpty() || Income.isEmpty()){
            JOptionPane.showMessageDialog(null,"Enter All The Fields","All the fields are mandatory",JOptionPane.ERROR_MESSAGE);
        }else{
            System.out.println("Successfully added");
            c = new cust();
            c.ClientName = name;
            c.ClientEmail = email;
            c.Income = Double.parseDouble(Income);
            c.password = Base64.getEncoder().encodeToString(CustomerPassword.getText().getBytes());
            String AccountType = TypeOfAccount.getValue();
            switch (AccountType){
                case "Personal":
                    c.AccountType = 1;
                    break;
                case "Corporate":
                    c.AccountType = 2;
                    break;
                case  "VIP":
                    c.AccountType = 3;
                    break;
                default:
                    break;
            }

            rs = DBH.execQuery("select * from cust where email='"+c.getClientEmail()+"'");

            try {
                if(rs.next()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error Adding");
                    alert.setContentText("An account with the give email "+c.getClientEmail()+" already exists");
                    alert.showAndWait();
                }else{
                    String queryCustomer = "insert into cust(customers_name,email,income,account_type,security_pin) values('"+c.ClientName+"','"+c.ClientEmail+"',"+c.Income+","+c.AccountType+",'"+SecurityNumberGenerator()+"')";
                    if(DBH.execAction(queryCustomer)){
                        String get_customer_id = "select * from cust where email = '"+c.ClientEmail+"'";
                        rs = DBH.execQuery(get_customer_id);
                        try {
                            if(rs.next()){
                                customerID = rs.getInt("customers_id");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    String queryTax_Revenue = null;
                    switch (c.AccountType){
                        case 1:
                            if(c.Income < 250000){
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+0+","+false+","+0+");";
                            }else if(c.Income >= 250000 && c.Income <500000 ){
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+c.Income*0+","+false+","+0+");";
                            }else if(c.Income >= 500000 && c.Income <1000000){
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+Math.abs(c.Income*0.2)+","+false+","+0+");";
                            }else{
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+Math.abs(c.Income*0.3)+","+false+","+0+");";
                            }
                            break;
                        case 2:
                            queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+Math.abs(c.Income*0.35)+","+false+","+0+");";
                            break;
                        case 3:
                            if(c.Income < 250000){
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+0+","+false+","+0+");";
                            }else if(c.Income >= 250000 && c.Income <500000 ){
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+Math.abs(c.Income*0.1)+","+false+","+0+");";
                            }else if(c.Income >= 500000 && c.Income <1000000){
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+Math.abs(c.Income*0.2)+","+false+","+0+");";
                            }else{
                                queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+c.Income+","+Math.abs(c.Income*0.3)+","+false+","+0+");";
                            }
                            break;
                        default:;
                    }
                    if(DBH.execAction(queryTax_Revenue)){
                        String queryPassword = "insert into passwords values('"+customerID+"','"+c.password+"')";
                        if(DBH.execAction(queryPassword)){
                            System.out.println("Passwords Successful");
                        }else{
                            System.out.println("Passwords ERROR");
                        }

                        queryPassword = "insert into password_display values('"+customerID+"','"+0+"')";
                        if(DBH.execAction(queryPassword)){
                            System.out.println("Password_Display Successful");
                        }else{
                            System.out.println("Password_Display ERROR");
                        }
                    }else{
                        System.out.println("Tax_Revenue ERROR");
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Client "+c.getClientName()+" has been added");
                    alert.showAndWait();
                    Stage stage = (Stage) AddClientPane.getScene().getWindow();
                    stage.close();
                    parentStage.show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }

    public String SecurityNumberGenerator() {
        int length = 5;
        int left = 48;
        int right = 90;
        Random r = new Random();
        String random = r.ints(left, right).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            System.out.println(random);
        return random;
    }

    public void setDraggable(MouseEvent mouseEvent) {
        Stage CurrentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        Box.setOnMousePressed( event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        Box.setOnMouseDragged(event -> {
            CurrentStage.setX(event.getScreenX() - xOffset);
            CurrentStage.setY(event.getScreenY() - yOffset);
            CurrentStage.setOpacity(0.9f);
        });

        Box.setOnDragDone(event -> {
            CurrentStage.setOpacity(1.0f);
        });

        Box.setOnMouseReleased(event -> {
            CurrentStage.setOpacity(1.0f);
        });
    }


    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) AddClientPane.getScene().getWindow();
        stage.close();
        parentStage.show();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage) AddClientPane.getScene().getWindow();
        stage.setFullScreen(true);
    }

    public void minimize(ContextMenuEvent mouseEvent) {
        Stage stage = (Stage) AddClientPane.getScene().getWindow();
        stage.setIconified(true);
    }



    public static class cust{
        private String ClientName;
        private String ClientEmail;
        private String password;
        private int AccountType;
        private double Income;

        public String getClientName() {
            return ClientName;
        }

        public void setClientName(String clientName) {
            ClientName = clientName;
        }

        public String getClientEmail() {
            return ClientEmail;
        }

        public void setClientEmail(String clientEmail) {
            ClientEmail = clientEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getAccountType() {
            return AccountType;
        }

        public void setAccountType(int accountType) {
            AccountType = accountType;
        }

        public double getIncome() {
            return Income;
        }

        public void setIncome(double income) {
            Income = income;
        }
    }
}
