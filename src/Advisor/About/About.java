package Advisor.About;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class About implements Initializable {
    public void close(MouseEvent mouseEvent) {
        Stage s = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        s.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
