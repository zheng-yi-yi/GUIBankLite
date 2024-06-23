package cn.zhengyiyi.dao.entity;

import lombok.Data;

@Data
public class BankCard {
    private final String cardNumber;
    private String password;
    private double balance;
    private final String userName;
    private final Bank bank;
    private String salt; // 存储盐值

    public BankCard(String cardNumber, String password, double balance, String userName, Bank bank, String salt) {
        this.cardNumber = cardNumber;
        this.password = password;
        this.balance = balance;
        this.userName = userName;
        this.bank = bank;
        this.salt = salt;
    }
}
