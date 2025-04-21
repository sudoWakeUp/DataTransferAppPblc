package ge.edu.sangu.giorgi.datatransferapp.usersServices;

import ge.edu.sangu.giorgi.datatransferapp.DBConnect;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PassedData {

    /**
     * checks if user already exists
     *
     * @param email user email
     * @return TRUE if user exists, else returns FALSE
     */
    public static boolean check(String email){
        JdbcDataSource dataSource = DBConnect.getObject();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM USERS WHERE EMAIL = ?")
        ) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param name Fullname of user
     * @param email Email of user
     * @param password password of user account
     * @return True if all parameters are non-null and non-blank
     */
    public static boolean isNotEmpty(String name, String email, String password){
        return name != null && !name.isBlank() &&
                email != null && !email.isBlank() &&
                password != null && !password.isBlank();
    }
}
