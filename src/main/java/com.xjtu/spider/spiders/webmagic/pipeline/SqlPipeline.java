package com.xjtu.spider.spiders.webmagic.pipeline;

import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.spider.spiders.webmagic.bean.Assembles;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.text.SimpleDateFormat;
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
    AssembleRepository assembleRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()){
            Assembles assembles = (Assembles)entry.getValue();
            List<String> assembleContents = assembles.getAssembleContents();
            List<String> assembleTexts = assembles.getAssembleTexts();
            //将碎片保存到碎片表
            for(int i=0;i<assembleContents.size();i++){
                //构建碎片
                // 分面信息
                Map<String,Object> facet = resultItems.getRequest().getExtras();
                //时间
                //设置日期格式
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 获取当前系统时间，也可使用当前时间戳
                String date = df.format(new Date());
                Assemble assemble = new Assemble(assembleContents.get(i)
                        , assembleTexts.get(i)
                        , date
                        , (Long) facet.get("facetId")
                        , (Long) facet.get("sourceId"));
                //存碎片
                assembleRepository.save(assemble);

            }
        }
    }
}
