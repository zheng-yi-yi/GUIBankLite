package cn.zhengyiyi.banklite;

import cn.zhengyiyi.banklite.service.impl.BankServiceImpl;
import cn.zhengyiyi.banklite.ui.LoginUI;

/**
 * 本应用的主入口。
 */
public class Main {
    public static void main(String[] args) {
        // 创建登录界面实例，并将银行服务实例传递给它
        LoginUI loginUI = new LoginUI(new BankServiceImpl());
        // 启动登录界面
        loginUI.start();
    }
}