package com.xjtu.spider.spiders.webmagic.pipeline;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.spider.spiders.webmagic.bean.Assembles;
import com.xjtu.spider.spiders.webmagic.service.SQLService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author yangkuan
 * @date 2018/05/17 22:42
 */
public class SqlPipeline implements Pipeline {
    @Autowired
    SQLService sqlService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()){
            Assembles assembles = (Assembles)entry.getValue();
            List<String> assembleContents = assembles.getAssembleContents();
            List<String> assembleTexts = assembles.getAssembleTexts();
            //将碎片保存到碎片表
            List<Assemble> assembleList = new ArrayList<>();
            for(int i=0;i<assembleContents.size();i++){
                //构建碎片
                // 分面信息
                Map<String,Object> facetMap = resultItems.getRequest().getExtras();
                facetMap = sqlService.getFacet(facetMap);
                //时间
                //设置日期格式
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 获取当前系统时间，也可使用当前时间戳
                String date = df.format(new Date());
                Assemble assemble = new Assemble(assembleContents.get(i)
                        , assembleTexts.get(i)
                        , date
                        , (Long) facetMap.get("facetId")
                        , (Long) facetMap.get("sourceId"));
                //存碎片
                assembleList.add(assemble);
            }
        sqlService.saveAssembles(assembleList);
        }
    }
}
