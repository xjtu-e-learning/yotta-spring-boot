package com.xjtu.spider_new.spiders.csdn;

import com.xjtu.facet.domain.FacetSimple;
import com.xjtu.spider_new.spiders.CrawlerStat;
import com.xjtu.spider_new.spiders.wikicn.MysqlReadWriteDAO;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CSDNCrawler extends WebCrawler {

    private static final Pattern FILTERS = Pattern.compile(
            ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private String DOMAIN;

    private CrawlerStat myCrawlerStat;

    private Document document;

    private boolean isChinese;

    private Long domainId;

    private Long topicId;

    private String facetName;

    /**
     * Creates a new crawler instance.
     */
    public CSDNCrawler(String DOMAIN, boolean isChinese, Long domainId, Long topicId, String facetName) {
        this.DOMAIN = DOMAIN;
        this.isChinese = isChinese;
        this.domainId = domainId;
        this.topicId = topicId;
        this.facetName = facetName;
        myCrawlerStat = new CrawlerStat();
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();

        // Only accept the url if it is in the "https://zh.wikipedia.org/wiki/" domain and protocol is "https".
//        return !FILTERS.matcher(href).matches() &&
//                (href.startsWith("https://blog.csdn.net/") || href.startsWith("https://so.csdn.net/"));
        return !FILTERS.matcher(href).matches() &&
                (href.startsWith("https://blog.csdn.net/") || href.startsWith("https://www.cnblogs.com/") ||
                    href.startsWith("https://www.jianshu.com/") || href.startsWith("https://baike.baidu.com/") ||
                        href.equals(DOMAIN.toLowerCase()));
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        logger.info("===================================================================================");
        logger.info("Visited: {}", page.getWebURL().getURL());
        myCrawlerStat.incProcessedPages();


        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            String domain = page.getWebURL().getDomain(); // 查看当前爬的是哪个域

            switch (domain) {
                case "bing.com":
                    logger.info("当前域为搜索页面，不进行处理");
                    break;
                case "jianshu.com":
                    // 解析简书页面
                    parseJianshu(html);
                    break;
                case "csdn.net":
                    // 解析CSDN页面
                    parseCSDN(html);
                    break;
                case "cnblogs.com":
                    // 解析博客园页面
                    parseCNBlog(html);
                    break;
                default:
                    logger.error("未知源");
            }


//            for (Object child : content.childNodes()) {
//                if (child instanceof Element) {
//
//                    Long facetLevel1; // 一级分面id
//                    Long facetLevel2; // 二级分面id
//
//                    // 获取一级分面名 （即h2标题）
//                    if (((Element) child).tag().getName().equals("h2")) {
//                        // 这里由于中英文页面样式不同，需要分开处理
//                        facetName = isChinese ? ((Element) child).child(1).text() : ((Element) child).child(0).text();
//
//                        // 需要根据分面名查找数据库找到对应分面id
//
//                        // 记录下分面id，供后续碎片存储
//
//                        // 将二级分面id置为null防止碎片放错
//
//
//                    }
//
//                    // 获取二级分面名 （即h3标题）
//                    if (((Element) child).tag().getName().equals("h3")) {
////                        System.out.println(isChinese ? ((Element) child).child(1).text() : ((Element) child).child(0).text());
//
//                        // 需要根据分面名查找数据库找到对应分面id
//
//                        // 记录下分面id，供后续碎片存储
//
//                    }
//
//                    // 获取碎片 （即h2标题下的文本）
//                    if (((Element) child).tag().getName().equals("p")) {
//                        String assembleHtml = ((Element) child).html();
//                        String assemble = ((Element) child).text();
//
//                        if (facetName == null) {
//                            // 为每个主题加上一个定义
//                            if ((!isChinese && assemble.split(" ").length > 2 || isChinese && assemble.length() > 2) && !assembleHtml.equals("<br>")) {
//                                String definition = isChinese ? "定义" : "Definition";
//
//                                System.out.println("获取到分面：" + definition);
//                                System.out.println("获取到碎片：" + assemble);
//                                System.out.println("碎片html格式：" + assembleHtml);
//                                System.out.println("课程ID：" + domainId + " 主题ID：" + topicId);
//
//                                // 在数据库中存储相应分面及碎片
//
//                                Long facetId = MysqlReadWriteDAO.findByTopicIdAndFacetName(topicId, definition);
//                                if (facetId == -1L) {
//                                    List<FacetSimple> facetList = new ArrayList<>();
//                                    facetList.add(new FacetSimple(definition, 1));
//                                    try {
//                                        MysqlReadWriteDAO.storeFacet(topicId, facetList); // 存分面
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    facetId = MysqlReadWriteDAO.findByTopicIdAndFacetName(topicId, definition);
//                                }
//
//                                MysqlReadWriteDAO.storeAssemble(assembleHtml, assemble, domainId, facetId, 1L); // 存碎片
//                            }
//                        }else if (!facetName.equals("") && !assemble.equals("") &&
//                                (!isChinese && assemble.split(" ").length > 2 || isChinese && assemble.length() > 2) &&
//                                !assembleHtml.equals("<br>")) {
//                            System.out.println("获取到分面：" + facetName);
//                            System.out.println("获取到碎片：" + assemble);
//                            System.out.println("碎片html格式：" + assembleHtml);
//                            System.out.println("课程ID：" + domainId + " 主题ID：" + topicId);
//
//                            // 在数据库中存储相应分面及碎片
//
//                            Long facetId = MysqlReadWriteDAO.findByTopicIdAndFacetName(topicId, facetName);
//                            if (facetId == -1L) {
//                                List<FacetSimple> facetList = new ArrayList<>();
//                                facetList.add(new FacetSimple(facetName, 1));
//                                try {
//                                    MysqlReadWriteDAO.storeFacet(topicId, facetList); // 存分面
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                facetId = MysqlReadWriteDAO.findByTopicIdAndFacetName(topicId, facetName);
//                            }
//
//                            MysqlReadWriteDAO.storeAssemble(assembleHtml, assemble, domainId, facetId, 1L); // 存碎片
//
//                        }
//
//
//
//                    }
//
//                }
//            }

            logger.debug("Text length: {}", text.length());
            logger.debug("Html length: {}", html.length());
            logger.debug("Number of outgoing links: {}", links.size());

            myCrawlerStat.incTotalLinks(links.size());
            try {
                myCrawlerStat.incTotalTextSize(text.getBytes("UTF-8").length);
            } catch (UnsupportedEncodingException e) {
                System.out.println("不支持的编码类型");
                e.printStackTrace();
            }
        }

        // 每处理一个page转储一下数据
        if (myCrawlerStat.getTotalProcessedPages() > 0) {
            dumpMyData();
        }

        logger.info("===================================================================================");
    }

    /**
     * 解析简书html
     * @param html 源数据
     */
    private void parseJianshu(String html) {
        Document document = Jsoup.parse(html);
        Elements article = document.select("div[class=show-content-free]");
        String articleHTML = article.html();
        String articleContent = article.text();
        System.out.println(articleContent);
        storeAssemble(articleHTML, articleContent, 17L);
    }

    private void parseCSDN(String html) {
        Document document = Jsoup.parse(html);
        Elements article = document.select("div[id=content_views]");
        String articleHTML = article.html();
        String articleContent = article.text();
        System.out.println(articleContent);
        storeAssemble(articleHTML, articleContent, 4L);
    }

    private void parseCNBlog(String html) {
        Document document = Jsoup.parse(html);
        Elements article = document.select("div[id=cnblogs_post_body]");
        String articleHTML = article.html();
        String articleContent = article.text();
        System.out.println(articleContent);
        storeAssemble(articleHTML, articleContent, 13L);
    }

    /**
     * 存储爬到的碎片
     * @param articleHTML 碎片 html
     * @param articleContent 碎片 text
     */
    private void storeAssemble(String articleHTML, String articleContent, Long sourceId) {
        // 在数据库中存储相应分面及碎片
        Long facetId = MysqlReadWriteDAO.findByTopicIdAndFacetName(topicId, facetName);
        if (facetId == -1L) {
            List<FacetSimple> facetList = new ArrayList<>();
            facetList.add(new FacetSimple(facetName, 1));
            try {
                MysqlReadWriteDAO.storeFacet(topicId, facetList); // 存分面
            } catch (Exception e) {
                e.printStackTrace();
            }
            facetId = MysqlReadWriteDAO.findByTopicIdAndFacetName(topicId, facetName);
        }

        MysqlReadWriteDAO.storeAssemble(articleHTML, articleContent, domainId, facetId, sourceId); // 存碎片
    }

    @Override
    public void onBeforeExit() {
        dumpMyData();
    }

    @Override
    public Object getMyLocalData() {
        return myCrawlerStat;
    }

    public void dumpMyData() {
        int id = getMyId();
        // You can configure the log to output to file
        logger.info("Crawler {} > Processed Pages: {}", id, myCrawlerStat.getTotalProcessedPages());
        logger.info("Crawler {} > Total Links Found: {}", id, myCrawlerStat.getTotalLinks());
        logger.info("Crawler {} > Total Text Size: {}", id, myCrawlerStat.getTotalTextSize());
    }
}
