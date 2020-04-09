package Advisor.Client.UserPage.ChangePassword;

import Advisor.Database.DataBaseHandler;
import com.jfoenix.controls.JFXPasswordField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.ResourceBundle;

public class ChangePassword implements Initializable {
    public JFXPasswordField OLD;
    public JFXPasswordField NEW;
    public JFXPasswordField RE;
    DataBaseHandler DBH;
    int id;
    String old;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DBH = DataBaseHandler.getInstance();
    }

    public void Init(int id){
        this.id = id;
    }

    public void setDraggable(MouseEvent mouseEvent) {
    }

    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void maximize(MouseEvent mouseEvent) {
    }

    public void minimize(MouseEvent mouseEvent) {
    }

    public void change(ActionEvent actionEvent) {
        old = Base64.getEncoder().encodeToString(OLD.getText().getBytes());
        ResultSet rs = DBH.execQuery("select * from passwords where customer_id = '"+id+"';");
        String pass = "";
        try {
            if(rs.next()){
                pass = rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(old.equals(pass)){
            if(NEW.getText().equals(RE.getText())){
                String newPassword = Base64.getEncoder().encodeToString(NEW.getText().getBytes());
                if(DBH.execUpdate("update passwords set password='"+newPassword+"' where customer_id = "+id+";")){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Password Updated");
                    alert.showAndWait();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Both Passwords don't match");
                alert.showAndWait();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error Updating Password");
            alert.showAndWait();
        }
    }
}
