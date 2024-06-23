package cn.zhengyiyi;

import cn.zhengyiyi.service.BankService;
import cn.zhengyiyi.view.LoginUI;

public class Main {
    public static void main(String[] args) {
        BankService bankService = new BankService();
        LoginUI loginUI = new LoginUI(bankService);
        loginUI.start();
    }
}