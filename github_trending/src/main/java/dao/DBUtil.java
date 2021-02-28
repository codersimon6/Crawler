package dao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static String url = "jdbc:mysql://39.104.88.216:3306/java_github_crawler?useSSL=true&useUnicode=true&CharacterEncoding=utf-8";
    private static String USERNAME = "root";
    private static String PASSWORD = "Aa1234...";

    private static volatile DataSource dataSource = null;

    // 双重校验锁，保证线程安全
    private static DataSource getDateSource() {
        if (dataSource == null) {
            synchronized (DBUtil.class) {
                if (dataSource == null) {
                    MysqlDataSource mysqlDataSource = new MysqlDataSource();
                    mysqlDataSource.setServerName("39.104.88.216");
                    mysqlDataSource.setPort(3306);
                    mysqlDataSource.setUser("root");
                    mysqlDataSource.setPassword("Aa1234...");
                    mysqlDataSource.setDatabaseName("java_github_crawler");
                    mysqlDataSource.setUseSSL(false);
                    mysqlDataSource.setCharacterEncoding("utf8");
                    dataSource = mysqlDataSource;
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection() {
        try {
            return getDateSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection connection, PreparedStatement preparedStatement,
                             ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
