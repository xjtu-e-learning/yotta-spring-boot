package com.xjtu.spider_topic.spiders.wikicn;

import com.xjtu.common.Config;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.domain.FacetRelation;
import com.xjtu.utils.JsoupDao;
import com.xjtu.utils.SpiderUtils;
import com.xjtu.facet.domain.FacetSimple;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.xjtu.utils.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.xjtu.spider_topic.spiders.wikicn.FragmentExtract.delTitle;

/**
 * 实现中文维基百科知识森林数据集的构建
 * 将文本和图片存储到一个表格中
 *
 * @author lynn
 */
public class FragmentCrawlerDAO {
    /**
     * 得到一个主题的所有分面及其分面级数
     * 1. 数据结构为: FacetSimple
     *
     * @param doc
     * @return
     */
    public static List<FacetSimple> getFacet(Document doc) {
        List<FacetSimple> facetList = new ArrayList<>();
        Log.log("爬取该主题分面如下");
        List<String> firstTitle = FragmentExtract.getFirstTitle(doc);
        List<String> secondTitle = FragmentExtract.getSecondTitle(doc);
        List<String> thirdTitle = FragmentExtract.getThirdTitle(doc);

        // 保存一级分面名及其分面级数
        for (int i = 0; i < firstTitle.size(); i++) {
            String facetName = firstTitle.get(i);
            int facetLayer = 1;
            FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
            facetList.add(facetSimple);
        }
        // 保存二级分面名及其分面级数
        for (int i = 0; i < secondTitle.size(); i++) {
            String facetName = secondTitle.get(i);
            int facetLayer = 2;
            FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
            facetList.add(facetSimple);
        }
        // 保存三级分面名及其分面级数
        for (int i = 0; i < thirdTitle.size(); i++) {
            String facetName = thirdTitle.get(i);
            int facetLayer = 3;
            FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
            facetList.add(facetSimple);
        }
        //}

        return facetList;

    }


    /**
     * 获取各级分面父子对应关系
     *
     * @param doc
     * @return
     */
    public static List<FacetRelation> getFacetRelation(Document doc) {
        LinkedList<String> indexs = new LinkedList<String>();// 标题前面的下标
        LinkedList<String> facets = new LinkedList<String>();// 各级标题的名字
        List<FacetRelation> facetRelationList = new ArrayList<FacetRelation>();

        try {
            /**
             * 获取标题
             */
            Elements titles = doc.select("div#toc").select("li");
            Log.log("\n该主题分面数目为" + titles.size());
            if (titles.size() != 0) {
                Log.log("--------------------------------------------");
                Log.log("\n爬取该主题分面关系表如下：");
                for (int i = 0; i < titles.size(); i++) {
                    String index = titles.get(i).child(0).child(0).text();
                    String text = titles.get(i).child(0).child(1).text();
                    text = Config.converter.convert(text);
                    Boolean flag = delTitle(text);
                    if (!flag) {
                        indexs.add(index);
                        facets.add(text);
                        Log.log(index + " " + text);
                    }
                }
                /**
                 * 将二级/三级标题全部匹配到对应的父标题
                 */
                Log.log("--------------------------------------------");
                for (int i = 0; i < indexs.size(); i++) {
                    String index = indexs.get(i);
                    if (index.lastIndexOf(".") == 1) { // 二级分面
                        String facetSecond = facets.get(i);
                        for (int j = i - 1; j >= 0; j--) {
                            String index2 = indexs.get(j);
                            if (index2.lastIndexOf(".") == -1) {
                                String facetOne = facets.get(j);
                                FacetRelation facetRelation = new FacetRelation(facetSecond, 2, facetOne, 1);
                                facetRelationList.add(facetRelation);
                                Log.log("一级分面 " + facetOne + " 下的二级分面 " + facetSecond + " 已关联");
                                break;
                            }
                        }
                    } else if (index.lastIndexOf(".") == 3) { // 三级分面
                        String facetThird = facets.get(i);
                        for (int j = i - 1; j >= 0; j--) {
                            String index2 = indexs.get(j);
                            if (index2.lastIndexOf(".") == 1) {
                                String facetSecond = facets.get(j);
                                FacetRelation facetRelation = new FacetRelation(facetThird, 3, facetSecond, 2);
                                facetRelationList.add(facetRelation);
                                Log.log("二级分面 " + facetSecond + " 下的三级分面 " + facetThird + " 已关联");
                                break;
                            }
                        }
                    }
                }

            } else {
                Log.log("该主题没有目录，不是目录结构，直接爬取 -->摘要<-- 信息");
            }
        } catch (Exception e) {
            Log.log("该页面内容格式不正确，请检查链接格式");
        }
        Log.log("\n该主题分面关系对个数如下："+ facetRelationList.size());
        return facetRelationList;
    }

}

