package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatController {
    @FXML private TextArea taChat;
    @FXML private TextField tfMessage;
    @FXML private TextArea taUsers;
    @FXML private Label lNick;


    private ChatController chatController;
    private Scene scene;

    public void sendButtonAction(ActionEvent actionEvent) {
        ClientConnection.sendMessage(this.tfMessage.getText());
        this.tfMessage.clear();
    }
    public void setNickLabel(String nick) {
        this.lNick.setText("Чат: " + nick);
    }
    public void setUsersList(String[] users)
    {
        taUsers.setText("");
        for(int i = 0;i<users.length;i++)
        {
            taUsers.appendText(users[i] + "\n");
        }

    }

    public void addMessage(String message) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.taChat.setStyle("-fx-font-size:15");
        this.taChat.appendText(dtf.format(now) + " : " + message + "\n");

        this.taChat.selectPositionCaret(this.taChat.getLength());
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

    public void onExit(ActionEvent actionEvent) {
        this.showLogin();
        ClientConnection.logout();
    }
}