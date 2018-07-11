package com.xjtu.common;

import java.util.Random;

/**
 * 系统常量
 * Created by yuanhao on 2017/3/14.
 */
public class Config {

    /**
     * Spider 爬虫参数设置
     *
     * @author 郑元浩
     */
    public final static int CONTENTLENGTH = 30;


    /**
     * 爬虫配置
     */
    public final static String SOURCE_TABLE = "source";
    public final static String SUBJECT_TABLE = "subject";
    public final static String DOMAIN_TABLE = "domain";
    public final static String TOPIC_TABLE = "topic";
    public final static String FACET_TABLE = "facet";
    public final static String ASSEMBLE_TABLE = "assemble";

    /**
     * 代理设置
     */
    public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    public static String IE_PATH = "";
    /**
     * Stack overflow网站爬虫参数配置：网站防扒做的比较好，爬虫条件苛刻
     */
    /**
     * SO网站的爬虫线程为1，否则会被禁止
     */
    public static int threadSO = 1;
    /**
     * 爬虫连接失败重试次数，注意设置为随机数，避免被发现是爬虫（下同）
     */
    public static int retryTimesSO = new Random().nextInt(2) + 3;
    /**
     * 爬虫连接失败重试的间隔
     */
    public static int retrySleepTimeSO = new Random().nextInt(10000) + 10000;
    /**
     * 爬虫请求连接的时间间隔
     */
    public static int sleepTimeSO = new Random().nextInt(10000) + 10000;
    /**
     * 爬虫连接超时的时间间隔
     */
    public static int timeOutSO = new Random().nextInt(10000) + 20000;
    /**
     * 网站域名
     */
    public static String originSO = "https://stackoverflow.com";
    /**
     * 网站主机信息
     */
    public static String hostsSO = "as-sec.casalemedia.com";

    /**
     * 其它网站爬虫参数配置：网站防扒做的不太好，爬虫条件更宽松
     */
    public static int THREAD = 3;
    public static int retryTimes = new Random().nextInt(2) + 3;
    public static int retrySleepTime = new Random().nextInt(2000) + 1000;
    public static int sleepTime = new Random().nextInt(3000) + 3000;
    public static int timeOut = new Random().nextInt(3000) + 3000;
}
