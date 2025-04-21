package ge.edu.sangu.giorgi.datatransferapp.usersServices;

import ge.edu.sangu.giorgi.datatransferapp.DBConnect;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Info;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthData {
    private static String EMAIL = null;

    public static List<User> getUserData(){

        if (EMAIL == null){
            new Alert(new Info()).show("Couldn't find any data");
            return null;
        }

        List<User> users = new ArrayList<>();
        JdbcDataSource dataSource = DBConnect.getObject();
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM USERS WHERE EMAIL = ?")
            )
        {
            preparedStatement.setString(1, EMAIL);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                User user = new User();
                user.setEmail(resultSet.getString("EMAIL"));
                user.setFullName(resultSet.getString("FULL_NAME"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getEMAIL() {
        return EMAIL;
    }

    public static void setEMAIL(String EMAIL) {
        AuthData.EMAIL = EMAIL;
    }
}
