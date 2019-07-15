package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server {
    private final int PORT = 8189;
    private ArrayList<ClientHandler> clients;
    private ServerSocket server;
    private Socket socket;
    public Server() throws SQLException {

        clients = new ArrayList<>();
        DataBaseHelper.connect();
        DataBaseHelper.createTable();
        System.out.println(DataBaseHelper.getNickByLoginAndPass("login1","pass1"));
        try{
            server = new ServerSocket(PORT);
            System.out.println("Server is running...");
            while(true)
            {
                socket = server.accept();
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
                server.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            DataBaseHelper.disconnect();
        }
    }

    public void subscribe(ClientHandler c)
    {
        clients.add(c);
    }
    public void unsubscribe(ClientHandler c)
    {
        clients.remove(c);
    }
    public void broadcast(String str)
    {
        for(ClientHandler c:clients)
        {
            c.sendMSG(str);
        }
    }
    public synchronized void broadcast(String msg, String... nicks) {
        int countCurrent = 0;
        int countAll = nicks.length;

        for (ClientHandler c: clients) {
            for (String nick : nicks) {
                if (c.getName().equals(nick)) {
                    c.sendMSG(msg);
                    //проверка
                    if (++countCurrent == countAll) {
                        return;
                    }
                }
            }
        }
    }
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler c: clients) {
            if (c.getName().equals(nick)) {
                return true;
            }
        }

        return false;
    }
    public void broadcastUSERS(){
        String str = "/users_list";
        StringBuffer stringBuffer = new StringBuffer(str);
        for(ClientHandler c :clients)
        {
            stringBuffer.append(" " + c.getName());
        }
        for(ClientHandler c : clients)
        {
            c.sendMSG(stringBuffer.toString());
        }
    }
}
