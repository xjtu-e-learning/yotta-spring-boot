package com.xjtu.spider_Assemble.spiders.webmagic.spider;

import com.xjtu.spider.spiders.webmagic.spider.YangKuanSpider;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class spiderCreate extends Spider {
    public spiderCreate(PageProcessor pageProcessor) {
        super(pageProcessor);
    }

    public static spiderCreate create(PageProcessor pageProcessor) {
        return new spiderCreate(pageProcessor);
    }

    public spiderCreate addRequests(List<Request> requests) {
        for (Request request : requests) {
            this.addRequest(request);
        }
        return this;
    }
}
