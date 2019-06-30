package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
                                    //server.subscribe(ClientHandler.this);
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
                            server.broadcast("From " + name + "to " + elements[2], name, elements[1]);

                        } else {
                            server.broadcast("Client: " + name + " " + str);
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
}
