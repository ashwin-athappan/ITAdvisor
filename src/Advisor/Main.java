package Advisor;

import Advisor.Utilities.Utilities;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        final String Icon = "/Advisor/UI_Images/icons8_money_500px.png";
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Utilities.setImage(primaryStage,Icon);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
