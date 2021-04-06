package com.xjtu.spider_new.service;

import com.alibaba.fastjson.JSONObject;
import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.domain.domain.Domain;
import com.xjtu.domain.repository.DomainRepository;
import com.xjtu.domain.service.DomainService;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.spider_new.common.FacetResultVO;
import com.xjtu.spider_new.common.NewSpiderRunnable;
import com.xjtu.spider_new.common.ProgressResult;
import com.xjtu.spider_new.domain.MissingRecord;
import com.xjtu.spider_new.repository.MissingRecordRepository;
import com.xjtu.spider_new.spiders.wikicn.AssembleCrawler;
import com.xjtu.spider_new.spiders.wikicn.FragmentCrawler;
import com.xjtu.spider_new.spiders.wikicn.TopicCrawler;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.topic.service.TopicService;
import com.xjtu.utils.Log;
import com.xjtu.utils.ResultUtil;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.SchemaOutputResolver;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2021使用的爬虫
 *
 * @author 洪振杰
 * @date 2021年3月
 */
@Service
public class NewSpiderService {

    private static Boolean isChinese;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private DomainService domainService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicService topicService;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private AssembleRepository assembleRepository;

    @Autowired
    private MissingRecordRepository missingRecordRepository;

    private Thread crawler;

    private int synLock = 1;

    /**
     * 主题-分面-碎片爬虫方法
     *
     * @param subjectName 学科名
     * @param domainName 课程名
     * @param isChineseOrNot 是否为中文课程
     * @return
     */
    public Result TopicFacetTreeSpider(String subjectName, String domainName, Boolean isChineseOrNot) throws Exception {

        Domain domain = domainRepository.findByDomainName(domainName);
        List<Topic> topics = topicRepository.findByDomainName(domainName);
        List<Facet> facets = facetRepository.findByDomainName(domainName);
        List<Assemble> assembles = assembleRepository.findByDomainName(domainName);
        if (domain == null) {
            if (isChineseOrNot) {
                isChinese = true;
            } else {
                isChinese = false;
            }
            Log.log("==========知识森林里还没有这门课程，开始爬取课程：" + domainName + "==========");
            Result result = domainService.findOrInsetDomainByDomainName(subjectName, domainName);
            if (result.getCode() == 116) {
                // 课程信息插入失败：课程名不存在或者为空
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR.getCode(), ResultEnum.DOMAIN_INSERT_ERROR.getMsg());
            } else if (result.getCode() == 118) {
                // 课程信息插入失败：数据库插入语句失败
                return ResultUtil.error(ResultEnum.DOMAIN_INSERT_ERROR_2.getCode(), ResultEnum.DOMAIN_INSERT_ERROR_2.getMsg());
            } else {
                Domain domain_new = domainRepository.findByDomainName(domainName);
                Runnable runnable = new NewSpiderRunnable(domain_new);
                Thread thread = new Thread(runnable);
                thread.start();
                return ResultUtil.error(ResultEnum.TSPIDER_ERROR.getCode(), ResultEnum.TSPIDER_ERROR.getMsg(), "课程 " + domainName + " 准备开始构建");
            }
        } else if (domain != null && (topics == null || topics.size() == 0) && (facets == null || facets.size() == 0)) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR1.getCode(), ResultEnum.TSPIDER_ERROR1.getMsg(),
//                    "已经爬取的主题数目为 " + TopicCrawler.getCountTopicCrawled()
                    new ProgressResult(TopicCrawler.getCountTopicCrawled(), 0));
        } else if (domain != null && (topics != null && topics.size() != 0) && (facets == null || !FragmentCrawler.getisCompleted())) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(),
//                    "已经爬取的主题数目为 " + topicRepository.findByDomainName(domainName).size() +
//                    " 已经爬取的一级分面数目为 " + FragmentCrawler.getCountFacetCrawled() + " 二、三级分面数目为 " + FragmentCrawler.getCountFacetRelationCrawled()
                    new ProgressResult(topicRepository.findByDomainName(domainName).size(),
                    FragmentCrawler.getCountFacetCrawled() + FragmentCrawler.getCountFacetRelationCrawled(),
                            FragmentCrawler.getProgress(domain.getDomainId()) / 2));
        } else if ((domain != null)
                && (topics != null && topics.size() != 0)
                && (facets != null && facets.size() != 0)
                && (assembles == null || !AssembleCrawler.isCompleted())) {
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR5.getCode(), ResultEnum.TSPIDER_ERROR5.getMsg(),
//                    "已经爬取的主题数目为 " + topicRepository.findByDomainName(domainName).size() +
//                    " 已经爬取的分面数目为 " + facetRepository.findByDomainName(domainName).size() +
//                    "已经爬取的碎片数目为 " + AssembleCrawler.getAssembleCount() + ", 进度为" + AssembleCrawler.getProgress()
                    new ProgressResult(topicRepository.findByDomainName(domainName).size(),
                    facetRepository.findByDomainName(domainName).size(),
                    AssembleCrawler.getAssembleCount(),
                    AssembleCrawler.getProgress(domain.getDomainId()) / 2 + 0.5));
        } else {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),
                    "==========该课程的知识主题分面树已成功构建，且碎片爬取完毕==========");
        }


    }

    /**
     * 爬取一门课程：主题、分面、分面关系、碎片
     *
     * @param domain 课程
     */
    public static void constructTopicFacetTreeByDomainName(Domain domain) throws Exception {
        // 爬取并存储知识主题
        TopicCrawler.storeTopic(domain);
        // 爬取并存储知识主题对应的分面，形成知识主题分面树（不含碎片信息及知识主题间的认知关系）
        FragmentCrawler.storeFacetTreeByDomainName(domain);
        // 爬取上面爬到的所有分面下的所有碎片
        AssembleCrawler.storeAssembleByDomainName(domain);

    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public boolean isSpecialChar(String str) {
        String regEx = "[`~!@#$%^&:<>|\\[\\]/~！#￥%……&|【】‘；：。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 调用根据lhx师兄的接口，得到返回的爬取并抽取分面结果
     * @param domainName 课程名
     * @param isChineseOrNot 是否为中文
     * @param topicNameList 主题列表
     * @param isConstruct true为构造分面，false为查看构造状态
     * @return
     */
    public  Result facetExtraction(String domainName,List<String> topicNameList, Boolean isChineseOrNot, boolean isConstruct, int tryTime, long tmpId) throws URISyntaxException {

        if (isSpecialChar(topicNameList.get(0))) {
            // 数据异常，直接删除
            topicRepository.delete(tmpId);

            Log.log("数据异常，有特殊字符 ，主题名为 " + topicNameList.get(0));
            return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(), "数据异常，有特殊字符");
        }

        String facetConstruct = "http://maotoumao.xyz:5373/facet-construct";
        String facetConstructStatus = "http://maotoumao.xyz:5373/facet-construct-status";
//        String facetConstruct = "http://10.181.184.41:3747/facet-construct";
//        String facetConstructStatus = "http://10.181.184.41:3747/facet-construct-status";
        String url = isConstruct ? facetConstruct : facetConstructStatus;
        //使用Restemplate来发送HTTP请求
        RestTemplate restTemplate = new RestTemplate();
        // json对象
        JSONObject jsonObject = new JSONObject();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("className", domainName);
        params.add("language", (isChineseOrNot ? "zh" : "en"));
        for (int i = 0; i < topicNameList.size(); i++) {
            params.add("topicNames[" + i + "]", topicNameList.get(i));
        }

        // 发送post数据并返回数据
        //设置请求header 为 APPLICATION_FORM_URLENCODED
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//        // 请求体，包括请求数据 body 和 请求头 headers
//        HttpEntity httpEntity = new HttpEntity(params, headers);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        if (isConstruct) {
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            return facetExtraction(domainName, topicNameList, isChineseOrNot, false, 0, tmpId);
        }
        ResponseEntity<FacetResultVO> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, FacetResultVO.class);

        FacetResultVO facetResult = response.getBody();

//        System.out.println("running: " + facetResult.isRunning());
//        System.out.println("msg: " + facetResult.getMessage());
//        for (Map.Entry<String, List<String>> facet : ((Map<String, List<String>>) facetResult.getFacets()).entrySet()) {
//            System.out.println("topic: " + facet.getKey());
//            System.out.print("facets: ");
//            for (String s : facet.getValue()) {
//                System.out.print(s + " ");
//            }
//            System.out.println();
//        }

        if (facetResult.isRunning()) {
//            Log.log("========================="  + "分面爬取正在进行中" + "=========================");
            if (tryTime > 20) {
                // 数据异常，直接删除
                Log.log("数据异常，分面爬取接口调用错误 ，主题名为 " + topicNameList.get(0) + "错误为 " + facetResult.getMessage() + " ，已删除错误主题");
                topicRepository.delete(tmpId);

                return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(), facetResult.getMessage());

            }
            System.out.print("...分面爬取中...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(), facetResult.getMessage());
            return facetExtraction(domainName, topicNameList, isChineseOrNot, false, tryTime + 1, tmpId);
        }
        else {
            if (facetResult.getFacets() == null) {
                return facetExtraction(domainName, topicNameList, isChineseOrNot, true, 0, tmpId);
            } else {
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), facetResult.getMessage(), facetResult.getFacets());
            }
        }

    }


    public Result crawlEmptyData() throws Exception {
//        Optional<MissingRecord> first = null;
        synLock = 1;
//        while (missingRecordRepository.count() != 0) {

//            first = missingRecordRepository.findFirstBy();
//            first = missingRecordRepository.findFirstByType(1); // 1053349
//            if (!first.isPresent())
//                return ResultUtil.error(404, "失败", "已无缺失数据");
//
//            MissingRecord missingRecord = first.get();
//            int typeId = missingRecord.getType();

            // 先删除数据库中的缺失记录防止分布进行爬取时冲突
            // 若非分布式爬取，这个操作可以放在爬取之后防止出错
//            missingRecordRepository.deleteById(missingRecord.getId());

        if (missingRecordRepository.countByType(1) == 0)
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "空分面全部填充完成");

        CrawlerRunnable crawlerRunnable = new CrawlerRunnable();
        try {
            while (synLock < 0)
                Thread.sleep(10);
            crawler = new Thread(crawlerRunnable);
            synLock--;
            crawler.start();
        } catch (Exception e) {
            // 若出错了，将数据写回数据库
//                missingRecordRepository.save(new MissingRecord(typeId, missingRecord.getSpecificId()));

            e.printStackTrace();

        }

//        Thread.sleep(500);
//        while (synLock < 0)
//            Thread.sleep(10);
//        synLock--;
//        Thread crawler2 = new Thread(crawlerRunnable);
//        crawler2.start();
//
//        Thread.sleep(500);
//        while (synLock < 0)
//            Thread.sleep(10);
//        synLock--;
//        Thread crawler3 = new Thread(crawlerRunnable);
//        crawler3.start();

        // todo: 改成线程池
        int thread_num = 2;
        for (int i = 0; i < thread_num; i++) {
            Thread.sleep(5000);
            while (synLock < 0)
                Thread.sleep(10);
            synLock--;
            new Thread(crawlerRunnable).start();
        }


            // 先删除数据库中的缺失记录防止分布进行爬取时冲突
            // 若非分布式爬取，这个操作可以放在爬取之后防止出错
//            missingRecordRepository.deleteById(missingRecord.getId());

//        }

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "正在填充，详情查看/newSpiderFor2021/emptyCrawlerStatus");
    }

    public Result getEmptyCrawlerInfo() {
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), crawler.getState());
    }


    public Result crawlEmptyDomain(Long domainId, boolean isChinese) throws Exception {
        Domain domain = domainRepository.findByDomainId(domainId);
        this.isChinese = checkIsChinese(domain.getDomainName());
        TopicCrawler.setDomainLanguage();

        // 调用zd师兄接口
        constructTopicFacetTreeByDomainName(domain);

        // 此处应该加更多错误判断

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "大概好了吧");
    }

    public Result crawlEmptyTopic(Long topicId, boolean isChinese) throws Exception {
        Topic topic = topicRepository.findByTopicId(topicId);
        Domain parentDomain = domainRepository.findByDomainId(topic.getDomainId());
        this.isChinese = checkIsChinese(topic.getTopicName());
        List<String> topicList = new ArrayList<>();
        topicList.add(topic.getTopicName());

        // 调用lhx师兄接口

        Result result = facetExtraction(parentDomain.getDomainName(), topicList, this.isChinese, false, 0, topicId);

        for (Object value : ((LinkedHashMap) result.getData()).values()) {
            List<String> facetNames = (ArrayList<String>) value;
            for (String facetName : facetNames) {

                // !!!这里暂时只考虑了一级分面的情况
                if (facetRepository.findByTopicIdAndFacetName(topicId, facetName) == null) {
                    facetRepository.save(new Facet(facetName, 1, topicId, null));

                    // 然后爬取对应的空分面
                    crawlEmptyFacet(facetRepository.findByTopicIdAndFacetName(topicId, facetName).getFacetId());
                }
            }
        }

        return result;
    }

    public Result crawlEmptyFacet(Long facetId) throws Exception {
        Facet facet = facetRepository.findByFacetId(facetId);
        Topic parentTopic = topicRepository.findByTopicId(facet.getTopicId());
        if (parentTopic == null) {
            facetRepository.deleteByFacetId(facetId);
            return ResultUtil.success(ResultEnum.FACET_SEARCH_ERROR_4.getCode(), ResultEnum.FACET_SEARCH_ERROR_4.getMsg(), "error");
        }
        Domain parentDomain = domainRepository.findByDomainId(parentTopic.getDomainId());

        // 若无此课程，说明数据库中数据有问题，直接删掉该课程下全部主题，分面会在线程中自动删除
        if (parentDomain == null) {
            topicRepository.deleteByDomainId(parentTopic.getDomainId());
            Log.log(Thread.currentThread().getName() + " ================================= 该分面数据异常，无对应课程，已删除该课程id下的所有主题");
        }

        // 调用ljj师兄接口
        boolean canCrawled = AssembleCrawler.crawlAssembleByFacet(parentDomain, parentTopic, facet);

        // 如果在该分面下爬不到碎片，直接删了这个分面
        if (!canCrawled) {
            facetRepository.deleteByFacetId(facetId);
            // 删除分面后，若其父主题下没分面了，则把这个主题也删除
            if (!facetRepository.existsFacetByTopicId(parentTopic.getTopicId())) {
                topicRepository.delete(parentTopic.getTopicId());
            }
        }

        // 此处应该加更多错误判断

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "大概好了吧");
    }

    public static Boolean getIsChinese() {
        return isChinese;
    }

    public static void setIsChinese(Boolean isChinese) {
        NewSpiderService.isChinese = isChinese;
    }

    /**
     * 判断是否为中文
     * @param name
     * @return
     */
    public boolean checkIsChinese (String name) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(name);
        return m.find();
    }

    class CrawlerRunnable implements Runnable {
        private Optional<MissingRecord> first;
        private Long missingRecordId;
        private Long specificId;
        private int type;
        private boolean isChinese;

        public CrawlerRunnable() {
        }

        public CrawlerRunnable(Long specificId, int type) {
            this.specificId = specificId;
            this.type = type;
        }

        @Override
        public void run() {

            first = missingRecordRepository.findFirstByType(1); //

            this.missingRecordId = first.get().getId();

            // 先删除记录，防止多线程读冲突
            missingRecordRepository.deleteById(missingRecordId);
            synLock++;

            this.specificId = first.get().getSpecificId();
            this.type = first.get().getType();


            if (first.isPresent()) {

                switch (type) {
                    case 0:
                        Log.log("===============" + "找到空课程, id为" + specificId + ", 开始构建" + "===============");
                        try {
                            crawlEmptyDomain(specificId, true); // !!!先默认都为中文
                            Log.log("===============" + Thread.currentThread().getName() + "已完成工作，课程 " + specificId + " 下的主题爬取完毕");
                        } catch (Exception e) {
                            // 若出错，将记录写回数据库
                            missingRecordRepository.save(new MissingRecord(type, specificId));
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        Log.log("===============" + "找到空主题, id为" + specificId + ", 开始构建" + "===============");
                        try {
                            crawlEmptyTopic(specificId, true); // !!!先默认都为中文
                            Log.log("===============" + Thread.currentThread().getName() + "已完成工作，主题 " + specificId + " 下的分面爬取完毕");
                        } catch (URISyntaxException e) {
                            // 若出错，将记录写回数据库
                            missingRecordRepository.save(new MissingRecord(type, specificId));
                            e.printStackTrace();
                        } catch (Exception e) {
                            // 若出错，将记录写回数据库
                            missingRecordRepository.save(new MissingRecord(type, specificId));
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        Log.log("===============" + "找到空分面, id为" + specificId + ", 开始构建" + "===============");
                        try {
                            Result res = crawlEmptyFacet(specificId);
                            if (res.getCode() == ResultEnum.FACET_SEARCH_ERROR_4.getCode()) {
                                Log.log("===============" + Thread.currentThread().getName() + "已完成工作，分面 " + specificId + " 不存在对应主题，已将其删除");
                            } else {
                                Log.log("===============" + Thread.currentThread().getName() + "已完成工作，分面 " + specificId + " 下的碎片爬取完毕");
                            }

//                            missingRecordRepository.deleteById(missingRecordId);
                        } catch (Exception e) {

                            // 若出错，将记录写回数据库
                            missingRecordRepository.save(new MissingRecord(type, specificId));

                            e.printStackTrace();
                        }
                        break;
                }

                // 爬完一个空的后接着爬
//                try {
//                    crawlEmptyData();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                try {
                    Thread.sleep(500);
                    while (synLock < 0) {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synLock--;
                CrawlerRunnable crawlerRunnable = new CrawlerRunnable();
                crawler = new Thread(crawlerRunnable);
                try {
                    crawler.start();
                } catch (Exception e) {
                    // 若出错了，将数据写回数据库
//                missingRecordRepository.save(new MissingRecord(typeId, missingRecord.getSpecificId()));

                    e.printStackTrace();

                }
            }
        }
    }

}
