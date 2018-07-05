package com.xjtu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 和时间相关的工具类
 * Created by 18710 on 2017/8/9.
 */
public class TimeUtil {

    /**
     * 获取系统当前时间
     *
     * @return 系统当前时间
     */
    public static String getSystemTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置日期格式
        return df.format(new Date()); // new Date()为获取当前系统时间
    }

}
