package com.xjtu.timerTask_assemble_spider.spiders.webmagic.spider;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.monitor.SpiderStatus;
import us.codecraft.webmagic.monitor.SpiderStatusMXBean;

import javax.management.JMException;
import java.util.List;

public class myMonitor {
    SpiderMonitor spiderMonitor;

    public myMonitor()
    {
        spiderMonitor = new SpiderMonitor(){
            @Override
            protected SpiderStatusMXBean getSpiderStatusMBean(Spider spider, MonitorSpiderListener monitorSpiderListener){
                return new SpiderStatus(spider, monitorSpiderListener);
            }
        };
    }

    public void register(Spider spider)
    {
        try {
            spiderMonitor.register(spider);
        } catch (JMException e) {
            e.printStackTrace();
        }
    }

    public int monitor(Spider spider)
    {
        int leftCount = 0;
        List<SpiderListener> spiderListeners = spider.getSpiderListeners();
        for(SpiderListener spiderListener : spiderListeners)
        {
            if(spiderListener instanceof SpiderMonitor.MonitorSpiderListener)
            {
                SpiderMonitor.MonitorSpiderListener monitorSpiderListener = (SpiderMonitor.MonitorSpiderListener) spiderListener;
                SpiderStatus spiderStatus = new SpiderStatus(spider, monitorSpiderListener);
                leftCount = spiderStatus.getLeftPageCount();
                if(leftCount == 0)
                {
                    break;
                }
                System.out.println("left page count: " + leftCount);
            }
        }
        return leftCount;
    }

}
