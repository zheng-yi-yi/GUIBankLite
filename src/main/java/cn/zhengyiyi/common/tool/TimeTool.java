package cn.zhengyiyi.common.tool;

import java.sql.Timestamp;

public class TimeTool {
    // 获取当前时间并且格式化输出
    public static String getCurrentTime() {
        return new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new java.util.Date());
    }

    // 根据传入的参数Timestamp格式化输出
    public static String formatTimestamp(Timestamp timestamp) {
        return new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(timestamp);
    }
}
