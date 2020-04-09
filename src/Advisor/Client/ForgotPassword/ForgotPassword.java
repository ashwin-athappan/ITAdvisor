package Advisor.Client.ForgotPassword;

import Advisor.Database.DataBaseHandler;
import Advisor.Mail.MailUtil;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.ResourceBundle;

public class ForgotPassword implements Initializable {
    public HBox Box;
    public JFXTextField UserEmail;
    public AnchorPane ForgotPasswordPane;
    DataBaseHandler DBH;
    private double xOffset = 0;
    private double yOffset = 0;
    Stage parentStage;

    int id;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBH = DataBaseHandler.getInstance();
    }

    public void init(Stage s){
        parentStage = s;
    }

    public void setDraggable(MouseEvent mouseEvent) {
        new Thread(() -> {
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
        }).start();
    }

    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) ForgotPasswordPane.getScene().getWindow();
        stage.close();
        parentStage.show();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage) ForgotPasswordPane.getScene().getWindow();
        stage.setFullScreen(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage stage = (Stage) ForgotPasswordPane.getScene().getWindow();
        stage.setIconified(true);
    }

    public void handleSendMail(ActionEvent actionEvent) {
        ResultSet resultSet = DBH.execQuery(" select * from cust where email='"+UserEmail.getText()+"';");
        try {
            if(resultSet.next()){
                id=resultSet.getInt(1);
                String pass;
                resultSet = DBH.execQuery("select * from passwords where customer_id = "+id+";");
                try {
                    if(resultSet.next()){
                        pass = resultSet.getString("password");
                        byte[] decode = Base64.getDecoder().decode(pass);
                        pass = new String(decode);
                        String Message = "Your Password for the account associated with the email address '"+UserEmail.getText()+"' is ----- : "+pass;
                        new Thread(() -> {
                            MailUtil.sendMail(UserEmail.getText(),Message);
                        }).start();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Email Not found");
                alert.setContentText("Email "+UserEmail.getText()+"does not exist.");
                alert.showAndWait();
                UserEmail.getStyleClass().add("wrong-Credentials");
                ((Stage)((Node)(actionEvent.getSource())).getScene().getWindow()).close();
                parentStage.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
