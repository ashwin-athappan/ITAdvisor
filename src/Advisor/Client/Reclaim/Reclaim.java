package Advisor.Client.Reclaim;

import Advisor.Database.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Reclaim implements Initializable {
    public ImageView ImagePreview;
    public HBox Box;
    double xOffset;
    double yOffset;
    boolean image_selected;
    String imagePath;
    int id;
    DataBaseHandler DBH;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        image_selected = false;
        DBH = DataBaseHandler.getInstance();
    }

    public void init(int id){
        this.id = id;
    }

    public void chooseImage(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        File selected = fc.showOpenDialog(null);
        String path = "";
        if (selected != null) {
            path=selected.getAbsolutePath();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Invalid file");
            alert.setTitle("Error");
            alert.showAndWait();
        }
        if(path.equals("")){
            return;
        }
        image_selected = true;
        imagePath = "file:///"+path.replace("\\","/");
        ImagePreview.setX(10);
        ImagePreview.setY(10);
        ImagePreview.setImage(new Image(imagePath));
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
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.setMaximized(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void handleUpload(ActionEvent actionEvent) {
        String q = "insert into bills values("+id+",'"+imagePath+"');";
        if(DBH.execAction(q)){
            System.out.println("Bill Uploaded");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Upload Successful");
            alert.setContentText("Bill Successfully Uploaded");
            alert.showAndWait();
        }else{
            System.out.println("Bill Not Uploaded");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Upload Unsuccessful");
            alert.setContentText("Image already used..");
            alert.showAndWait();
        }
    }
}
