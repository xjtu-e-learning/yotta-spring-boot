package com.xjtu.common;

import com.spreada.utils.chinese.ZHConverter;

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
    public static String DOMAIN_LAYER_TABLE = "domain_layer";
//    public static String DOMAIN_LAYER_FUZHU_TABLE = "domain_layer_fuzhu";
//    public static String DOMAIN_LAYER_FUZHU2_TABLE = "domain_layer_fuzhu2";
//    public static String DOMAIN_TOPIC_RELATION_TABLE = "domain_topic_relation";
//    public static String DOMAIN_LAYER_RELATION_TABLE = "domain_layer_relation";
    public final static String TOPIC_TABLE = "topic";
    public final static String FACET_TABLE = "facet";
 //   public static String FACET_RELATION_TABLE = "facet_relation";
    public final static String ASSEMBLE_TABLE = "assemble";

    /**
     * 代理设置
     */
    public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    //public static String IE_PATH = "";
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


    /**20190628新增
     * Selenium Webdriver 配置
     */
    public static String PHANTOMJS_PATH = "D:\\Yotta\\phantomjs.exe";  // 无界面浏览器
    public static String IE_PATH = "D:\\Yotta\\IEDriverServer.exe";  // IE模拟
    public static String CHROME_PATH = "D:\\Yotta\\chromedriver.exe";  // Chrome模拟

    /**20190628新增
     * Mysql 配置
     */
    public static String DBNAME = "yotta_spring_boot_complete";
    //    public static String DBNAME = "yotta";
    public static String HOST = "localhost";
    public static String USERNAME = "root";
    public static String PASSWD = "123456";
    public static int PORT = 3306;
    //    public static int PORT = 9220;
    public static String MYSQL_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DBNAME + "?user=" + USERNAME + "&password=" + PASSWD + "&characterEncoding=UTF8"; // 阿里云服务器：域名+http端口

//   public static String MYSQL_URL = "jdbc:mysql://localhost:3306/yotta_create_test?user=root&password=root&characterEncoding=UTF8"; // 阿里云服务器：域名+http端口
//
//    public static String IP1 = "http://202.117.54.39:666"; // 跨域访问控制：域名+apache端口
//    public static String IP2 = "http://202.117.54.39:8081/Yotta"; // 阿里云服务器：域名+http端口
//    public static String SWAGGERHOST = "202.117.54.39:8081"; // swagger主机
//    public static String SWAGGERBASEPATH = "/Yotta"; //swagger根路径

    /**
     * 爬虫
     */
    public static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
    public static int TEXTLENGTH = 50; // 保存文本最短长度

    //public static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36"; // 代理设置
}
