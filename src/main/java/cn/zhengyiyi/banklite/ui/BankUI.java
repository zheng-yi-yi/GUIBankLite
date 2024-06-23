package cn.zhengyiyi.banklite.ui;

import cn.zhengyiyi.banklite.service.IBankService;
import cn.zhengyiyi.banklite.util.EncryptionUtil;
import cn.zhengyiyi.banklite.util.TimeUtil;
import cn.zhengyiyi.banklite.util.UIUtil;
import cn.zhengyiyi.banklite.entity.BankCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class BankUI {
    private JLabel balanceLabel;
    private final IBankService bankServiceImpl;
    private final String bankName;
    private final String cardNumber;
    private JTextArea operationTextArea;

    public BankUI(IBankService bankServiceImpl, String bankName, String cardNumber) {
        this.bankServiceImpl = bankServiceImpl;
        this.bankName = bankName;
        this.cardNumber = cardNumber;
    }

    public void start() {
        UIUtil.setFontSize();
        BankCard card = bankServiceImpl.getCard(cardNumber);
        JFrame frame = new JFrame("基于图形界面的银行卡管理系统 - " + bankName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel userNameLabel = new JLabel("当前用户：" + card.getUserName());
        userNameLabel.setFont(new Font(userNameLabel.getFont().getName(), Font.PLAIN, 22));
        headerPanel.add(userNameLabel, BorderLayout.WEST);

        balanceLabel = new JLabel("所剩余额：" + card.getBalance() + "元");
        balanceLabel.setFont(new Font(balanceLabel.getFont().getName(), Font.PLAIN, 22));
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        panel.add(buttonPanel, BorderLayout.CENTER);

        JButton depositButton = new JButton("存款");
        JButton withdrawButton = new JButton("取款");
        JButton transferButton = new JButton("转账");
        JButton bindRelativeCardButton = new JButton("绑定亲属卡");
        JButton changePasswordButton = new JButton("更改密码");
        JButton logoutButton = new JButton("退出登录");
        JButton deleteAccountButton = new JButton("注销账户");

        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(bindRelativeCardButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(deleteAccountButton);

        depositButton.addActionListener(new BankActionListener(e -> deposit()));
        withdrawButton.addActionListener(new BankActionListener(e -> withdraw()));
        changePasswordButton.addActionListener(new BankActionListener(e -> changePassword()));
        logoutButton.addActionListener(new BankActionListener(e -> logout(frame)));
        deleteAccountButton.addActionListener(new BankActionListener(e -> deleteAccount(frame)));
        bindRelativeCardButton.addActionListener(new BankActionListener(e -> bindRelativeCard()));
        transferButton.addActionListener(new BankActionListener(e -> transfer()));

        operationTextArea = new JTextArea(20, 40);
        operationTextArea.setFont(new Font("宋体", Font.PLAIN, 23));
        operationTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(operationTextArea);
        panel.add(scrollPane, BorderLayout.SOUTH);

        operationTextArea.append("当前记录：\n");

        List<String> operations = bankServiceImpl.getOperations(cardNumber);
        for (String operation : operations) {
            operationTextArea.append(operation + "\n");
        }

        frame.setVisible(true);
    }

    private void deposit() {
        double amount;
        while (true) {
            String amountStr = JOptionPane.showInputDialog("请输入存款金额：");
            if (amountStr == null) {
                return;
            } else if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "存款金额不能为空！");
            } else {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) {
                    JOptionPane.showMessageDialog(null, "存款金额不能为负数！");
                } else {
                    break;
                }
            }
        }

        double finalAmount = amount;
        handleBankOperation(cardNumber, card -> {
            bankServiceImpl.deposit(card, finalAmount);
            JOptionPane.showMessageDialog(null, "尊敬的" + card.getUserName() + "用户，您已存款成功，当前余额为：" + card.getBalance() + "元。");
        });

        updateBalanceLabel();
        String transactionTime = TimeUtil.getCurrentTime();
        appendOperationText("[" + transactionTime + "] -> [" + bankServiceImpl.getCard(cardNumber).getUserName() + "]用户往 [" + cardNumber + "] 银行卡中存款 " + amount + " 元。");
    }

    private void withdraw() {
        double curBankCardBalance = bankServiceImpl.getCard(cardNumber).getBalance();
        double amount;
        while (true) {
            String amountStr = JOptionPane.showInputDialog("请输入取款金额：");
            if (amountStr == null) {
                return;
            } else if(amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "取款金额不能为空！");
            } else {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) {
                    JOptionPane.showMessageDialog(null, "取款金额不能为负数！");
                } else if (amount > curBankCardBalance) {
                    JOptionPane.showMessageDialog(null, "取款金额不能大于当前余额。您当前的余额为" + curBankCardBalance + "元，请重新输入取款金额。");
                } else {
                    break;
                }
            }
        }

        double finalAmount = amount;
        handleBankOperation(cardNumber, card -> {
            bankServiceImpl.withdraw(card, finalAmount);
            JOptionPane.showMessageDialog(null, "尊敬的" + card.getUserName() + "用户，您已取款成功，当前余额为：" + card.getBalance() + "元。");
        });

        updateBalanceLabel();
        String transactionTime = TimeUtil.getCurrentTime();
        appendOperationText("[" + transactionTime + "] -> [" + bankServiceImpl.getCard(cardNumber).getUserName() + "]用户从 [" + cardNumber + "] 银行卡中取款 " + amount + " 元。");
    }

    private void changePassword() {
        String password = bankServiceImpl.getCard(cardNumber).getPassword();
        if (!verifyPassword()) return;

        String newPassword = getPasswordInput(password);
        if (newPassword == null) return;

        handleBankOperation(cardNumber, card -> {
            bankServiceImpl.changePassword(card, password, newPassword);
            JOptionPane.showMessageDialog(null, "密码更改成功！");
        });
    }

    private void logout(JFrame frame) {
        frame.dispose();
        new LoginUI(bankServiceImpl).start();
    }

    private void deleteAccount(JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(null, "您确定要注销账户吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (!verifyPassword()) return;

            handleBankOperation(cardNumber, card -> {
                bankServiceImpl.deleteCard(card);
                JOptionPane.showMessageDialog(null, "账户已注销！");
            });
            frame.dispose();
            new LoginUI(bankServiceImpl).start();
        }
    }

    private void bindRelativeCard() {
        List<BankCard> relativeCards = bankServiceImpl.getRelativeCardNumbers(cardNumber);

        String relativeCardNumber;
        while(true) {
            relativeCardNumber = JOptionPane.showInputDialog("请输入要绑定的亲属卡的卡号：");
            if(relativeCardNumber == null) {
                return;
            } else if(relativeCardNumber.isEmpty()) {
                JOptionPane.showMessageDialog(null, "亲属卡号不能为空！");
            } else {
                String finalRelativeCardNumber = relativeCardNumber;
                if(relativeCards.stream().anyMatch(card -> card.getCardNumber().equals(finalRelativeCardNumber))) {
                    JOptionPane.showMessageDialog(null, "该亲属卡已经绑定过了！请重新输入。");
                } else {
                    break;
                }
            }
        }

        String targetCardUserName;
        while(true) {
            targetCardUserName = JOptionPane.showInputDialog("请输入目标卡持有人的名字：");
            if(targetCardUserName == null) {
                return;
            } else if(targetCardUserName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "目标卡持有人的名字不能为空！");
            } else if(!bankServiceImpl.getCard(relativeCardNumber).getUserName().equals(targetCardUserName)) {
                JOptionPane.showMessageDialog(null, "名字有误！请重新输入。");
            } else {
                break;
            }
        }

        String finalRelativeCardNumber = relativeCardNumber;
        handleBankOperation(cardNumber, card -> {
            bankServiceImpl.bindRelativeCard(card, finalRelativeCardNumber);
            JOptionPane.showMessageDialog(null, "亲属卡绑定成功！");
        });
    }

    private void transfer() {
        List<BankCard> relativeCards = bankServiceImpl.getRelativeCards(cardNumber);
        String[] options = new String[relativeCards.size() + 1];
        options[0] = "手动输入目标卡号";
        for (int i = 0; i < relativeCards.size(); i++) {
            options[i + 1] = relativeCards.get(i).getCardNumber() + " - " + relativeCards.get(i).getUserName();
        }

        String selectedOption = (String) JOptionPane.showInputDialog(null, "可指定转账卡号 or 转账给亲属卡：", "转账", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selectedOption == null) return;

        String targetCardNumber;
        String targetCardUserName;
        if (!selectedOption.equals(options[0])) {
            // 选择了亲属卡
            int separatorIndex = selectedOption.indexOf(" - ");
            targetCardNumber = selectedOption.substring(0, separatorIndex); // 亲属卡号
            targetCardUserName = selectedOption.substring(separatorIndex + 3);  // 亲属卡持有人的名字
        } else {
            targetCardNumber = JOptionPane.showInputDialog("请输入目标卡号：");
            if (targetCardNumber == null || targetCardNumber.isEmpty()) {
                JOptionPane.showMessageDialog(null, "目标卡号不能为空！");
                return;
            }

            while(true){
                targetCardUserName = JOptionPane.showInputDialog("请输入目标卡持有人的名字：");
                if (targetCardUserName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "目标卡持有人的名字不能为空！");
                    return;
                } else if(bankServiceImpl.getCard(targetCardNumber) == null) {
                    JOptionPane.showMessageDialog(null, "目标卡不存在！请重新输入。");
                } else if(!bankServiceImpl.getCard(targetCardNumber).getUserName().equals(targetCardUserName)) {
                    JOptionPane.showMessageDialog(null, "名字有误！请重新输入。");
                } else {
                    break;
                }
            }
        }

        double curBankCardBalance = bankServiceImpl.getCard(cardNumber).getBalance();
        double amount;
        while (true) {
            String amountStr = JOptionPane.showInputDialog("请输入转账金额：");
            if (amountStr == null) {
                return;
            } else if(amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "转账金额不能为空！");
            } else {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) {
                    JOptionPane.showMessageDialog(null, "转账金额不能为负数！");
                } else if (amount > curBankCardBalance) {
                    JOptionPane.showMessageDialog(null, "余额不足！您当前的余额为" + curBankCardBalance + "元，请重新输入转账金额。");
                } else {
                    break;
                }
            }
        }

        String finalTargetCardNumber = targetCardNumber;
        double finalAmount = amount;
        handleBankOperation(cardNumber, card -> {
            try {
                bankServiceImpl.transfer(card, finalTargetCardNumber, finalAmount);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            JOptionPane.showMessageDialog(null, "转账成功！");
        });

        updateBalanceLabel();
        String transactionTime = TimeUtil.getCurrentTime();
        appendOperationText("[" + transactionTime + "] -> [" + bankServiceImpl.getCard(cardNumber).getUserName() + "]用户转账 " + amount + " 元到 [" + targetCardNumber + "] 卡中。");
    }

    private String getPasswordInput(String oldPassword) {
        String password;
        while (true) {
            password = JOptionPane.showInputDialog("请输入新密码：");
            if (password == null) return null;
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "尊敬的用户，密码不能为空！");
            } else if (!password.matches("\\d{6}")) {
                JOptionPane.showMessageDialog(null, "密码无效。请确保密码是6位数字。");
            } else if (password.equals(oldPassword)) {
                JOptionPane.showMessageDialog(null, "新密码不能与旧密码相同！请重新输入新密码。");
            } else {
                break;
            }
        }
        return password;
    }

    private boolean verifyPassword() {
        String inputPassword;
        while (true) {
            inputPassword = JOptionPane.showInputDialog("请输入银行卡密码：");
            if (inputPassword == null) return false;
            if (inputPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "密码不能为空！");
                continue;
            }
            if (!inputPassword.matches("\\d{6}")) {
                JOptionPane.showMessageDialog(null, "密码无效。请确保密码是6位数字。");
                continue;
            }
            BankCard card = bankServiceImpl.getCard(cardNumber);
            String encryptedPassword = EncryptionUtil.md5WithSalt(inputPassword, card.getSalt());
            if (!encryptedPassword.equals(card.getPassword())) {
                JOptionPane.showMessageDialog(null, "您的密码错误！请重新输入密码。");
                continue;
            }
            break;
        }
        return true;
    }

    private void handleBankOperation(String cardNumber, Consumer<BankCard> operation) {
        BankCard card = bankServiceImpl.getCard(cardNumber);
        if (card == null) {
            JOptionPane.showMessageDialog(null, "银行卡不存在！");
        } else {
            operation.accept(card);
        }
    }

    private void appendOperationText(String text) {
        addTextWithFixedLine(operationTextArea, text);
    }

    private static void addTextWithFixedLine(JTextArea textArea, String newText) {
        // 移除固定文本
        textArea.setText(textArea.getText().replace("当前记录：\n", ""));

        // 在首行插入新文本
        textArea.insert(newText+"\n", 0);

        // 将固定文本添加回首行
        textArea.insert("当前记录：\n", 0);
    }


    private static class BankActionListener implements ActionListener {
        private final Consumer<ActionEvent> action;

        public BankActionListener(Consumer<ActionEvent> action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            action.accept(e);
        }
    }

    private void updateBalanceLabel() {
        BankCard card = bankServiceImpl.getCard(cardNumber);
        balanceLabel.setText("所剩余额：" + card.getBalance() + "元");
    }
}