package Advisor;

import Advisor.Admin.AddClient.AddClient;
import Advisor.Admin.Admin;
import Advisor.Client.ForgotPassword.ForgotPassword;
import Advisor.Client.UserPage.UserPage;
import Advisor.Database.DataBaseHandler;
import Advisor.Mail.MailUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController implements Initializable {
    public AnchorPane MainPane;
    public JFXButton it_statistics_button;
    public HBox Box;
    public Pane AdminPane;
    public Pane ClientPane;
    public Pane SignInPane;
    public Pane SignUpPane;
    public JFXTextField CustomerName;
    public JFXTextField CustomerEmail;
    public JFXTextField CustomerIncome;
    public JFXTextField CustomerPassword;
    public ChoiceBox<String> TypeOfAccount;
    public JFXButton SignUpButton;
    public JFXButton SignInButton;
    public Label ForgotPasswordLabel;
    public JFXButton DontHaveAnAccountButton;
    DataBaseHandler DBH;
    public JFXTextField UserEmail;
    public JFXPasswordField Password;
    private ObservableList<String> typeOfAccount = FXCollections.observableArrayList("Personal","Corporate","VIP");
    private AddClient.cust c;

    private double xOffset = 0;
    private double yOffset = 0;

    boolean darkMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SignInPane.toFront();
        ClientPane.toFront();
        DBH = DataBaseHandler.getInstance();
    }

    public void close(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void maximize(MouseEvent mouseEvent) {
        Stage stage = (Stage) MainPane.getScene().getWindow();
        stage.setFullScreen(true);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage stage = (Stage) MainPane.getScene().getWindow();
        stage.setIconified(true);
    }
//
//
//    public void show_it_statistics(ActionEvent actionEvent) {
//        try {
//            Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler "+"C:\\Users\\ashwi\\IdeaProjects\\ITAdvisor\\Files\\revenue-statistics-highlights-brochure.pdf");
//            System.out.println("Opening File...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//

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

    public void AdminMode(ScrollEvent scrollEvent) {
        AdminPane.toFront();
    }

    public void ClientMode(ScrollEvent scrollEvent) {
        ClientPane.toFront();
    }

    public void handleLogin(ActionEvent actionEvent) {
        SignInPane.toFront();
        String pass="";
        String search_cust_db = "select * from cust where email='"+UserEmail.getText()+"';";
        ResultSet rs = DBH.execQuery(search_cust_db);
        try {
            if(rs.next()){
                AtomicInteger trials = new AtomicInteger();
                int acc_id = rs.getInt("customers_id");
                String name = rs.getString("customers_name");
                String email = rs.getString("email");
                String search_passwords_trails = "select * from password_display where id='"+acc_id+"';";
                String search_pass_db = "select * from passwords where customer_id='"+acc_id+"';";
                rs = DBH.execQuery(search_pass_db);
                if(rs.next()){
                    byte [] decode = Base64.getDecoder().decode(rs.getString("password"));
                    pass = new String(decode);
                }
                rs = DBH.execQuery(search_passwords_trails);
                if(rs.next()){
                    trials.set(rs.getInt("trials"));
                }
                if(pass.equals(Password.getText())){
                    String location = "/Advisor/Client/UserPage/UserPage.fxml";
                    Stage parentStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
                        Parent parent =loader.load();
                        UserPage controller = loader.getController();
                        controller.Init(name,email,acc_id, parentStage);
                        Stage stage = new Stage(StageStyle.TRANSPARENT);
                        stage.setTitle("Add Client");
                        stage.setScene(new Scene(parent));
                        stage.show();
                        parentStage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Incorrect Password");
                    alert.setContentText("Try With Your Password");
                    alert.showAndWait();
                    UserEmail.getStyleClass().add("wrong-Credentials");
                    Password.getStyleClass().add("wrong-Credentials");
                    if(trials.get() > 2){
                        String Message = "Your Password for the account associated with the email address '"+email+"' is ----- : "+pass;
                        new Thread(()->{
                            MailUtil.sendMail(email,Message);
                            trials.set(0);
                            String update_trails = "update password_display set trials = "+trials+" where id = "+acc_id+";";
                            DBH.execUpdate(update_trails);
                        }).start();

                    }else{
                        trials.getAndIncrement();
                        String update_trails = "update password_display set trials = "+trials+" where id = "+acc_id+";";
                        DBH.execUpdate(update_trails);
                    }

                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Account Does Not  Exist");
                alert.setContentText("The account associated with email "+UserEmail+" does not exist");
                alert.showAndWait();
                UserEmail.getStyleClass().add("wrong-Credentials");
                Password.getStyleClass().add("wrong-Credentials");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleForgotPassword(MouseEvent mouseEvent) {
        String location = "/Advisor/Client/ForgotPassword/ForgotPassword.fxml";
        Stage parentStage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Parent parent =loader.load();
            ForgotPassword controller = loader.getController();
            controller.init(parentStage);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setTitle("Add Client");
            stage.setScene(new Scene(parent));
            stage.show();
            parentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSignUp(ActionEvent actionEvent) {
        String name = CustomerName.getText();
        String Income = CustomerIncome.getText();
        String email = CustomerEmail.getText();
        String password = CustomerPassword.getText();

        if(name.isEmpty() || email.isEmpty() || Income.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error Adding");
            alert.setContentText("Don't Leave any field empty");
            alert.showAndWait();
        }else{
            c = new AddClient.cust();
            c.setClientName(name);
            c.setClientEmail(email);
            c.setIncome(Double.parseDouble(Income));
            c.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
            String AccountType = TypeOfAccount.getValue();
            switch (AccountType){
                case "Personal":
                    c.setAccountType(1);
                    break;
                case "Corporate":
                    c.setAccountType(2);
                    break;
                case  "VIP":
                    c.setAccountType(3);
                    break;
                default:
                    break;
            }

            String checkEmailInRequests = "select * from cust_request where email='" + c.getClientEmail() + "'";
            String checkEmailInCustomers = "select * from cust where email='" + c.getClientEmail() + "'";
            ResultSet rs = DBH.execQuery(checkEmailInRequests);
            ResultSet rs1 = DBH.execQuery(checkEmailInCustomers);
            try {
                if (rs.next() || rs1.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error Adding");
                    alert.setContentText("An account with the give email " + c.getClientEmail() + " already exists");
                    alert.showAndWait();
                } else {
                    String submitRequest = "insert into cust_request values('" + c.getClientName() + "','" + c.getClientEmail() + "'," + c.getIncome() + "," + c.getAccountType() + ",'" + c.getPassword() + "');";
                    DBH.execAction(submitRequest);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Your request was submitted");
                alert.showAndWait();
                ClientPane.toFront();
                SignInPane.toFront();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void SignUpMode(ActionEvent actionEvent) {
        SignUpPane.toFront();
        TypeOfAccount.setValue("Personal");
        TypeOfAccount.setItems(typeOfAccount);
    }

    public void SignInMode(MouseEvent mouseEvent) {
        SignInPane.toFront();
    }

    public void loadAdminPage(ActionEvent actionEvent) {
        String location = "/Advisor/Admin/Admin.fxml";
        Stage parentStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            Admin.init(parentStage);
            Parent parent =loader.load();
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
}
