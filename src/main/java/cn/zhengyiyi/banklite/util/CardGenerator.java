package cn.zhengyiyi.banklite.util;

import cn.zhengyiyi.banklite.dao.IBankDao;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 银行卡号生成工具类。
 * 用于生成唯一的银行卡号。
 */
public class CardGenerator {
    private static final Random random = new Random(); // 随机数生成器
    private static final Set<String> generatedCardNumbers = new HashSet<>(); // 已生成的卡号集合，用于确保唯一性

    /**
     * 生成16位随机卡号。
     * 生成的卡号将不会与已存在的卡号重复。
     *
     * @param bankDao 银行数据访问对象，用于检查卡号是否已存在
     * @return 生成的唯一卡号
     */
    public static String generateCardNumber(IBankDao bankDao) {
        String cardNumber;
        do {
            cardNumber = generateRandomNumber(); // 生成随机卡号
        } while (generatedCardNumbers.contains(cardNumber) || bankDao.isCardNumberExists(cardNumber)); // 检查卡号是否唯一
        generatedCardNumbers.add(cardNumber); // 将新生成的卡号添加到集合中
        return cardNumber;
    }

    /**
     * 生成16位随机数字字符串。
     *
     * @return 生成的随机数字字符串
     */
    private static String generateRandomNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10)); // 生成0到9之间的随机数并追加到字符串构建器
        }
        return sb.toString();
    }
}