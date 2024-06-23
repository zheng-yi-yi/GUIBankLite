package cn.zhengyiyi.dao.entity;

import lombok.Data;

@Data
public class Bank {
    private final int bankId;
    private final String bankName;

    public Bank(int bankId, String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
    }
}
