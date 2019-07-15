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
import server.DataBaseHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ChatController {
    @FXML private TextArea taChat;
    @FXML private TextField tfMessage;
    @FXML private TextArea taUsers;
    @FXML private Label lNick;
    ArrayList<String> arrayHistory ;
    ArrayList<String> arrHisOut ;


    private ChatController chatController;
    private Scene scene;
    public
    String strHistory;

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

    public void loadHistoryFromDB(ActionEvent actionEvent) {
        arrayHistory = new ArrayList<>();
        String[] s = lNick.getText().split(" ");
        System.out.println(s[1]);
        ClientConnection.sendMessage("/history"+" "+s[1]);

        //arrayHistory = DataBaseHelper.receiveFromDB(s[1]);
      //  System.out.println("ЧАТКОНТРОЛЛЕР"+strHistory);
      //  parseStringForHistory(strHistory);
        //this.showHistory();
    }
    public void createHistory(String str)
    {
        this.strHistory = str;
        parseStringForHistory(strHistory);
        System.out.println("Перевод на экран  " + arrHisOut);
        showHistory();

    }
    public void showHistory(){
        Platform.runLater(()->{
            taChat.setText("");
            for(int i = 0; i < arrHisOut.size();i++) {
                taChat.appendText(arrHisOut.get(i)+"\n");
                //taChat.setText();
            }
        });
    }

    public void parseStringForHistory(String strHistory){
        System.out.println(strHistory);
        String[] elements = strHistory.split("/beginHistoryStr");
        ArrayList<String> stringForHistory = new ArrayList<>();
        for(int i=1; i<elements.length;i++)
        {
            String[] elementsByValue;
            elementsByValue = (elements[i]).split("/`");
            //stringForHistory.add()
            if(elementsByValue[2].equalsIgnoreCase("ALL"))
            {
                stringForHistory.add(elementsByValue[0].substring(5,19) + " " + elementsByValue[1] + ": " + elementsByValue[3]);
            }else {
                stringForHistory.add(elementsByValue[0].substring(5,19) + " " + elementsByValue[1] + " to " + elementsByValue[2] +": " + elementsByValue[3]);
            }

        }
        arrHisOut = stringForHistory;
        System.out.println(stringForHistory);
    }
}