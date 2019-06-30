package server;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseHelper
{

    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            // обращение к драйверу
            Class.forName("org.sqlite.JDBC");
            // установка подключения
            connection = DriverManager.getConnection("jdbc:sqlite:User.db");
            // создание Statement для возможности оправки запросов
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void disconnect() {
        try {
            // закрываем соединение
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void addNewByLogPas(String login, String pass) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO Clients (login, password) VALUES (?,?)");
        ps.setString(1, login);
        ps.setString(1, pass);
        ps.execute();
    }
    public static String getNickByLoginAndPass(String login, String pass) {
        // формирование запроса
        String sql = String.format("SELECT nickname FROM Clients where login = '%s' and password = '%s'", login, pass);

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

}

