package com.xjtu.spider.spiders.webmagic.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SqlPipeline implements Pipeline {


    @Override
    public void process(ResultItems resultItems, Task task) {

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()){
            FragmentContent fragmentContent = (FragmentContent)entry.getValue();
            List<String> fragments = fragmentContent.getFragments();
            List<String> fragmentsPureText = fragmentContent.getFragmentsPureText();
            for (int i = 0; i < fragments.size(); i++) {
                mysqlUtils mysql = new mysqlUtils();
                // 定义插入语句参数
                String addSql = "insert into " + Config.ASSEMBLE_FRAGMENT_TABLE
                        + "(FragmentContent, Text, FragmentScratchTime,TermID,TermName,FacetName,FacetLayer,ClassName,SourceName) values (?,?,?,?,?,?,?,?,?)";

                // 分面信息
                Map<String,Object> facetTableMap = resultItems.getRequest().getExtras();
                // 添加碎片表需要的元组值
                List<Object> params = new ArrayList<Object>();
                // FragmentID 碎片ID 自动递增不需要
                // FragmentContent 碎片内容
                params.add(fragments.get(i));
                params.add(fragmentsPureText.get(i));
                // FragmentScratchTime 碎片爬取时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
                params.add(date);
                // TermID 主题ID
                params.add(facetTableMap.get("TermID"));
                // TermName 主题名
                params.add(facetTableMap.get("TermName"));
                // FacetName 分面名
                params.add(facetTableMap.get("FacetName"));
                // FacetLayer 分面层
                params.add(facetTableMap.get("FacetLayer"));
                // ClassName 课程名
                params.add(facetTableMap.get("ClassName"));
                // Source 数据源
                params.add(facetTableMap.get("SourceName"));
                try {
                    mysql.addDeleteModify(addSql,params);
                }
                catch (SQLException exception){
                    System.out.println(exception.getMessage());
                } finally {
                    mysql.closeconnection();
                }
            }
        }

    }
}
