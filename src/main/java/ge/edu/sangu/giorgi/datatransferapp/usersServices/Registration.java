package ge.edu.sangu.giorgi.datatransferapp.usersServices;

import ge.edu.sangu.giorgi.datatransferapp.DBConnect;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Alert;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Error;
import ge.edu.sangu.giorgi.datatransferapp.alerts.Info;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Registration {

    public static boolean register(String name, String email, String password){
        JdbcDataSource dataSource = DBConnect.getObject();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO USERS(EMAIL, PASSWORD, FULL_NAME) VALUES(?,?,?)")
        ){
            if(PassedData.check(email)){
                new Alert(new Error()).show("user already exists. Please use your email and password to login");
                return false;
            }
            if(PassedData.isNotEmpty(name, email, password)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, name);
                preparedStatement.executeUpdate();
                new Alert(new Info()).show("Account created. Please use your Email and password for login");
                return true;
            }
            else {
                new Alert(new Error()).show("Please, enter all required information");
                return false;
            }
        } catch (SQLException e) {
            new Alert(new Error()).show(e.getMessage());
            return false;
        }
    }
}
