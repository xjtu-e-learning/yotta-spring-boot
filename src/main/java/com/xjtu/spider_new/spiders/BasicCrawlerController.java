package com.xjtu.spider_new.spiders;

import com.xjtu.facet.domain.Facet;
import com.xjtu.spider_new.spiders.csdn.CSDNCrawler;
import com.xjtu.spider_new.spiders.wikicn.MysqlReadWriteDAO;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicCrawlerController {
    private static final Logger logger =
            LoggerFactory.getLogger(BasicCrawlerController.class);

    public void startCrawler(String url, boolean isChinese, Long domainId, Long topicId) throws Exception {
        CrawlConfig config = new CrawlConfig();

        // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        config.setCrawlStorageFolder("tmp/crawler4j/");

        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        // Otherwise it may overload the target servers.
        config.setPolitenessDelay(1000);

        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth.
        config.setMaxDepthOfCrawling(0);

        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages.
        config.setMaxPagesToFetch(1000);

        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc
        config.setIncludeBinaryContentInCrawling(true);

        // Do you need to set a proxy? If so, you can use:
        // config.setProxyHost("proxyserver.example.com");
        // config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        config.setResumableCrawling(false);

        // Set this to true if you want crawling to stop whenever an unexpected error
        // occurs. You'll probably want this set to true when you first start testing
        // your crawler, and then set to false once you're ready to let the crawler run
        // for a long time.
//        config.setHaltOnError(true);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
//        controller.addSeed("https://zh.wikipedia.org/wiki/%E9%A2%B1%E9%A2%A8%E5%A5%A7%E9%A6%AC");
//        controller.addSeed("https://zh.wikipedia.org/wiki/%E5%A4%AA%E5%B9%B3%E6%B4%8B");
//        controller.addSeed("https://zh.wikipedia.org/wiki/%E6%96%90%E8%BF%AA%E5%8D%97%C2%B7%E9%BA%A5%E5%93%B2%E5%80%AB");
        controller.addSeed(url);


        // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
        // speed depends on many other factors as well. You can experiment with this to figure out what number of
        // threads works best for you.
        int numberOfCrawlers = 2;

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<BasicCrawler> factory =
                () -> new BasicCrawler("https://en.wikipedia.org/wiki/", isChinese, domainId, topicId);

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);
//        controller.waitUntilFinish();
        logger.info("Crawler is finished");

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        Set<String> facetNameSet = new HashSet<>();
        long totalLinks = 0;
        long totalTextSize = 0;
        int totalProcessedPages = 0;
        for (Object localData : crawlersLocalData) {
            CrawlerStat stat = (CrawlerStat) localData;
            totalLinks += stat.getTotalLinks();
            totalTextSize += stat.getTotalTextSize();
            totalProcessedPages += stat.getTotalProcessedPages();
            if (stat.getFacetNameList() == null) continue;
            for (String facetName : stat.getFacetNameList()) {
                if (facetNameSet.contains(facetName)) continue;
                facetNameSet.add(facetName);
            }
        }

        logger.info("Aggregated Statistics:");
        logger.info("\tProcessed Pages: {}", totalProcessedPages);
        logger.info("\tTotal Links found: {}", totalLinks);
        logger.info("\tTotal Text Size: {}", totalTextSize);

//        controller.startNonBlocking(factory, numberOfCrawlers);
//
//        Thread.sleep(30 * 1000);
//
//        controller.shutdown();
//        controller.waitUntilFinish();


        // 再去CSDN上获取新的碎片
        String topicName = MysqlReadWriteDAO.findTopicNameByTopicId(topicId);
        config.setMaxDepthOfCrawling(1);
        config.setMaxPagesToFetch(50);

        assert facetNameSet != null;
        for (String facetName : facetNameSet) {

            pageFetcher = new PageFetcher(config);
            robotstxtConfig = new RobotstxtConfig();
            robotstxtConfig.setEnabled(false);
            robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
//            controller.addSeed("https://so.csdn.net/so/search?q=" + topicName + facetName + "&t=&u=&utm_medium=distribute.pc_search_hot_word.none-task-hot_word-alirecmd-7.nonecase&dist_request_id=&depth_1-utm_source=distribute.pc_search_hot_word.none-task-hot_word-alirecmd-7.nonecase");
            String targetURL = "https://cn.bing.com/search?q=" + topicName + facetName;
            controller.addSeed(targetURL);

            // 启动CSDNCrawler
            CrawlController.WebCrawlerFactory<CSDNCrawler> csdnFactory =
                    () -> new CSDNCrawler(targetURL, isChinese, domainId, topicId, facetName);

            controller.start(csdnFactory, numberOfCrawlers);
            logger.info("Crawler is finished");

        }

    }

    /**
     * 根据给定课程、主题、分面列表，爬取分面列表下每个分面的碎片
     * @param domainId
     * @param topicId
     * @param facets
     */
    public void startCrawlerForAssembleOnly(Long domainId, Long topicId, List<Facet> facets) throws Exception {
        int numberOfCrawlers = 2;
        String crawlStorageFolder = "/tmp/crawler4j/";
        List<CrawlController> crawlControllers = new ArrayList<>();
        List<CrawlController.WebCrawlerFactory<CSDNCrawler>> crawlerFactories = new ArrayList<>();

        for (int i = 0; i < facets.size(); i++) {
            CrawlConfig config = new CrawlConfig();

            config.setCrawlStorageFolder(crawlStorageFolder + "/facet" + facets.get(i).getFacetName());
            config.setPolitenessDelay(1000);
            config.setMaxDepthOfCrawling(0);
            config.setMaxPagesToFetch(50);
            config.setIncludeBinaryContentInCrawling(false);
            config.setResumableCrawling(false);

            // Instantiate the controller for this crawl.
            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            robotstxtConfig.setEnabled(false);
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            String topicName = MysqlReadWriteDAO.findTopicNameByTopicId(topicId);
            config.setMaxDepthOfCrawling(1);
            config.setMaxPagesToFetch(50);

            String facetName = facets.get(i).getFacetName();
            String targetURL = "https://cn.bing.com/search?q=" + topicName + facetName;
            controller.addSeed(targetURL);
            CrawlController.WebCrawlerFactory<CSDNCrawler> csdnFactory =
                    () -> new CSDNCrawler("https://cn.bing.com/search?q=", true, domainId, topicId, facetName);

            crawlControllers.add(controller);
            crawlerFactories.add(csdnFactory);

        }

        for (int i = 0; i < facets.size(); i++) {
            crawlControllers.get(i).startNonBlocking(crawlerFactories.get(i), numberOfCrawlers);
        }

        for (int i = 0; i < facets.size(); i++) {
            crawlControllers.get(i).waitUntilFinish();
            logger.info("Crawler for " + facets.get(i).getFacetName() +  " is finished.");
        }

    }

}
