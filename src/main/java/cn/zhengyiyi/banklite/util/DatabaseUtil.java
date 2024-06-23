package cn.zhengyiyi.banklite.util;

import cn.zhengyiyi.banklite.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库工具类。
 * 用于管理数据库连接。
 */
public class DatabaseUtil {
    /**
     * 获取数据库连接。
     *
     * @return 数据库连接对象
     * @throws SQLException 如果连接数据库时发生错误
     */
    public Connection getConnection() throws SQLException {
        try {
            // 加载数据库驱动
            // 如果你使用的是 MySQL 8.0 以下版本，请使用 com.mysql.jdbc.Driver。并修改pom.xml文件中的mysql-connector-java版本为5.1.35
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 获取数据库连接
        return DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
    }
}