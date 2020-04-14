package Advisor.Admin;

import Advisor.Admin.AddClient.AddClient;
import Advisor.Admin.SendMail.SendMail;
import Advisor.Admin.ViewBills.ViewBills;
import Advisor.Admin.ViewClient.ViewClient;
import Advisor.Admin.ViewRequest.ViewRequest;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Admin implements Initializable {
    public AnchorPane MainPane;
    public HBox Box;
    static Stage temp;
    public JFXButton MailClientButton;
    public JFXButton ViewRequestButton;
    public RadioButton DarkModeButton;
    Stage parentStage;
    private double xOffset = 0;
    private double yOffset = 0;
    public static boolean darkMode;
    Scene scene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FileInputStream in = new FileInputStream("DarkMode.txt");
            DataInputStream DIS = new DataInputStream(in);
            darkMode = Boolean.parseBoolean(DIS.readUTF());

            scene = MainPane.getScene();
            if(darkMode){
                System.out.println("Dark Mode is set");
                DarkModeButton.setSelected(true);
                //scene.getStylesheets().add(getClass().getResource("/Advisor/Dark_Mode.css").toExternalForm());
                //MainPane.getStylesheets().add("Dark_Mode.css");
            }else{
                System.out.println("Dark Mode is not set");
                DarkModeButton.setSelected(false);
            }
            in.close();
            DIS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(Stage s){
        temp = s;
    }

    public void close(MouseEvent mouseEvent) {
        Stage stage = (Stage)MainPane.getScene().getWindow();
        stage.close();
        parentStage = temp;
        parentStage.show();
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage) MainPane.getScene().getWindow();
        stage.setFullScreen(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage stage = (Stage) MainPane.getScene().getWindow();
        stage.setIconified(true);
    }

    public void handleMail(ActionEvent actionEvent) {
        String location = "/Advisor/Admin/SendMail/SendMail.fxml";
        Stage parentStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            SendMail controller = loader.getController();
            controller.init(parentStage);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Add Client");
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

    public void handleViewClients(ActionEvent actionEvent) {
        String location = "/Advisor/Admin/ViewClient/ViewClient.fxml";
        parentStage  = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            ViewClient controller = loader.getController();
            controller.setStage(parentStage);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Add Client");
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

    public void handleAddClients(ActionEvent actionEvent) {
        String location = "/Advisor/Admin/AddClient/AddClient.fxml";
        Stage parentStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            AddClient controller = loader.getController();
            controller.init(parentStage);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Add Client");
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

    public void handleViewRequests(ActionEvent actionEvent) {
        String location = "/Advisor/Admin/ViewRequest/ViewRequest.fxml";
        Stage parentStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            ViewRequest controller = loader.getController();
            controller.init(parentStage);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("View Requests");
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

    public void applyDarkMode(ActionEvent actionEvent) {

        try {
            FileInputStream in = new FileInputStream("DarkMode.txt");
            DataInputStream DIS = new DataInputStream(in);
            darkMode = Boolean.parseBoolean(DIS.readUTF());
            in.close();
            DIS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        scene = MainPane.getScene();
        if(!darkMode){
            try {
               FileOutputStream out = new FileOutputStream("DarkMode.txt");
               DataOutputStream DOS = new DataOutputStream(out);
               DOS.writeUTF("true");
               out.close();
               DOS.close();
               DarkModeButton.setSelected(true);
               scene.getStylesheets().add(getClass().getResource("/Advisor/Dark_Mode.css").toExternalForm());
               MainPane.getStylesheets().add("Dark_Mode.css");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                FileOutputStream out = new FileOutputStream("DarkMode.txt");
                DataOutputStream DOS = new DataOutputStream(out);
                DOS.writeUTF("false");
                out.close();
                DOS.close();
                DarkModeButton.setSelected(false);
                scene.getStylesheets().remove(getClass().getResource("/Advisor/Dark_Mode.css").toExternalForm());
                MainPane.getStylesheets().remove("Dark_Mode.css");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleViewBills(ActionEvent actionEvent) {
        String location = "/Advisor/Admin/ViewBills/ViewBills.fxml";
        Stage parentStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            ViewBills controller = loader.getController();
            controller.init(parentStage);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("View Bills");
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

    public void MenuBarClose(ActionEvent actionEvent) {
        Stage stage = (Stage)MainPane.getScene().getWindow();
        stage.close();
        parentStage = temp;
        parentStage.show();
    }

    public void showAbout(ActionEvent actionEvent) {
        String location = "/Advisor/About/About.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("View Bills");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
