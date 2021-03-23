package com.xjtu.spider_new.spiders.jdbooks;

import com.xjtu.common.Config;
import com.xjtu.spider_topic.service.TFSpiderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
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

    private static int bookCount = 0;

    private TFSpiderService tfSpiderService;


    public static final String URL_LIST = "https://search(.*?)";

    public static final String URL_CONTENT = "https://item(.*?)";

    public JingDongPageProcessor() {
    }

    public JingDongPageProcessor(TFSpiderService tfSpiderService) {
        this.tfSpiderService = tfSpiderService;
    }


    private Site site = Site.me()
            .setRetrySleepTime(Config.retrySleepTime)
            .setRetryTimes(Config.retryTimes)
            .setSleepTime(100)
            .setTimeOut(Config.timeOut)
            .addHeader("User-Agent", Config.userAgent)
            .addHeader("Accept", "*/*");

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 重写为加同步锁的形式，保证书目ID自增有序
     * @param page
     */
    @Override
    public void process(Page page) {
        if (page.getUrl().regex(URL_LIST).match()) {
            //获取书籍目录
            List<String> books = page.getHtml().xpath("//*[@id=\"J_goodsList\"]//ul//li/@data-sku").all();
            page.putField("书籍id集合", books);
            logger.info("已获取该课程书目ID集合，准备开始爬取每本书的目录");
            for (String book : books) {
                System.out.println("书籍id为 " + book);
                Request request = new Request();
                request.setUrl("https://item.jd.com/" + book + ".html");
                page.addTargetRequest(request);
            }
        } else if (page.getUrl().regex(URL_CONTENT).match()) {
            //page.getHtml().xpath("/html/head/title/text()").regex("(.*?)\\【$").get();
            String bookContent = page.getHtml().xpath("//*[@id=\"detail-tag-id-6\"]/div[2]/div/tidyText()").get();
            if (!bookContent.isEmpty()) {
                synchronized (this){
                    String bookTitle = ++bookCount + "";
                    page.putField(bookTitle, bookContent);
                }
            }
        } else {
            logger.error("链接出错，请检查爬虫设置是否正确");
        }

    }

    public static void main(String[] args) {
        String domainName = "数据结构";
        System.setProperty("selenuim_config", Config.SELENIUM_CONFIG);
        Spider.create(new JingDongPageProcessor())
                .addUrl("https://search.jd.com/Search?keyword=" + domainName + "&wq=" + domainName + "&psort=3&click=0")
                .setDownloader(new SeleniumDownloader(Config.CHROME_PATH))
                //.addPipeline(new JsonFilePipeline("D:\\Yotta\\"))
                .addPipeline(new JingDongPipeline())
                .thread(3)
                .runAsync();
    }

}
