package Advisor.Admin.SendMail;

import Advisor.Database.DataBaseHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import Advisor.Mail.MailUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SendMail implements Initializable {
    public JFXTextArea Message;
    public JFXButton SendButton;
    public ChoiceBox<String> ClientChooser;
    public HBox Box;
    private ObservableList<String> AccountName;
    private DataBaseHandler DBH;
    ResultSet customerDetails;
    int counter;
    Stage parentStage;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AccountName = FXCollections.observableArrayList();
        AccountName.clear();
        DBH = DataBaseHandler.getInstance();
        customerDetails = DBH.execQuery("select * from cust");
        counter = 0;
        while (true){
            try {
                if(customerDetails.next()){
                    AccountName.add(customerDetails.getString("email"));
                }else{
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ClientChooser.setItems(AccountName);
    }

    public void init(Stage s){
        parentStage = s;
    }

    public void handleSendMail(ActionEvent actionEvent) {
        String email = ClientChooser.getSelectionModel().getSelectedItem();
        String message =  Message.getText();
        new Thread(()->
                MailUtil.sendMail(email,message)
        ).start();
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
}
