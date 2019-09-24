package com.xjtu.timerTask_assemble_spider.spiders.webmagic.pipeline;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.timerTask_assemble_spider.service.TimeTaskSpiderAssembleService;
import com.xjtu.timerTask_assemble_spider.spiders.webmagic.bean.Assembles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 保存碎片到sql数据库
 */

public class SqlPipeline implements Pipeline {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    TimeTaskSpiderAssembleService spiderService;

    private AssembleRepository assembleRepository;

    public SqlPipeline(TimeTaskSpiderAssembleService spiderService, AssembleRepository assembleRepository) {
        this.spiderService = spiderService;
        this.assembleRepository = assembleRepository;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        // 分面信息
//        Map<String, Object> facetMap = resultItems.getRequest().getExtras();
//        facetMap = spiderService.getFacet(facetMap);
        //确定的分面下，数据库中已存在的碎片
//        List<Assemble> existAssembles = assembleRepository.findByFacetId((Long) facetMap.get("facetId"));

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            Assembles assembles = (Assembles) entry.getValue();
            List<String> assembleContents = assembles.getAssembleContents();
            List<String> assembleTexts = assembles.getAssembleTexts();
            //将碎片保存到碎片表
            List<Assemble> assembleList = new ArrayList<>();
            for (int i = 0; i < assembleContents.size(); i++) {
                //构建碎片
                // 分面信息
                Map<String, Object> facetMap = resultItems.getRequest().getExtras();
                facetMap = spiderService.getFacet(facetMap);
//                //确定的分面下，数据库中已存在的碎片
                Long facetID = (Long) facetMap.get("facetId");
                logger.error("facetID: " + facetID);
                List<Assemble> existAssembles = new ArrayList<>();
                if (facetID != null)
                    existAssembles = assembleRepository.findByFacetId(facetID);

                //判断是否与数据库中碎片重复
                Iterator iterator = existAssembles.iterator();
                boolean equalFlag = false;
                while (iterator.hasNext())
                {
                    Assemble tempAssemble = (Assemble) iterator.next();
                    if (assembleTexts.get(i).hashCode() == tempAssemble.getAssembleText().hashCode())
                        equalFlag = true;
                }
                if (equalFlag)
                    continue;

                //时间
                //设置日期格式
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 获取当前系统时间，也可使用当前时间戳
                String date = df.format(new Date());
                Assemble assemble = new Assemble();
                assemble.setAssembleContent(assembleContents.get(i));
                assemble.setAssembleText(assembleTexts.get(i));
                assemble.setAssembleScratchTime(date);
                assemble.setFacetId((Long) facetMap.get("facetId"));
                assemble.setSourceId((Long) facetMap.get("sourceId"));
                assemble.setDomainId((Long) facetMap.get("domainId"));
                //存碎片
                assembleList.add(assemble);
            }
            spiderService.saveAssembles(assembleList);
        }
    }
}
