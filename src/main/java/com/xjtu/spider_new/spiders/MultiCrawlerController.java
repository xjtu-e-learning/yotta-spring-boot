package com.xjtu.spider_new.spiders;

import com.xjtu.topic.domain.Topic;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MultiCrawlerController {
    private static final Logger logger =
            LoggerFactory.getLogger(MultiCrawlerController.class);

    public void startCrawler(List<Topic> emptyTopics, boolean isChinese, Long domainId) throws Exception {
        // The folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        String crawlStorageFolder = "/tmp/crawler4j/";

        List<CrawlController> crawlControllers = new ArrayList<>();
        List<CrawlController.WebCrawlerFactory<BasicCrawler>> crawlerFactories = new ArrayList<>();

        for (int i = 0; i < emptyTopics.size(); i++) {
            CrawlConfig config = new CrawlConfig();

            // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
            // fetched pages and need to be crawled later).
            config.setCrawlStorageFolder(crawlStorageFolder + "/topic" + emptyTopics.get(i).getTopicId());

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
            controller.addSeed(emptyTopics.get(i).getTopicUrl());


            // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
            // speed depends on many other factors as well. You can experiment with this to figure out what number of
            // threads works best for you.
            int numberOfCrawlers = 1;

            Long topicId = emptyTopics.get(i).getTopicId();
            // The factory which creates instances of crawlers.
            CrawlController.WebCrawlerFactory<BasicCrawler> factory =
                    () -> new BasicCrawler("https://en.wikipedia.org/wiki/", isChinese, domainId, topicId);

            crawlControllers.add(controller);
            crawlerFactories.add(factory);
        }

        for (int i = 0; i < crawlControllers.size(); i++) {
            crawlControllers.get(i).startNonBlocking(crawlerFactories.get(i), 1);
        }

        for (int i = 0; i < crawlControllers.size(); i++) {
            crawlControllers.get(i).waitUntilFinish();
            logger.info("Domain-" + domainId + ": " + (i+1) + "th empty topic has been crawled.");
//            List<Object> crawlersLocalData = crawlControllers.get(i).getCrawlersLocalData();
//            long totalLinks = 0;
//            long totalTextSize = 0;
//            int totalProcessedPages = 0;
//            for (Object localData : crawlersLocalData) {
//                CrawlerStat stat = (CrawlerStat) localData;
//                totalLinks += stat.getTotalLinks();
//                totalTextSize += stat.getTotalTextSize();
//                totalProcessedPages += stat.getTotalProcessedPages();
//            }
//
//            logger.info("Aggregated Statistics:");
//            logger.info("\tProcessed Pages: {}", totalProcessedPages);
//            logger.info("\tTotal Links found: {}", totalLinks);
//            logger.info("\tTotal Text Size: {}", totalTextSize);
        }

    }
}
