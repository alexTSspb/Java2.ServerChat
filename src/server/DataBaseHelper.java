package server;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseHelper
{

    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Users.db");
            stmt = connection.createStatement();
            System.out.println("База подключена");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void disconnect() {
        try {
            connection.close();
            System.out.println("База отключена");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void createTable() throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS 'Humans'" +
                "('id' INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT," +
                "'login' text UNIQUE," +
                "'password' text," +
                "'nickname' text UNIQUE " +
                ")");
        System.out.println("Таблица создана или уже существует");
    }
    public static void addToDB(String log, String pass, String nick)  {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO 'Humans'" +
                    "(login,password,nickname) " +
                    "VALUES (?,?,?)");

            ps.setString(1, log);
            ps.setString(2, pass);
            ps.setString(3, nick);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    Database History
    1.id integer
    2. dateTime
    3.from
    4.to
    5.message
     */
    public static void addHistoryToDB(String datetime, String from, String to,String message)
    {
        PreparedStatement ps=null;
        try{
            ps = connection.prepareStatement("INSERT INTO 'History'" +
                    "(dateTime, sender,receiver,message) " +
                    "VALUES (?,?,?,?)");
            ps.setString(1, datetime);
            ps.setString(2, from);
            ps.setString(3, to);
            ps.setString(4, message);
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    Получить записи из БД, если в поле TO стоит ALL, то вставляем, если другое значение, то делаем проверку на ник, чтобы личное сообщение
    не всплыло из базы просто так
     */
    public static ArrayList<String> receiveFromDB(String nick)
    {
        ArrayList<String> arrayListHistory = new ArrayList<>();
        String sql = String.format("SELECT * FROM History where receiver = '%s' Or receiver = '%s'","ALL",nick);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                arrayListHistory.add("/beginHistoryStr"+rs.getString("dateTime")+"/`"+rs.getString("sender")+"/`" +
                        rs.getString("receiver")+"/`"+rs.getString("message"));
            }
            return arrayListHistory;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getNickByLoginAndPass(String login, String pass) {
        // формирование запроса
        String sql = String.format("SELECT nickname FROM Humans where login = '%s' and password = '%s'", login, pass);
        try {
            // оправка запроса и получение ответа
            ResultSet rs = stmt.executeQuery(sql);
            // если есть строка возвращаем результат если нет то вернеться null
            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void deleteFromDB(String nick) throws SQLException {
        String sql = String.format("DELETE FROM Humans WHERE nickname = '%s'",nick);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.execute();
    }
    public static void updateNick(String exNick, String newNick) throws SQLException {
        String sql = String.format("UPDATE Humans SET nickname = '%s' WHERE nickname = '%s' ", newNick,exNick);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.execute();
    }
    public static boolean isUsed(String where, String value) {

        String sql = null;
        if (where.equalsIgnoreCase("login")) {
            sql = String.format("SELECT nickname FROM Humans where login = '%s'", value);
        } else if (where.equalsIgnoreCase("nickname")) {
            //else{
            sql = String.format("SELECT login FROM Humans where nickname = '%s'", value);
        }
        try {
            // оправка запроса и получение ответа
            ResultSet rs = stmt.executeQuery(sql);
            // если есть строка возвращаем результат если нет то вернеться null
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}

