package com.xjtu.spider.spiders.webmagic.pipeline;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.spider.service.SpiderService;
import com.xjtu.spider.spiders.webmagic.bean.Assembles;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 保存碎片到sql数据库
 *
 * @author yangkuan
 * @date 2018/05/17 22:42
 */
public class SqlPipeline implements Pipeline {

    SpiderService spiderService;

    public SqlPipeline(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
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
                assemble.setFacetId((Long) facetMap.get("sourceId"));
                //存碎片
                assembleList.add(assemble);
            }
            spiderService.saveAssembles(assembleList);
        }
    }
}
