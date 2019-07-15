package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

public class ClientHandler {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(Socket socket, Server server) {
        try{
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";

        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(()->{
            try {
                while (true) {
                    //цикл авторизации
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth")) {
                            String[] tokens = str.split(" ");
                            // Вытаскиваем данные из БД
                            String newNick = DataBaseHelper.getNickByLoginAndPass(tokens[1], tokens[2]);
                            if (newNick != null) {
                                if(!server.isNickBusy(newNick)) {
                                    // отправляем сообщение об успешной авторизации
                                    name = newNick;
                                    sendMSG("/authok " + name);
                                    System.out.println("/auth_ok_send");
                                    setAuthorized(true);
                                    break;
                                }else{
                                    sendMSG("/auth_fail Пользователь уже зашел");
                                }
                            } else {
                                sendMSG("/auth_fail Неверный логин/пароль!");
                            }
                        }else if(str.startsWith("/register"))
                        {
                            String[] elements = str.split(" ");
                            String log = elements[1];
                            String nick = elements[3];
                            if(!DataBaseHelper.isUsed("login",log)&&!DataBaseHelper.isUsed("nickname",nick)) {
                                DataBaseHelper.addToDB(elements[1], elements[2], elements[3]);
                                sendMSG("/register_ok"+" "+nick);
                            }else {
                                if(DataBaseHelper.isUsed("login",log)){
                                    String msg = "Логин уже используется";
                                    sendMSG("/register_fail"+" "+msg);
                                }else{
                                    String msg = "Ник уже используется";
                                    sendMSG("/register_fail"+" "+msg);
                                }

                            }


                        }

                    }
                    // блок для отправки сообщений
                    while (true) {
                        String str = in.readUTF();
                        if (str.equalsIgnoreCase("/end")) {
                            server.broadcast(str, name);
                            break;
                        } else if (str.startsWith("/w")) {
                            String[] elements = str.split(" ");
                            int sizeMess = elements[0].length()+elements[1].length() + 1;
                            String messStr = str.substring(sizeMess);
                            server.broadcast("From " + name + "to " + elements[1]+messStr, name, elements[1]);
                            DataBaseHelper.addHistoryToDB(timestamp(),name,elements[1],messStr);

                        }else if(str.startsWith("/history")){
                            String[] elements = str.split(" ");
                            ArrayList<String> arrStr = new ArrayList<>();
                            StringBuffer stringBuffer  = new StringBuffer();
                            arrStr = DataBaseHelper.receiveFromDB(elements[1]);
                            for(int i = 0; i < arrStr.size();i++)
                            {
                                stringBuffer.append(arrStr.get(i));
                            }
                            System.out.println(stringBuffer.toString());
                            String strFromHistory = stringBuffer.toString();
                            sendMSG("/history_ok" + strFromHistory);

                        }
                        else {
                            server.broadcast("Client: " + name + " " + str);
                            DataBaseHelper.addHistoryToDB(timestamp(),name,"ALL",str);

                        }
                    }
                    setAuthorized(false);

                }
            }catch (IOException e)
            {

            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setAuthorized(false);
            }

        }).start();

    }
    private void setAuthorized(boolean isAuth)
    {
        if(isAuth)
        {
            server.subscribe(this);
            if(!name.isEmpty())
            {
                server.broadcast("Пользователь " + name + " зашел в чат");
                server.broadcastUSERS();
            }
        }
        else{
            server.unsubscribe(this);
            if(!name.isEmpty())
            {
                server.broadcast("Пользователь " + name + " вышел из чат");
                server.broadcastUSERS();
            }

        }
    }

    public void sendMSG(String str) {
        try{
            out.writeUTF(str);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String timestamp() {
        Calendar calendar = Calendar.getInstance();
        java.sql.Timestamp timeStamp = new java.sql.Timestamp(calendar.getTime().getTime());
        return timeStamp.toString();
    }
}
