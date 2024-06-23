package cn.zhengyiyi.banklite.config;

/**
 * 数据库配置类。
 * 用于存储数据库连接的基本信息，包括URL、用户名和密码。
 */
public class DatabaseConfig {
    public static final String URL = "jdbc:mysql://localhost:3306/bank_lite_db?serverTimezone=UTC"; // 数据库URL
    public static final String USER = "root"; // 数据库用户名
    public static final String PASSWORD = "123456"; // 数据库密码
}