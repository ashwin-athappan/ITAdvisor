package Advisor.Admin.ViewClient.ClientChart;

import Advisor.Database.DataBaseHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClientChart implements Initializable {
    public HBox Box;
    @FXML
    private BarChart<String,Double> TaxGraph;
    @FXML
    private CategoryAxis X;
    @FXML
    private NumberAxis Y;

    private double xOffset,yOffset;
    Stage parentStage;

    private DataBaseHandler DBH;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBH = DataBaseHandler.getInstance();
        XYChart.Series<String,Double> data = new XYChart.Series<>();
        ResultSet rs = DBH.execQuery("Select customers_name,income from cust;");
        while(true){
            try {
                if(rs.next()){
                    data.getData().add(new XYChart.Data<>(rs.getString(1),rs.getDouble(2)));
                }else break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        TaxGraph.getData().addAll(data);
    }

    public void init(Stage stage){
        parentStage = stage;
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
}
