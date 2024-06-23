package cn.zhengyiyi.view;

import cn.zhengyiyi.dao.entity.BankCard;
import cn.zhengyiyi.service.BankService;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class LoginUI {
    private final BankService bankService;
    private final JFrame frame;
    private static final String[] BANKS = {"中国工商银行", "中国建设银行", "中国农业银行"};

    public LoginUI(BankService bankService) {
        this.bankService = bankService;
        this.frame = new JFrame("基于图形界面的银行卡管理系统 - 登录界面");
        setGlobalFontSize();
    }

    private void setGlobalFontSize() {
        FontUIResource fontUIResource = new FontUIResource(new Font("SansSerif", Font.PLAIN, 30));
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }

    public void start() {
        frame.setSize(929, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel bankLabel = new JLabel("选择银行：");
        bankLabel.setBounds(150, 10, 240, 50);
        bankLabel.setFont(new Font(bankLabel.getFont().getName(), Font.PLAIN, 30));
        panel.add(bankLabel);

        JComboBox<String> bankComboBox = new JComboBox<>(BANKS);
        bankComboBox.setBounds(350, 10, 400, 50);
        bankComboBox.setFont(new Font(bankComboBox.getFont().getName(), Font.PLAIN, 30));
        panel.add(bankComboBox);

        JLabel userLabel = new JLabel("用户卡号：");
        userLabel.setBounds(150, 110, 240, 60);
        userLabel.setFont(new Font(userLabel.getFont().getName(), Font.PLAIN, 30));
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(350, 110, 400, 60);
        userText.setFont(new Font(userText.getFont().getName(), Font.PLAIN, 30));
        panel.add(userText);

        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setBounds(175, 200, 240, 60);
        passwordLabel.setFont(new Font(passwordLabel.getFont().getName(), Font.PLAIN, 30));
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(350, 200, 400, 60);
        passwordText.setFont(new Font(passwordText.getFont().getName(), Font.PLAIN, 30));
        panel.add(passwordText);

        JButton loginButton = new JButton("登录");
        loginButton.setBounds(595, 280, 150, 60);
        loginButton.setFont(new Font(loginButton.getFont().getName(), Font.PLAIN, 30));
        panel.add(loginButton);

        JButton registerButton = new JButton("注册");
        registerButton.setBounds(150, 280, 150, 60);
        registerButton.setFont(new Font(registerButton.getFont().getName(), Font.PLAIN, 30));
        panel.add(registerButton);

        loginButton.addActionListener(e -> {
            String selectedBank = (String) bankComboBox.getSelectedItem();
            if (selectedBank == null || selectedBank.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请选择银行！");
                return;
            }
            String cardNumber = userText.getText();
            if (cardNumber == null || cardNumber.isEmpty()) {
                JOptionPane.showMessageDialog(null, "卡号不能为空！");
                return;
            }
            String password = new String(passwordText.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "密码不能为空！");
                return;
            }
            if (bankService.verifyLogin(selectedBank, cardNumber, password)) {
                frame.dispose();
                new BankUI(bankService, selectedBank, cardNumber).start();
            } else {
                JOptionPane.showMessageDialog(null, "卡号或密码错误！");
            }
        });

        registerButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("请输入用户名：");
            if (username == null) return;

            String bankName = (String) JOptionPane.showInputDialog(null, "请选择银行名称：", "银行选择", JOptionPane.QUESTION_MESSAGE, null, BANKS, BANKS[0]);
            if (bankName == null) return;

            String password = getPasswordInput();
            if (password == null) return;

            BankCard newCard = bankService.createCard(bankName, username, password);
            JOptionPane.showMessageDialog(null, "尊敬的" + newCard.getUserName() + "用户，您的银行卡已创建成功！卡号为：" + newCard.getCardNumber());
            frame.dispose();
            new BankUI(bankService, bankName, newCard.getCardNumber()).start();
        });
    }

    private String getPasswordInput() {
        String password;
        while (true) {
            password = JOptionPane.showInputDialog("请输入6位数字密码");
            if (password == null) return null;
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "尊敬的用户，密码不能为空！");
            } else if (!password.matches("\\d{6}")) {
                JOptionPane.showMessageDialog(null, "密码无效。请确保密码是6位数字。");
            } else {
                break;
            }
        }
        return password;
    }
}