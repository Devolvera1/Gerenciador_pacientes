package com.example.sistema_gestao_pacientes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://192.168.68.110:3306/clinica_medica";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MySQL não foi encontrado!");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT senha FROM usuarios WHERE username = ? AND status = 'ATIVO'";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPasswordBD = resultSet.getString("senha");

                    return BCrypt.checkpw(password, hashedPasswordBD);
                }
            }

        } catch (SQLException e) {
            System.err.println("ERRO DE CONEXÃO/SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
