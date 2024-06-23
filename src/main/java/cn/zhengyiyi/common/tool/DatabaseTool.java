package cn.zhengyiyi.common.tool;

import cn.zhengyiyi.common.config.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTool {
    public Connection getConnection() throws SQLException {
        try {
            // 加载数据库驱动
            // 如果你使用的是 MySQL 8.0 以下版本，请使用 com.mysql.jdbc.Driver。并修改pom.xml文件中的mysql-connector-java版本为5.1.35
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 获取数据库连接
        return DriverManager.getConnection(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
    }
}