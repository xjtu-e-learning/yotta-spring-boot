package com.xjtu.spider.spiders.wikien;

import com.spreada.utils.chinese.ZHConverter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * 解析中文维基
 * 1. 获得每一分类的子分类
 * 2. 获得每一分类的子页面
 * <p>
 * * @author 张铎
 *
 * @date 2018年3月10日
 */
public class TopicEnExtract {

    private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文

    public static void main(String[] args) throws Exception {
//        String url = "https://en.wikipedia.org/wiki/Category:Data_structures";
//        String html = SpiderUtils.seleniumWikiCN(url);
//        Document doc = JsoupDao.parseHtmlText(html);
//        getTopic(doc);
//		getLayer(doc);
    }

    /**
     * 解析得到Category中的页面术语
     *
     * @param doc
     * @return
     */
    public static List<Term> getTopic(Document doc) {
        List<Term> termList = new ArrayList<>();
        Elements mwPages = doc.select("#mw-pages").select("li");
        for (int i = 0; i < mwPages.size(); i++) {
            String url = "https://en.wikipedia.org" + mwPages.get(i).select("a").attr("href");
            String topic = mwPages.get(i).text();
            topic = converter.convert(topic);
            Term term = new Term(topic, url);
            termList.add(term);
        }
        return termList;
    }

    /**
     * 解析得到Category中的二级子分类页面
     *
     * @param doc
     * @return
     */
    public static List<Term> getLayer(Document doc) {
        List<Term> termList = new ArrayList<Term>();
        if (doc.select("#mw-subcategories").size() == 0) {
            Log.log("没有下一层子分类...");
        } else {
            Elements mwPages = doc.select("#mw-subcategories").select("li");
            for (int i = 0; i < mwPages.size(); i++) {
                String url = "https://en.wikipedia.org" + mwPages.get(i).select("a").attr("href");
                String layer = mwPages.get(i).select("a").text();
                layer = converter.convert(layer);
                Term term = new Term(layer, url);
                termList.add(term);
            }
        }
        return termList;
    }

}
