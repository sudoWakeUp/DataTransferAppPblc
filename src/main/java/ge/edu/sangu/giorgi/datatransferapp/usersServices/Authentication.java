package ge.edu.sangu.giorgi.datatransferapp.usersServices;

import ge.edu.sangu.giorgi.datatransferapp.DBConnect;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Authentication {

    public static Map<String, String> login(String email, String password) {
        JdbcDataSource dataSource = DBConnect.getObject();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM USERS WHERE EMAIL = ? AND PASSWORD = ?");
        ) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> result = new HashMap<>();

            if(resultSet.next()){
                result.put("statusCode", "200");
                result.put("username", resultSet.getNString("EMAIL"));
                AuthData.setEMAIL(email);
            }
            else {
                result.put("statusCode", "404");
                result.put("username", null);
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
