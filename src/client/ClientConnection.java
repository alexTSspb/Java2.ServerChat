package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ClientConnection implements Runnable{
    private static Socket socket;
    private DataInputStream in;
    private static DataOutputStream out;
    private ChatController chatController;
    private static String pass;
    private static String login;
    private static String nick;
    private Boolean register;

    ClientConnection(String tfLogintext, String tfPass, ChatController chatController){
        this.chatController = chatController;
        this.pass = tfPass;
        this.login = tfLogintext;
        this.register = false;
    }

    private void login() {sendMessage("/auth " + ClientConnection.login + " " + ClientConnection.pass); }
    private void register() {
        sendMessage("/register " + ClientConnection.login + " " + ClientConnection.pass);
    }
    public void setRegister(Boolean register) {
        this.register = register;
    }
    public static void logout() {
        nick = null;
        sendMessage("/end");
    }
    public static void delete() {
        nick = null;
        sendMessage("/delete");
    }

    @Override
    public void run() {
        try{
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println(login + " " + pass);
            //Посылаем
            if (this.register) {//Надо написать на серверной части ответ
                this.register();
            } else {
                this.login();
            }

            while (socket.isConnected())
            {

                String str = in.readUTF();
                if(nick!= null)
                {
                    if(str.startsWith("/end")){
                        this.chatController.showLogin();

                    }else if(str.startsWith("/users_list")){
                        String[] users = str.split(" ");
                        this.chatController.setUsersList(Arrays.copyOfRange(users,1,users.length));
                    }
                    else {
                        this.chatController.addMessage(str);
                    }
                }else{
                    if(str.startsWith("/authok"))
                    {
                        String[] elements = str.split(" ");
                        nick = elements[1];
                        this.chatController.setNickLabel(nick);
                        LoginController.getInstance().showScene();

                    }else if(str.startsWith("/auth_fail")){
                        String response = str.substring(10);
                        LoginController.getInstance().showResponse(response);

                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sendMessage(String message) {
        if (message.isEmpty()) {
            return;
        }

        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
