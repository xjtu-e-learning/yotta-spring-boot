package com.xjtu.utils;

import com.xjtu.common.Config;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Selenium模拟浏览器爬取中文维基
 *
 * @author 郑元浩
 * @date 2016年11月26日
 */
@SuppressWarnings("deprecation")
public class SpiderUtils {
    private static final Logger logger = LoggerFactory.getLogger(SpiderUtils.class);

    /**
     * 返回中文维基页面加载结果的HTML源码
     * 拖动页面一次
     *
     * @param url 网页链接
     * @return 网页源码
     * @throws Exception
     */
    public static String seleniumWiki(String url) throws Exception {
//		System.setProperty("webdriver.chrome.driver", Config.CHROME_PATH);
        System.setProperty("webdriver.ie.driver", Config.IE_PATH);
//		System.setProperty("phantomjs.binary.path", Config.PHANTOMJS_PATH);

//		WebDriver driver = new ChromeDriver();
        WebDriver driver = new InternetExplorerDriver();
//		WebDriver driver = new PhantomJSDriver();

        int m = 1;
        driver.manage().timeouts().pageLoadTimeout(1000, TimeUnit.SECONDS);
        while (m < 4) {
            try {
                driver.get(url);
            } catch (Exception e) {
                logger.info("第" + m + "次重载页面...");
                m++;
                driver.quit();

//				driver = new ChromeDriver();
                driver = new InternetExplorerDriver();
//				driver = new PhantomJSDriver();

                driver.manage().timeouts().pageLoadTimeout(1000, TimeUnit.SECONDS);
                continue;
            }
            break;
        }
        logger.info("Page title is: " + driver.getTitle());
        String html = driver.getPageSource();
        Thread.sleep(1000);
        driver.quit();
        return html;
    }

    public static String httpWiki(String url) {
        String web = "";
        try {
            //创建client实例
            HttpClient client = HttpClients.createDefault();
            //创建httpget实例
            HttpGet httpGet = new HttpGet(url);
            //执行 get请求
            HttpResponse response = client.execute(httpGet);
            //返回获取实体
            HttpEntity entity = response.getEntity();
            //获取网页内容，指定编码
            web = EntityUtils.toString(entity, "UTF-8");
            //输出网页

        } catch (IOException e) {
            e.printStackTrace();
        }
        return web;
    }

}
