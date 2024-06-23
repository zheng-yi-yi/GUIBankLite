package cn.zhengyiyi.common.tool;

import cn.zhengyiyi.dao.BankDao;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CardGenTool {
    private static final Random random = new Random();
    private static final Set<String> generatedCardNumbers = new HashSet<>();

    // 生成16位随机卡号
    public static String generateCardNumber(BankDao bankDao) {
        String cardNumber;
        do {
            cardNumber = generateRandomNumber();
        } while (generatedCardNumbers.contains(cardNumber) || bankDao.isCardNumberExists(cardNumber));
        generatedCardNumbers.add(cardNumber);
        return cardNumber;
    }

    // 生成16位随机数字字符串
    private static String generateRandomNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
