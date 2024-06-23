package cn.zhengyiyi.banklite.dao;

import cn.zhengyiyi.banklite.entity.Bank;
import cn.zhengyiyi.banklite.entity.BankCard;

import java.sql.SQLException;
import java.util.List;

public interface IBankDao {
    /**
     * 根据银行名称获取银行信息
     *
     * @param bankName 银行名称
     * @return Bank对象，如果没有找到则返回null
     */
    Bank getBankByName(String bankName);

    /**
     * 根据银行ID获取银行信息
     *
     * @param bankId 银行ID
     * @return Bank对象，如果没有找到则返回null
     */
    Bank getBankById(int bankId);

    /**
     * 插入新的银行卡信息
     *
     * @param bankCard 要插入的BankCard对象
     * @throws SQLException 如果数据库操作出现异常
     */
    void insertBankCard(BankCard bankCard) throws SQLException;

    /**
     * 根据卡号获取银行卡信息
     *
     * @param cardNumber 卡号
     * @return BankCard对象，如果没有找到则返回null
     */
    BankCard getCard(String cardNumber);

    /**
     * 更新银行卡信息
     *
     * @param bankCard 要更新的BankCard对象
     * @throws SQLException 如果数据库操作出现异常
     */
    void updateBankCard(BankCard bankCard) throws SQLException;

    /**
     * 根据卡号删除银行卡
     *
     * @param cardNumber 卡号
     * @throws SQLException 如果数据库操作出现异常
     */
    void deleteBankCard(String cardNumber) throws SQLException;

    /**
     * 绑定相关联的银行卡
     *
     * @param card 主要BankCard对象
     * @param relativeCardNumber 要绑定的关联卡号
     */
    void bindRelativeCard(BankCard card, String relativeCardNumber);

    /**
     * 获取指定卡号的关联银行卡列表
     *
     * @param cardNumber 卡号
     * @return 关联的BankCard对象列表
     */
    List<BankCard> getRelativeCards(String cardNumber);

    /**
     * 检查卡号是否存在
     *
     * @param cardNumber 卡号
     * @return 如果卡号存在则返回true，否则返回false
     */
    boolean isCardNumberExists(String cardNumber);

    /**
     * 插入存取款交易记录
     *
     * @param cardNumber 卡号
     * @param operationType 操作类型（存款或取款）
     * @param amount 金额
     * @throws SQLException 如果数据库操作出现异常
     */
    void insertDepositWithdrawalTransaction(String cardNumber, String operationType, double amount) throws SQLException;

    /**
     * 插入转账交易记录
     *
     * @param fromCardNumber 转出卡号
     * @param toCardNumber 转入卡号
     * @param amount 转账金额
     * @param operationType 操作类型
     * @throws SQLException 如果数据库操作出现异常
     */
    void insertTransferTransaction(String fromCardNumber, String toCardNumber, double amount, String operationType) throws SQLException;

    /**
     * 获取指定卡号的存取款交易记录
     *
     * @param cardNumber 卡号
     * @return 交易记录列表
     */
    List<String> getDepositWithdrawalTransactions(String cardNumber);

    /**
     * 获取指定卡号的转账交易记录
     *
     * @param cardNumber 卡号
     * @return 交易记录列表
     */
    List<String> getTransferTransactions(String cardNumber);
}
