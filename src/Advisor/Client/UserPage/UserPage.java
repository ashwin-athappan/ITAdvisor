package Advisor.Client.UserPage;

import Advisor.Client.Reclaim.Reclaim;
import Advisor.Client.UserPage.ChangePassword.ChangePassword;
import Advisor.Client.UserPage.PinReset.PinResetJSwing;
import Advisor.Database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserPage implements Initializable {
    public Label IncomeLabel;
    public Label TaxLabel;
    public Label BalanceLabel;
    public Label NameLabel;
    public HBox Box;
    public AnchorPane UserPane;
    public PieChart pieChart;
    String Name;
    String Email;
    double income,tax,profit;
    boolean paymentStatus;
    int ID;
    Stage parentStage;
    DataBaseHandler DBH;
    private static String Pin;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBH = DataBaseHandler.getInstance();
    }

    public void Init(String name, String email, int id, Stage stage){
        Name = name;
        Email = email;
        ID = id;
        NameLabel.setText("Hi "+Name+" What are you upto?");
        String taxQ = "select * from tax_revenue where customers_id = '"+ID+"';";
        ResultSet rs = DBH.execQuery(taxQ);
        try {
            if(rs.next()){
                income = rs.getDouble("income");
                tax = rs.getDouble("tax");
                profit = rs.getDouble("profit_margin");
                TaxLabel.setText("TAX : "+rs.getString("tax"));
                boolean status = rs.getBoolean("tax_paid");
                paymentStatus = status;
                if(status){
                    BalanceLabel.setText("PAYMENT : Done");
                }else{
                    BalanceLabel.setText("PAYMENT : Pending");
                }
                IncomeLabel.setText("Income : "+income);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        parentStage = stage;
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
        Stage stage = (Stage) UserPane.getScene().getWindow();
        stage.close();
        parentStage.show();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage) UserPane.getScene().getWindow();
        stage.setMaximized(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage stage = (Stage) UserPane.getScene().getWindow();
        stage.setIconified(true);
    }

    public void handleChangePassword(ActionEvent actionEvent) {
        String location = "/Advisor/Client/UserPage/ChangePassword/ChangePassword.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            ChangePassword controller = loader.getController();
            controller.Init(ID);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Add Client");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePayTaxes(ActionEvent actionEvent) {
        ResultSet rs = DBH.execQuery("select * from tax_revenue where customers_id="+ID+";");
        try {
            if(rs.next()){
                if(!rs.getBoolean("tax_paid")){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Are you sure you want to pay "+rs.getDouble("tax"));
                    Optional<ButtonType> optionalButton =  alert.showAndWait();
                    if(optionalButton.get() == ButtonType.OK){
                        rs =  DBH.execQuery("select * from cust where customers_id="+ID+";");
                        if(rs.next()) {
                            Pin = rs.getString("security_pin");
                            PinResetJSwing ip = new PinResetJSwing(Pin,ID,Email);
                            ip.setVisible(true);
                        }
                    }else{
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Your Tax is Still not paid");
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Your Tax is already paid");
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayChart(Event event) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                                                new PieChart.Data("income",income),
                                                new PieChart.Data("tax",tax),
                                                new PieChart.Data("profit",profit));
        pieChart.setData(pieChartData);
    }

    public void bills(ActionEvent actionEvent) {
        String location = "/Advisor/Client/Reclaim/Reclaim.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            Reclaim controller = loader.getController();
            controller.init(ID);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Add Client");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
