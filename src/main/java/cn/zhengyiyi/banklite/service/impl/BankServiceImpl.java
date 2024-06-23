package cn.zhengyiyi.banklite.service.impl;

import cn.zhengyiyi.banklite.dao.IBankDao;
import cn.zhengyiyi.banklite.service.IBankService;
import cn.zhengyiyi.banklite.util.CardGenerator;
import cn.zhengyiyi.banklite.util.EncryptionUtil;
import cn.zhengyiyi.banklite.util.TimeUtil;
import cn.zhengyiyi.banklite.dao.impl.BankDaoImpl;
import cn.zhengyiyi.banklite.entity.Bank;
import cn.zhengyiyi.banklite.entity.BankCard;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BankServiceImpl implements IBankService {
    private final IBankDao bankDao;

    public BankServiceImpl() {
        this.bankDao = new BankDaoImpl();
    }

    public BankCard createCard(String bankName, String username, String password) {
        try {
            Bank bank = bankDao.getBankByName(bankName);
            if (bank == null) {
                System.out.println("Error: Bank does not exist.");
                return null;
            }

            String cardNumber = CardGenerator.generateCardNumber(bankDao); // 生成16位不重复的银行卡号
            double balance = 0.0;
            String salt = EncryptionUtil.generateSalt();
            String encryptedPassword = EncryptionUtil.md5WithSalt(password, salt);
            BankCard bankCard = new BankCard(cardNumber, encryptedPassword, balance, username, bank, salt);

            bankDao.insertBankCard(bankCard);
            return bankCard;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void changePassword(BankCard card, String oldPassword, String newPassword) {
        try {
            String encryptedOldPassword = EncryptionUtil.md5WithSalt(oldPassword, card.getSalt());
            if (!card.getPassword().equals(encryptedOldPassword)) {
                System.out.println("Error: Incorrect old password.");
                return;
            }

            String newSalt = EncryptionUtil.generateSalt();
            String encryptedNewPassword = EncryptionUtil.md5WithSalt(newPassword, newSalt);

            card.setPassword(encryptedNewPassword);
            card.setSalt(newSalt);
            bankDao.updateBankCard(card);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BankCard getCard(String cardNumber) {
        return bankDao.getCard(cardNumber);
    }

    public boolean verifyLogin(String selectedBank, String cardNumber, String password) {
        BankCard card = getCard(cardNumber);
        if (card == null || !card.getBank().getBankName().equals(selectedBank)) {
            return false;
        }

        String encryptedPassword = EncryptionUtil.md5WithSalt(password, card.getSalt());
        return card.getPassword().equals(encryptedPassword);
    }

    public void deleteCard(BankCard card) {
        try {
            bankDao.deleteBankCard(card.getCardNumber());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deposit(BankCard card, double amount) {
        try {
            double newBalance = card.getBalance() + amount;
            card.setBalance(newBalance);
            bankDao.updateBankCard(card);
            recordTransaction(card.getCardNumber(), "存款", amount, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void withdraw(BankCard card, double amount) {
        try {
            if (card.getBalance() < amount) {
                System.out.println("Error: Insufficient balance.");
                return;
            }

            double newBalance = card.getBalance() - amount;
            card.setBalance(newBalance);
            bankDao.updateBankCard(card);
            recordTransaction(card.getCardNumber(), "取款", amount, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void recordTransaction(String cardNumber, String operationType, double amount, String targetCardNumber) {
        try {
            if (operationType.equals("存款") || operationType.equals("取款")) {
                bankDao.insertDepositWithdrawalTransaction(cardNumber, operationType, amount);
            } else if (operationType.equals("转账")) {
                bankDao.insertTransferTransaction(cardNumber, targetCardNumber, amount, "转账");
                bankDao.insertTransferTransaction(targetCardNumber, cardNumber, amount, "收款");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transfer(BankCard card, String targetCardNumber, double amount) throws SQLException {
        BankCard targetCard = getCard(targetCardNumber);
        card.setBalance(card.getBalance() - amount);
        targetCard.setBalance(targetCard.getBalance() + amount);
        bankDao.updateBankCard(card);
        bankDao.updateBankCard(targetCard);
        recordTransaction(card.getCardNumber(), "转账", amount, targetCardNumber);
    }

    public List<String> getOperations(String cardNumber) {
        String userName = getCard(cardNumber).getUserName();
        List<String> operations = new ArrayList<>();
        // 获取存款和取款记录
        List<String> depositWithdrawalTransactions = bankDao.getDepositWithdrawalTransactions(cardNumber);
        if(depositWithdrawalTransactions.size() == 0) {
            operations.add("暂无历史存款或取款记录。");
        }else {
            operations.add("历史存款或取款记录如下：");
            for (String transaction : depositWithdrawalTransactions) {
                String[] parts = transaction.split(" at ");
                String[] operationParts = parts[0].split(": ");
                if (operationParts.length >= 2) {
                    String operationType = operationParts[0];
                    double amount = Double.parseDouble(operationParts[1]);
                    String operation = null;
                    if (operationType.equals("存款")) {
                        operation = "[" + TimeUtil.formatTimestamp(Timestamp.valueOf(parts[1])) + "] -> [" + userName + "]用户往 [" + cardNumber + "] 银行卡中存款 " + amount + " 元。";
                    } else if(operationType.equals("取款")) {
                        operation = "[" + TimeUtil.formatTimestamp(Timestamp.valueOf(parts[1])) + "] -> [" + userName + "]用户从 [" + cardNumber + "] 银行卡中取款 " + amount + " 元。";
                    }
                    operations.add(operation);
                }
            }
        }

        List<String> transferTransactions = bankDao.getTransferTransactions(cardNumber);
        if(transferTransactions.size() == 0) {
            operations.add("暂无历史转账或进账记录。");
        }else {
            operations.add("历史转账或进账记录如下：");
            for (String transaction : transferTransactions) {
                String[] parts = transaction.split("at");
                String operationTime = TimeUtil.formatTimestamp(Timestamp.valueOf(parts[1]));
                parts = parts[0].split(":");
                String toCardNumber = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String operationType = parts[2];
                String operation;
                if(operationType.equals("转账")) {
                    operation = "[" + operationTime + "] -> [" + userName + "]用户转账 " + amount + " 元到 [" + toCardNumber + "]。";
                } else {
                    operation = "[" + operationTime + "] -> [" + userName + "]用户收到来自 [" + toCardNumber + "] 的转账 " + amount + " 元。";
                }
                operations.add(operation);
            }
        }

        return operations;
    }

    public void bindRelativeCard(BankCard card, String relativeCardNumber) {
        bankDao.bindRelativeCard(card, relativeCardNumber);
    }

    public List<BankCard> getRelativeCards(String cardNumber) {
        return bankDao.getRelativeCards(cardNumber);
    }

    public List<BankCard> getRelativeCardNumbers(String cardNumber) {
        return getRelativeCards(cardNumber);
    }
}
