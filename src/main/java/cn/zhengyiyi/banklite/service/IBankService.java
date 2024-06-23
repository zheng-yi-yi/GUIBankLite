package cn.zhengyiyi.banklite.service;

import cn.zhengyiyi.banklite.entity.BankCard;

import java.sql.SQLException;
import java.util.List;

public interface IBankService {
    /**
     * 创建新的银行卡
     *
     * @param bankName 银行名称
     * @param username 用户名
     * @param password 密码
     * @return 创建的BankCard对象，如果创建失败则返回null
     */
    BankCard createCard(String bankName, String username, String password);

    /**
     * 修改银行卡密码
     *
     * @param card 要修改密码的BankCard对象
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(BankCard card, String oldPassword, String newPassword);

    /**
     * 根据卡号获取银行卡信息
     *
     * @param cardNumber 卡号
     * @return BankCard对象，如果没有找到则返回null
     */
    BankCard getCard(String cardNumber);

    /**
     * 验证用户登录
     *
     * @param selectedBank 选择的银行名称
     * @param cardNumber 卡号
     * @param password 密码
     * @return 如果验证成功则返回true，否则返回false
     */
    boolean verifyLogin(String selectedBank, String cardNumber, String password);

    /**
     * 删除银行卡及其所有交易记录
     *
     * @param card 要删除的BankCard对象
     */
    void deleteCard(BankCard card);

    /**
     * 存款到银行卡
     *
     * @param card 要存款的BankCard对象
     * @param amount 存款金额
     */
    void deposit(BankCard card, double amount);

    /**
     * 从银行卡取款
     *
     * @param card 要取款的BankCard对象
     * @param amount 取款金额
     */
    void withdraw(BankCard card, double amount);

    /**
     * 转账到另一张银行卡
     *
     * @param card 要转账的BankCard对象
     * @param targetCardNumber 目标银行卡号
     * @param amount 转账金额
     * @throws SQLException 如果数据库操作出现异常
     */
    void transfer(BankCard card, String targetCardNumber, double amount) throws SQLException;

    /**
     * 获取银行卡的操作历史记录
     *
     * @param cardNumber 卡号
     * @return 操作历史记录列表
     */
    List<String> getOperations(String cardNumber);

    /**
     * 绑定关联卡
     *
     * @param card 要绑定的BankCard对象
     * @param relativeCardNumber 关联卡号
     */
    void bindRelativeCard(BankCard card, String relativeCardNumber);

    /**
     * 获取关联卡列表
     *
     * @param cardNumber 卡号
     * @return 关联卡的BankCard对象列表
     */
    List<BankCard> getRelativeCards(String cardNumber);

    /**
     * 获取关联卡号列表
     *
     * @param cardNumber 卡号
     * @return 关联卡的BankCard对象列表
     */
    List<BankCard> getRelativeCardNumbers(String cardNumber);
}
