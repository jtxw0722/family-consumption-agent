package com.jtxw.familyagent.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: jtxw
 * @Date: 2026/05/11/00:12
 * @Description: 本地时间工具，统一生成持久化使用的时间字符串。
 */
public final class ClockUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ClockUtils() {
    }

    /**
     * 返回本地当前时间字符串，供数据库审计字段使用。
     *
     * @return ISO 本地日期时间字符串
     */
    public static String nowText() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
