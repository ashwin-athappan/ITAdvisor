package Advisor.Admin.ViewRequest;

import Advisor.Database.DataBaseHandler;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

public class ViewRequest implements Initializable {
    public MenuBar Menu;
    public TableView<ViewClientRequest> RequestTable;
    public TableColumn<ViewClientRequest,String> NameColumn;
    public TableColumn<ViewClientRequest,String> EmailColumn;
    public TableColumn<ViewClientRequest,Double> IncomeColumn;
    public TableColumn<ViewClientRequest,Integer> AccountTypeColumn;
    public HBox Box;

    ObservableList<ViewClientRequest> observableList = FXCollections.observableArrayList();

    private double xOffset = 0;
    private double yOffset = 0;

    DataBaseHandler DBH;
    private Stage parentStage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        associateColumns();
        loadData();
    }

    public void init(Stage s){
        parentStage = s;
    }
    //Names Associate with the ViewClients Class
    private void associateColumns() {
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        EmailColumn.setCellValueFactory(new PropertyValueFactory<>("Email"));
        IncomeColumn.setCellValueFactory(new PropertyValueFactory<>("Income"));
        AccountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("AccountType"));
    }

    private void loadData() {
        observableList.clear();
        DBH = DataBaseHandler.getInstance();
        String q = "select * from cust_request";
        ResultSet rs = DBH.execQuery(q);

        while (true) {
            try {
                if (rs.next()) {
                    String Name = rs.getString("customers_name");
                    String Email = rs.getString("email");
                    Double Income = rs.getDouble("income");
                    Integer AccountType = rs.getInt("account_type");
                    String Password = rs.getString("password");
                    observableList.add(new ViewClientRequest(Name,Email,Income,AccountType,Password));
                } else {
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        RequestTable.setItems(observableList);
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
        Stage s = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        s.close();
        parentStage.show();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage s = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        s.setFullScreen(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage s = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        s.setIconified(true);
    }

    public void handleRefresh(ActionEvent actionEvent) {
        loadData();
    }

    public void handleDelete(ActionEvent actionEvent) {
        ViewClientRequest selectedClient = RequestTable.getSelectionModel().getSelectedItem();
        if(selectedClient == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Book Selected");
            alert.setContentText("Please Select a Book");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to add");
        Optional<ButtonType> optionalButton = alert.showAndWait();
        if(optionalButton.get() == ButtonType.OK){
            String Email = selectedClient.getEmail();
            if(DBH.execAction("delete from cust_request where email='"+Email+"';")){
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Deleted Successfully");
                alert.setContentText("Client Request with "+Email+" was deleted.");
            }
        }else{
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Canceled");
            alert.setContentText("Accept Operation Canceled");
        }

        loadData();
    }

    public void handleAccept(ActionEvent actionEvent) {
        ViewClientRequest selectedClient = RequestTable.getSelectionModel().getSelectedItem();
        if(selectedClient == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Book Selected");
            alert.setContentText("Please Select a Book");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to add");
        Optional<ButtonType> optionalButton = alert.showAndWait();
        if(optionalButton.get() == ButtonType.OK){
             String Name = selectedClient.getName();
             String Email = selectedClient.getEmail();
             Double Income = selectedClient.getIncome();
             String accountType = selectedClient.getAccountType();
             String Password = selectedClient.getPassword();
             int customerID = 0;
             ResultSet rs;
             int AccountType = 0;
             switch (accountType){
                 case "Personal": AccountType = 1;
                     break;
                 case "Corporate": AccountType = 2;
                     break;
                 case "V.I.P": AccountType = 3;
                     break;
                 default:;
             }
            String queryCustomer = "insert into cust(customers_name,email,income,account_type,security_pin) values('"+Name+"','"+Email+"',"+Income+","+AccountType+",'"+SecurityNumberGenerator()+"')";
            if(DBH.execAction(queryCustomer)){
                String get_customer_id = "select * from cust where email = '"+Email+"'";
                rs = DBH.execQuery(get_customer_id);
                try {
                    if(rs.next()){
                        customerID = rs.getInt("customers_id");
                    }
                } catch (SQLException e) {
                    System.out.println("SQL Exception Occurred "+e.getMessage());
                }
            }

            String deleteFromRequests = "delete from cust_request where email='"+Email+"'";
            if(DBH.execAction(deleteFromRequests)){
                observableList.remove(selectedClient);
            }

            String queryTax_Revenue = null;
            switch (accountType){
                case "Personal":
                    if(Income < 250000){
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+0+","+false+","+0+");";
                    }else if(Income >= 250000 && Income <500000 ){
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+Income*0.05+","+false+","+0+");";
                    }else if(Income >= 500000 && Income <1000000){
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+Income*0.2+","+false+","+0+");";
                    }else{
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+Income*0.3+","+false+","+0+");";
                    }
                    break;
                case "Corporate":
                    queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+Income*0.25+","+false+","+0+");";
                    break;
                case "V.I.P":
                    if(Income < 250000){
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+0+","+false+","+0+");";
                    }else if(Income >= 250000 && Income <500000 ){
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+Income*0.01+","+false+","+0+");";
                    }else if(Income >= 500000 && Income <1000000){
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+Income*0.05+","+false+","+0+");";
                    }else{
                        queryTax_Revenue = "insert into tax_revenue(customers_id,income,tax,tax_paid,warnings) values ("+customerID+","+Income+","+Income*0.2+","+false+","+0+");";
                    }
                    break;
                default:;
            }

            if(DBH.execAction(queryTax_Revenue)){
                String queryPassword = "insert into passwords values('"+customerID+"','"+Password+"')";
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

        }else{
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Canceled");
            alert.setContentText("Accept Operation Canceled");
        }

        loadData();
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

    public static class ViewClientRequest {
        private SimpleStringProperty Name;
        private SimpleStringProperty Email;
        private SimpleDoubleProperty Income;
        private SimpleStringProperty AccountType;
        private SimpleStringProperty Password;

        public ViewClientRequest(String name, String  email, Double income, Integer accountType, String password) {
            Name = new SimpleStringProperty(name);
            Email = new SimpleStringProperty(email);
            Income = new SimpleDoubleProperty(income);
            AccountType = new SimpleStringProperty("null");
            switch (accountType){
                case 1: AccountType = new SimpleStringProperty("Personal");
                    break;
                case 2: AccountType = new SimpleStringProperty("Corporate");
                    break;
                case 3: AccountType = new SimpleStringProperty("V.I.P");
                    break;
                default:;
            }
            Password = new SimpleStringProperty(password);
        }

        public String getName(){
            return Name.get();
        }

        public String getEmail(){
            return Email.get();
        }

        public Double getIncome(){
            return Income.get();
        }

        public String getAccountType(){
            return AccountType.get();
        }

        public String getPassword(){
            return Password.get();
        }

    }
}
