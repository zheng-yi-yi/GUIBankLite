package cn.zhengyiyi.banklite.dao.impl;

import cn.zhengyiyi.banklite.dao.IBankDao;
import cn.zhengyiyi.banklite.util.DatabaseUtil;
import cn.zhengyiyi.banklite.entity.Bank;
import cn.zhengyiyi.banklite.entity.BankCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDaoImpl implements IBankDao {
    private final DatabaseUtil databaseUtil;

    public BankDaoImpl() {
        this.databaseUtil = new DatabaseUtil();
    }

    public Bank getBankByName(String bankName) {
        try {
            String sql = "SELECT * FROM Banks WHERE bank_name = ?";
            try (Connection conn = databaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, bankName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int bankId = rs.getInt("bank_id");
                    return new Bank(bankId, bankName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bank getBankById(int bankId) {
        try {
            String sql = "SELECT * FROM Banks WHERE bank_id = ?";
            try (Connection conn = databaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, bankId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String bankName = rs.getString("bank_name");
                    return new Bank(bankId, bankName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertBankCard(BankCard bankCard) throws SQLException {
        String sql = "INSERT INTO BankCards (card_number, password, balance, user_name, bank_id, salt) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bankCard.getCardNumber());
            pstmt.setString(2, bankCard.getPassword());
            pstmt.setDouble(3, bankCard.getBalance());
            pstmt.setString(4, bankCard.getUserName());
            pstmt.setInt(5, bankCard.getBank().getBankId());
            pstmt.setString(6, bankCard.getSalt());
            pstmt.executeUpdate();
        }
    }

    public BankCard getCard(String cardNumber) {
        try {
            String sql = "SELECT * FROM BankCards WHERE card_number = ?";
            try (Connection conn = databaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cardNumber);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String password = rs.getString("password");
                    double balance = rs.getDouble("balance");
                    String username = rs.getString("user_name");
                    int bankId = rs.getInt("bank_id");
                    String salt = rs.getString("salt");
                    Bank bank = getBankById(bankId);
                    return new BankCard(cardNumber, password, balance, username, bank, salt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateBankCard(BankCard bankCard) throws SQLException {
        String sql = "UPDATE BankCards SET balance = ?, password = ?, salt = ? WHERE card_number = ?";
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, bankCard.getBalance());
            pstmt.setString(2, bankCard.getPassword());
            pstmt.setString(3, bankCard.getSalt());
            pstmt.setString(4, bankCard.getCardNumber());
            pstmt.executeUpdate();
        }
    }

    public void deleteBankCard(String cardNumber) throws SQLException {
        String deleteRelativeCardsSql = "DELETE FROM relative_cards WHERE card_number = ? OR relative_card_number = ?";
        String deleteAccountsSql = "DELETE FROM accounts WHERE card_number = ?";
        String deleteTransactionsSql = "DELETE FROM transactions WHERE from_card_number = ? OR to_card_number = ?";
        String deleteBankCardSql = "DELETE FROM BankCards WHERE card_number = ?";

        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement deleteRelativeCardsStmt = conn.prepareStatement(deleteRelativeCardsSql);
             PreparedStatement deleteAccountsStmt = conn.prepareStatement(deleteAccountsSql);
             PreparedStatement deleteTransactionsStmt = conn.prepareStatement(deleteTransactionsSql);

             PreparedStatement deleteBankCardStmt = conn.prepareStatement(deleteBankCardSql)){

            conn.setAutoCommit(false);

            // 删除亲属卡
            deleteRelativeCardsStmt.setString(1, cardNumber);
            deleteRelativeCardsStmt.setString(2, cardNumber);
            deleteRelativeCardsStmt.executeUpdate();

            // 删除账户（存款或取款）
            deleteAccountsStmt.setString(1, cardNumber);
            deleteAccountsStmt.executeUpdate();

            // 删除交易记录（转账或进账）
            deleteTransactionsStmt.setString(1, cardNumber);
            deleteTransactionsStmt.setString(2, cardNumber);
            deleteTransactionsStmt.executeUpdate();

            // 删除银行卡
            deleteBankCardStmt.setString(1, cardNumber);
            deleteBankCardStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = databaseUtil.getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    public void deleteTransactions(String cardNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE card_number = ?";
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.executeUpdate();
        }
    }

    public void bindRelativeCard(BankCard card, String relativeCardNumber) {
        try {
            String sql = "INSERT INTO relative_cards (card_number, relative_card_number) VALUES (?, ?)";
            try (Connection conn = databaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, card.getCardNumber());
                pstmt.setString(2, relativeCardNumber);
                pstmt.executeUpdate();

                pstmt.setString(1, relativeCardNumber);
                pstmt.setString(2, card.getCardNumber());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BankCard> getRelativeCards(String cardNumber) {
        List<BankCard> relativeCards = new ArrayList<>();
        try {
            String sql = "SELECT relative_card_number FROM relative_cards WHERE card_number = ?";
            try (Connection conn = databaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cardNumber);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String relativeCardNumber = rs.getString("relative_card_number");
                    relativeCards.add(getCard(relativeCardNumber));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return relativeCards;
    }

    // 检查卡号是否存在
    public boolean isCardNumberExists(String cardNumber) {
        String sql = "SELECT COUNT(*) FROM bankcards WHERE card_number = ?";
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Dao层
    public void insertDepositWithdrawalTransaction(String cardNumber, String operationType, double amount) throws SQLException {
        String sql = "INSERT INTO accounts (card_number, operation_type, amount) VALUES (?, ?, ?)";
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, operationType);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        }
    }

    public void insertTransferTransaction(String fromCardNumber, String toCardNumber, double amount, String operationType) throws SQLException {
        String sql = "INSERT INTO transactions (from_card_number, to_card_number, amount, operation_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fromCardNumber);
            pstmt.setString(2, toCardNumber);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, operationType); // 添加操作类型
            pstmt.executeUpdate();
        }
    }

    public List<String> getDepositWithdrawalTransactions(String cardNumber) {
        List<String> transactions = new ArrayList<>();
        try {
            String sql = "SELECT * FROM accounts WHERE card_number = ? ORDER BY operation_time DESC";
            try (Connection conn = databaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cardNumber);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String operationType = rs.getString("operation_type");
                    double amount = rs.getDouble("amount");
                    Timestamp transactionTime = rs.getTimestamp("operation_time");
                    transactions.add(operationType + ": " + amount + " at " + transactionTime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<String> getTransferTransactions(String cardNumber) {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT from_card_number, to_card_number, amount, operation_type, operation_time FROM transactions WHERE from_card_number = ? ORDER BY operation_time DESC";
        try (Connection conn = databaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String toCardNumber = rs.getString("to_card_number");
                double amount = rs.getDouble("amount");
                String operationType = rs.getString("operation_type");
                Timestamp operationTime = rs.getTimestamp("operation_time");
                String transaction = toCardNumber + ":" + amount + ":" + operationType + "at" + operationTime;
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

}