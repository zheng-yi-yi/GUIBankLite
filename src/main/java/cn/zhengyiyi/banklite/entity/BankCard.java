package cn.zhengyiyi.banklite.entity;

import lombok.Data;

/**
 * 银行卡实体类。
 * 用于表示银行卡的基本信息和操作。
 */
@Data
public class BankCard {
    private final String cardNumber; // 银行卡号
    private String password; // 银行卡密码
    private double balance; // 银行卡余额
    private final String userName; // 持卡人姓名
    private final Bank bank; // 所属银行
    private String salt; // 密码加盐

    /**
     * 银行卡构造函数。
     */
    public BankCard(String cardNumber, String password, double balance, String userName, Bank bank, String salt) {
        this.cardNumber = cardNumber;
        this.password = password;
        this.balance = balance;
        this.userName = userName;
        this.bank = bank;
        this.salt = salt;
    }
}