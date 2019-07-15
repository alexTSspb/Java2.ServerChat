package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import server.DataBaseHelper;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {
    @FXML TextField tfLogin;
    @FXML TextField tfPass1;
    @FXML TextField tfPass2;
    @FXML TextField tfNickName;
    @FXML Label lbWarning;
    @FXML Button tbButton;
    private static RegisterController instance;
    private  ChatController chatController;
    private Scene scene;
    public RegisterController(){ instance = this;}
    public static RegisterController getInstance()
    {
        return instance;
    }
    public void showResponse(String message) {
        Platform.runLater(() -> {
            this.lbWarning.setText(message);
            this.lbWarning.setTextFill(Color.web("#ff7e7e"));
        });
    }
    public void showLogin() {
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getClassLoader().getResource("./forms/LoginForm.fxml"));
            Parent window = null;
            try {
                window = (BorderPane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = Main.getStage();

            stage.setResizable(false);
            stage.setScene(new Scene(window));
            stage.setMinWidth(350);
            stage.setMinHeight(250);
            stage.setWidth(400);
            stage.setHeight(300);
            stage.centerOnScreen();
        });
    }

    public void onRegister(ActionEvent actionEvent) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getClassLoader().getResource("./forms/ChatForm.fxml"));
        Parent window = (BorderPane) fmxlLoader.load();
        this.chatController = fmxlLoader.<ChatController>getController();
        if(tfPass1.getText().equals(tfPass2.getText())&&tfPass1.getText()!=null)
        {
            ClientConnection clientConnection = new ClientConnection(tfLogin.getText(), tfPass1.getText(), this.chatController);
            clientConnection.setRegister(true);
            clientConnection.setNickRegister(tfNickName.getText());
            Thread x = new Thread(clientConnection);

            x.start();


            this.scene = new Scene(window);
        }
        else {
            showResponse("Повторите пароль правильно");
        }

    }
}
