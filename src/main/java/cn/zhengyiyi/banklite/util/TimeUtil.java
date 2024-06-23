package cn.zhengyiyi.banklite.util;

import java.sql.Timestamp;

/**
 * 时间工具类。
 * 提供获取当前时间和格式化时间戳的功能。
 */
public class TimeUtil {
    /**
     * 获取当前时间并且格式化输出。
     *
     * @return 格式化后的当前时间字符串，格式为"yyyy年MM月dd日 HH:mm:ss"。
     */
    public static String getCurrentTime() {
        return new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new java.util.Date());
    }

    /**
     * 根据传入的参数Timestamp格式化输出。
     *
     * @param timestamp 需要格式化的时间戳
     * @return 格式化后的时间字符串，格式为"yyyy年MM月dd日 HH:mm:ss"。
     */
    public static String formatTimestamp(Timestamp timestamp) {
        return new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(timestamp);
    }
}