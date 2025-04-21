package ge.edu.sangu.giorgi.datatransferapp;

import org.h2.jdbcx.JdbcDataSource;

public class DBConnect {
    private static JdbcDataSource dataSource;

    private DBConnect(){}

    public static JdbcDataSource getObject(){
        if(dataSource == null){
            dataSource = new JdbcDataSource();
        }
        return dataSource;
    }

    public static void connect(){
        getObject().setURL("jdbc:h2:file:~/dataTransferAppDb");
        getObject().setUser("sa");
        getObject().setPassword("");
    }
}
