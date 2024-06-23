package cn.zhengyiyi.banklite.entity;

import lombok.Data;

/**
 * 银行实体类。
 * 用于表示银行的基本信息。
 */
@Data
public class Bank {
    private final int bankId; // 银行ID
    private final String bankName; // 银行名称

    /**
     * 银行构造函数。
     */
    public Bank(int bankId, String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
    }
}