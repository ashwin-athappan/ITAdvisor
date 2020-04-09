package Advisor.Admin.ViewClient;

import Advisor.Admin.ViewClient.ClientChart.ClientChart;
import Advisor.Database.DataBaseHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewClient implements Initializable {
    public TableView<viewClients> ClientTable;
    public TableColumn<viewClients,String> NameColumn;
    public TableColumn<viewClients,String> EmailColumn;
    public TableColumn<viewClients,Double> IncomeColumn;
    public TableColumn<viewClients,String> AccountTypeColumn;
    public TableColumn<viewClients,Integer> WarningsColumn;
    public TableColumn<viewClients,Double> TaxColumn;
    public JFXTextField SearchBar;
    public JFXButton Search;
    public ChoiceBox<String> Filter;
    public HBox Box;
    boolean darkMode;
    private ObservableList<String> filter = FXCollections.observableArrayList("Customer Name","Email","Income","Warnings","Tax");
    private ObservableList<viewClients> observableList = FXCollections.observableArrayList();
    private DataBaseHandler DBH;

    private double xOffset;
    private double yOffset;

    Stage parentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Filter.setValue("Customer Name");
        Filter.setItems(filter);
        DBH = DataBaseHandler.getInstance();
        associateTheColumns();
        loadData();
    }

    public void setStage(Stage s){
        parentStage = s;
    }

    private void associateTheColumns(){
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        EmailColumn.setCellValueFactory(new PropertyValueFactory<>("CustomerEmail"));
        IncomeColumn.setCellValueFactory(new PropertyValueFactory<>("CustomerIncome"));
        AccountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("CustomerAccountType"));
        WarningsColumn.setCellValueFactory(new PropertyValueFactory<>("Warnings"));
        TaxColumn.setCellValueFactory(new PropertyValueFactory<>("Tax"));
    }

    private void loadData(){
        observableList.clear();
        String q1 = "select * from cust order by customers_id";
        String q2 = "select * from tax_revenue order by customers_id";
        ResultSet rs = DBH.execQuery(q1);
        ResultSet rs1 = DBH.execQuery(q2);

        while(true){
            try {
                if(rs.next() && rs1.next()){
                    if(rs.getInt(1) == rs.getInt(1)){
                        String Name = rs.getString("customers_name");
                        String Email = rs.getString("email");
                        Double Income = rs.getDouble("income");
                        Integer AccountType = rs.getInt("account_type");
                        Double Tax = rs1.getDouble("tax");
                        Integer Warnings = rs1.getInt("warnings");
                        boolean paidStatus = rs1.getBoolean("tax_paid");
                        observableList.add(new viewClients(Name,Email,AccountType,Income,Warnings,Tax,paidStatus));
                    }else{
                        System.out.println("ID not matching");
                        break;
                    }
                }else{
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ClientTable.setItems(observableList);
    }

    public void setDraggable(MouseEvent mouseEvent) {
        Stage CurrentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

        Box.setOnMousePressed(event -> {
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
        Stage CurrentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        CurrentStage.close();
        parentStage.show();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage CurrentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        CurrentStage.setMaximized(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage CurrentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        CurrentStage.setIconified(true);
    }

    public void handleRefresh(ActionEvent actionEvent) {
        loadData();
    }

    public void handleRemove(ActionEvent actionEvent) {
        viewClients selectedClient = ClientTable.getSelectionModel().getSelectedItem();
        String email = selectedClient.getCustomerEmail();
        int id;
        ResultSet rs;
        String q = "select * from cust where email = '"+email+"';";
        rs = DBH.execQuery(q);
        try {
            if(rs.next()){
               id = rs.getInt(1);
               q = "delete from password_display where id = "+id;
               if(DBH.execAction(q)){
                   q = "delete from passwords where customer_id = "+id;
                   if(DBH.execAction(q)){
                       q = "delete from tax_revenue where customers_id = "+id;
                       if(DBH.execAction(q)){
                           q = "delete from cust where customers_id = "+id;
                           if(DBH.execAction(q)){
                               System.out.println("Client "+selectedClient.getCustomerName()+" has been removed");
                           }
                       }
                   }
               }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleView(ActionEvent actionEvent) {

    }

    public void handleSearch(ActionEvent actionEvent) {
        //"Customer Name","Email","Income","Account Type","Warnings","Tax"
        ResultSet rs = null;
        String key = SearchBar.getText();
        if(key.isEmpty()){
            loadData();
            return;
        }
        String selectedFilter = Filter.getValue();
        switch (selectedFilter){
            case "Customer Name":
                rs = DBH.execSpecialQuery(1,key);
                assert rs != null;
                searchAssistantForTax(rs);
                break;
            case "Email":
                rs = DBH.execSpecialQuery(2,key);
                assert rs != null;
                searchAssistantForTax(rs);
                break;
            case "Income":
                rs = DBH.execSpecialQuery(3,key);
                assert rs != null;
                searchAssistantForTax(rs);
                break;
            case "Warnings":
                rs = DBH.execSpecialQuery(4,key);
                assert rs != null;
                searchAssistantForCustomer(rs);
                break;
            case "Tax":
                rs = DBH.execSpecialQuery(5,key);
                assert rs != null;
                searchAssistantForCustomer(rs);
                break;
            default:
        }
    }

    private void searchAssistantForCustomer(ResultSet rs) {
        observableList.clear();

        while(true){
            try {
                if(rs.next()){
                    int id = rs.getInt(1);
                    ResultSet rs1 = DBH.SelectPreppedStatement("cust",id);
                    Double Tax = rs.getDouble("tax");
                    Integer Warnings = rs.getInt("warnings");
                    boolean paidStatus = rs.getBoolean("tax_paid");
                    if(rs1.next()){
                        String Name = rs1.getString("customers_name");
                        String Email = rs1.getString("email");
                        Double Income = rs1.getDouble("income");
                        Integer AccountType = rs1.getInt("account_type");
                        observableList.add(new viewClients(Name,Email,AccountType,Income,Warnings,Tax,paidStatus));
                    }
                }else{
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ClientTable.setItems(observableList);
    }

    private void searchAssistantForTax(ResultSet rs){
        observableList.clear();

        while(true){
            try {
                if(rs.next()){
                    int id = rs.getInt(1);
                    ResultSet rs1 = DBH.SelectPreppedStatement("tax_revenue",id);
                    String Name = rs.getString("customers_name");
                    String Email = rs.getString("email");
                    Double Income = rs.getDouble("income");
                    Integer AccountType = rs.getInt("account_type");
                    if(rs1.next()){
                        Double Tax = rs1.getDouble("tax");
                        Integer Warnings = rs1.getInt("warnings");
                        boolean paidStatus = rs1.getBoolean("tax_paid");
                        observableList.add(new viewClients(Name,Email,AccountType,Income,Warnings,Tax,paidStatus));
                    }
                }else{
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        ClientTable.setItems(observableList);

    }

    public void DisplayClientGraph(ActionEvent actionEvent) {
        String location = "/Advisor/Admin/ViewClient/ClientChart/ClientChart.fxml";
        Stage parentStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            ClientChart controller = loader.getController();
            controller.init(parentStage);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Client Chart");
            stage.setScene(new Scene(parent));
            Scene scene = stage.getScene();
            try {
                FileInputStream in = new FileInputStream("DarkMode.txt");
                DataInputStream DIS = new DataInputStream(in);
                darkMode = Boolean.parseBoolean(DIS.readUTF());
                if(darkMode){
                    System.out.println("Dark Mode is set");
                }else{
                    System.out.println("Dark Mode is not set");
                }
                in.close();
                DIS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(darkMode){
                scene.getStylesheets().add(getClass().getResource("/Advisor/Dark_Mode.css").toExternalForm());
            }else{
                scene.getStylesheets().remove(getClass().getResource("/Advisor/Dark_Mode.css").toExternalForm());
            }
            stage.show();
            parentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class viewClients {
        private SimpleStringProperty CustomerName;
        private SimpleStringProperty CustomerEmail;
        private SimpleStringProperty CustomerAccountType;
        private SimpleDoubleProperty CustomerIncome;
        private SimpleIntegerProperty Warnings;
        private SimpleDoubleProperty Tax;
        private SimpleBooleanProperty paidStatus;

        public viewClients(String name, String email, Integer accountType, Double income, Integer warnings, Double tax, boolean paidStatus) {
            CustomerName = new SimpleStringProperty(name);
            CustomerEmail = new SimpleStringProperty(email);
            CustomerIncome = new SimpleDoubleProperty(income);
            Warnings = new SimpleIntegerProperty(warnings);
            Tax = new SimpleDoubleProperty(tax);
            this.paidStatus = new SimpleBooleanProperty(paidStatus);
            CustomerAccountType = new SimpleStringProperty("null");
            switch (accountType){
                case 1: CustomerAccountType = new SimpleStringProperty("Personal");
                    break;
                case 2: CustomerAccountType = new SimpleStringProperty("Corporate");
                    break;
                case 3: CustomerAccountType = new SimpleStringProperty("VIP");
                    break;
                default:
            }
        }

        public String getCustomerName() { return CustomerName.get(); }

        public String getCustomerEmail() {
            return CustomerEmail.get();
        }

        public String getCustomerAccountType() {
            return CustomerAccountType.get();
        }

        public double getCustomerIncome() {
            return CustomerIncome.get();
        }

        public int getWarnings() {
            return Warnings.get();
        }

        public double getTax() {
            return Tax.get();
        }

        public boolean getStatus(){
            return paidStatus.get();
        }
    }
}
