package Advisor.Utilities;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Utilities {
    public static void setImage(Stage stage, String IconLoc){
        stage.getIcons().add(new Image(IconLoc));
    }
}
