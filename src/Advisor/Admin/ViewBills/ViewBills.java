package Advisor.Admin.ViewBills;

import Advisor.Admin.ViewBills.AcceptBill.AcceptBill;
import Advisor.Database.DataBaseHandler;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewBills implements Initializable {
    public TableView<billView> BillsTable;
    public TableColumn<billView,String> userName;
    public TableColumn<billView,Double> tax;
    public TableColumn<billView,Double> profit;
    public TableColumn<billView,ImageView> Img;
    public HBox Box;
    Stage parentStage;

    ObservableList<billView> billList = FXCollections.observableArrayList();

    DataBaseHandler DBH;
    int ID;
    String imgLoc;
    double xOffset;
    double yOffset;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBH = DataBaseHandler.getInstance();
        associateColumns();
        loadData();
    }

    public void associateColumns(){
        userName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        profit.setCellValueFactory(new PropertyValueFactory<>("profit"));
        Img.setCellValueFactory(new PropertyValueFactory<>("img"));
    }

    public void loadData(){
        billList.clear();
        ResultSet rs;
        String q = "select * from bills";
        rs = DBH.execQuery(q);
        while(true){
            try {
                if (rs.next()){
                    ResultSet rs1;
                    int id = rs.getInt(1);
                    ID = id;
                    String q1 = "select * from cust where customers_id = "+id+";";
                    imgLoc = rs.getString(2);
                    Image img = new Image(imgLoc);
                    String name = null;
                    rs1 = DBH.execQuery(q1);
                    if(rs1.next()){
                        name = rs1.getString("customers_name");
                    }
                    double tax = 0,profit = 0;
                    q1 = "select * from tax_revenue where customers_id = "+id+";";
                    rs1 = DBH.execQuery(q1);
                    if(rs1.next()){
                        tax = rs1.getDouble("tax");
                        profit = rs1.getDouble("profit_margin");
                    }
                    ImageView image = new ImageView(img);
                    image.setFitWidth(70);
                    image.setFitHeight(90);
                    billList.add(new billView(name,tax,profit,image,imgLoc));
                }else{
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            BillsTable.setItems(billList);
        }
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

        Box.setOnDragDone(event -> CurrentStage.setOpacity(1.0f));

        Box.setOnMouseReleased(event -> CurrentStage.setOpacity(1.0f));
    }

    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
        parentStage.show();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.setFullScreen(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void init(Stage stage) {
        parentStage = stage;
    }

    public void handleAccept(ActionEvent actionEvent) {
        billView selected = BillsTable.getSelectionModel().getSelectedItem();
        imgLoc = selected.getImgLoc();
        ResultSet rs = DBH.execQuery("select cust_id from bills where image = '"+imgLoc+"';");
        try {
            if(rs.next()){
                ID = rs.getInt(1);
                AcceptBill ab = new AcceptBill(ID,imgLoc);
                ab.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleRefresh(ActionEvent actionEvent) {
        loadData();
    }

    public void handleReject(ActionEvent actionEvent) {
        billView selectedBill = BillsTable.getSelectionModel().getSelectedItem();
        imgLoc = selectedBill.getImgLoc();
        String q = "DELETE from bills where image = '"+imgLoc+"';";
        if(DBH.execAction(q)){
            System.out.println("Deleted");
        }else{
            System.out.println("Not deleted");
        }

        loadData();
    }

    public static class billView{
        private SimpleStringProperty name;
        private SimpleDoubleProperty tax;
        private SimpleDoubleProperty profit;
        private ImageView img;
        private String imgLoc;

        public billView(String  uName, Double tax, Double profit, ImageView img,String imgLoc) {
            this.name = new SimpleStringProperty(uName);
            this.tax = new SimpleDoubleProperty(tax);
            this.profit = new SimpleDoubleProperty(profit);
            this.img = img;
            this.imgLoc = imgLoc;
        }

        public String getName() {
            return name.get();
        }

        public double getTax() {
            return tax.get();
        }

        public double getProfit() {
            return profit.get();
        }

        public ImageView getImg() {
            return img;
        }

        public String getImgLoc(){
            return imgLoc;
        }
    }
}
