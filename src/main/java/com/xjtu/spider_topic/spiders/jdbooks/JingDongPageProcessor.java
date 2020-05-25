package com.xjtu.spider_topic.spiders.jdbooks;

import com.xjtu.common.Config;
import com.xjtu.spider_topic.service.TFSpiderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;


/**
 * Created on 2020/5/24.
 *
 * @author Duo Zhang
 */
public class JingDongPageProcessor implements PageProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    TFSpiderService tfSpiderService;

    public static final String URL_LIST = "https://search(.*?)";

    public static final String URL_CONTENT = "https://item(.*?)";

//    public JingDongPageProcessor(TFSpiderService tfSpiderService){
//        this.tfSpiderService = tfSpiderService;
//    }

    private Site site = Site.me()
            .setRetrySleepTime(Config.retrySleepTime)
            .setRetryTimes(Config.retryTimes)
            .setSleepTime(Config.sleepTime)
            .setTimeOut(Config.timeOut)
            .addHeader("User-Agent", Config.userAgent)
            .addHeader("Accept", "*/*");

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(URL_LIST).match()) {
            //获取书籍目录
            List<String> books = page.getHtml().xpath("//*[@id=\"J_goodsList\"]//ul//li/@data-sku").all();
            page.putField("书籍id集合", books);
            for (String book : books) {
                System.out.println("书籍id为 " + book);
                Request request = new Request();
                request.setUrl("https://item.jd.com/" + book + ".html");
                page.addTargetRequest(request);
            }
        } else if (page.getUrl().regex(URL_CONTENT).match()) {
            List<String> bookContent = page.getHtml().xpath("//*[@id=\"detail-tag-id-6\"]/div[2]/div/tidyText()").all();
 //           String bookContent = page.getHtml().toString();
            page.putField("content", bookContent);
        }

    }

    public static void main(String[] args) {
        String domainName = "数据结构";
        System.setProperty("selenuim_config","D:\\Yotta\\config.ini");
        Spider.create(new JingDongPageProcessor())
                .addUrl("https://search.jd.com/Search?keyword=" + domainName + "&wq=" + domainName + "&psort=3&click=0")
                //new HttpClientDownloader()
                .setDownloader(new SeleniumDownloader("D:\\Yotta\\chromedriver.exe"))
                .thread(3)
                .run();
    }

}
