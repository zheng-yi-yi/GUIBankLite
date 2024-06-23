# GUIBankLite

## 介绍

GUIBankLite 是一个基于 Java 语言开发的简易银行卡管理系统。它包括数据库配置、工具类、实体类、DAO层、服务层以及视图层的实现，提供了银行卡的创建、存款、取款、密码修改、转账以及绑定亲属卡等功能，适合`Java`初学者练习。

## 如何使用？

首先，将项目导入到IDE（如IntelliJ IDEA）中。

**数据库脚本**：

在本地安装并配置MySQL数据库，并使用 `src/main/resources/sql`目录下的`GUIBankLite.sql`文件创建项目所需的表以及初始数据。

**依赖配置**：

在项目根目录的 `pom.xml` 文件中，你需要根据自己的MySQL版本来修改依赖版本：

```xml
<dependencies>
    <!-- 多数据源路由配置
         mysql 5.x mysql-connector-java 5.1.35
         mysql 8.x mysql-connector-java 8.0.22-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.22</version>
    </dependency>
</dependencies>
```

**数据库连接**：

通过`DatabaseConfig`类配置数据库连接信息，你需要根据需要修改自己的信息修改`DatabaseConfig`中的数据库用户名以及密码：

```java
public class DatabaseConfig {
    public static final String URL = "jdbc:mysql://localhost:3306/bank_lite_db?serverTimezone=UTC"; // 数据库URL
    public static final String USER = "root"; // 数据库用户名
    public static final String PASSWORD = "123456"; // 数据库密码
}
```

`DatabaseTool`类用于获取数据库连接：

```java
public class DatabaseUtil {
    // 获取数据库连接
    public Connection getConnection() throws SQLException {
        try {
            // 加载数据库驱动
            // 如果你使用的是 MySQL 8.0 以下版本，请使用 com.mysql.jdbc.Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 获取数据库连接
        return DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
    }
}
```

> 备注：
>
> JDBC 4.0 引入了一种服务提供者机制（SPI），允许`DriverManager`自动检测可用的驱动，只要驱动的`jar`文件在类路径（`classpath`）上。因此，对于**MySQL 8及更高版本的驱动**（`com.mysql.cj.jdbc.Driver`），**不需要在代码中显式加载驱动**。只需确保驱动的JAR文件在应用的类路径上即可。

最后，找到项目中的主类（`Main`），运行该类以启动图形用户界面。

## 贡献

如果你有任何建议或想要贡献代码，请通过GitHub提交Pull Request或创建Issue。

## 许可证

本项目采用MIT许可证，详情请见LICENSE文件。