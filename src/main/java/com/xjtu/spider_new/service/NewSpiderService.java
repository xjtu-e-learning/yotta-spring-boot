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
import com.xjtu.facet.service.FacetService;
import com.xjtu.spider_new.common.FacetResultVO;
import com.xjtu.spider_new.common.NewSpiderRunnable;
import com.xjtu.spider_new.common.ProgressResult;
import com.xjtu.spider_new.domain.MissingRecord;
import com.xjtu.spider_new.repository.MissingRecordRepository;
import com.xjtu.spider_new.spiders.wikicn.*;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.topic.service.TopicService;
import com.xjtu.utils.Log;
import com.xjtu.utils.ResultUtil;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.SchemaOutputResolver;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 2021使用的爬虫
 *
 * @author 洪振杰
 * @date 2021年3月
 */
@Service
public class NewSpiderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Boolean isChinese;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private DomainService domainService;

    @Autowired
    private FacetService facetService;

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
        String regEx = "[`~!?@#$%^&:<>|\\[\\]/~！#￥%……&|【】‘；：。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 调用根据lhx师兄的接口，得到返回的爬取并抽取分面结果
     * @param domain 课程
     * @param isChineseOrNot 是否为中文
     * @param topicList 主题列表
     * @param isConstruct true为构造分面，false为查看构造状态
     * @return
     */
    public  Result facetExtraction(Domain domain,List<Topic> topicList, Boolean isChineseOrNot, boolean isConstruct, int tryTime) throws URISyntaxException, InterruptedException {

        String facetConstruct = "http://maotoumao.xyz:5373/facet-construct";
        String facetConstructStatus = "http://maotoumao.xyz:5373/facet-construct-status";
//        String facetConstruct = "http://10.181.184.41:3747/facet-construct";
//        String facetConstructStatus = "http://10.181.184.41:3747/facet-construct-status";
        String url = isConstruct ? facetConstruct : facetConstructStatus;
        String domainName = domain.getDomainName().trim();

        //使用Restemplate来发送HTTP请求
        RestTemplate restTemplate = new RestTemplate();
        // json对象
        JSONObject jsonObject = new JSONObject();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("className", domainName);
        params.add("language", (isChineseOrNot ? "zh" : "en"));
        int index = 0;

//        if (topicList.size() > 20) return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(), "空主题太多，暂不处理");

        boolean isDelete = false; // 记录是否删除了特殊字符的主题，若删除了要更新topicList
        for (int i = 0; i < topicList.size(); i++) {
            if (isSpecialChar(topicList.get(i).getTopicName())) {
                // 数据异常，直接删除
                topicRepository.delete(topicList.get(i).getTopicId());
                isDelete = true;

                Log.log("数据异常，有特殊字符 ，主题名为 " + topicList.get(i).getTopicName() + "，已删除该主题");
            }else {

                params.add("topicNames[" + index++ + "]", topicList.get(i).getTopicName());
            }
        }
        // 若删除了要更新topicList
        if (isDelete)   topicList = topicRepository.findEmptyTopicByDomainId(domain.getDomainId());

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
            Thread.sleep(2000);
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            Thread.sleep(2000);
            return facetExtraction(domain, topicList, isChineseOrNot, false, 0);
        }

        ResponseEntity<FacetResultVO> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, FacetResultVO.class);
        } catch (HttpStatusCodeException exception) {

            int statusCode = exception.getStatusCode().value();
            System.out.println(exception);

        }

        FacetResultVO facetResult = response.getBody();

        if (facetResult.isRunning()) {
//            Log.log("========================="  + "分面爬取正在进行中" + "=========================");
            if (tryTime % 10 == 0)
                Log.log("===============" + Thread.currentThread().getName() + ": " + facetResult.getMessage() + "===============");
            if (tryTime > 100) {
                // 数据异常
                Log.log("数据异常，分面爬取接口调用超时 ，主题名为 " + topicList.get(0).getTopicName() + "，课程名为" + domainName + "，错误为 " + facetResult.getMessage());
//                topicRepository.delete(tmpId);
//                missingRecordRepository.save(new MissingRecord(1, tmpId));

                return ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(), facetResult.getMessage());

            }
            Thread.sleep(5000);

//            ResultUtil.error(ResultEnum.TSPIDER_ERROR2.getCode(), ResultEnum.TSPIDER_ERROR2.getMsg(), facetResult.getMessage());
            return facetExtraction(domain, topicList, isChineseOrNot, false, tryTime + 1);
        } else {
            if (facetResult.getFacets() == null) {
                return facetExtraction(domain, topicList, isChineseOrNot, true, 0);
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
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "空主题全部填充完成");

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

        // todo: 改成线程池
        int thread_num = 0;
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

    class CrawlEmptyTopicRunnable implements Runnable {

        private List<Long> handleList;

        public CrawlEmptyTopicRunnable(List<Long> handleList) {
            this.handleList = handleList;
        }

        @Override
        public void run() {
            Log.log("===============" + Thread.currentThread().getName() + ": 具有空主题的课程数量为" + handleList.size() +
                    "，开始爬取===============");

            for (int i = 0; i < handleList.size(); i++) {
                Long domainId = handleList.get(i);

                Domain currDomain = domainRepository.findByDomainId(domainId);
                if (currDomain == null) continue;
                boolean domainChinese = checkIsChinese(currDomain.getDomainName()); // 记录课程是中文还是英文
                // 获取对应课程下的空主题列表
                List<Topic> rawEmptyTopics = topicRepository.findEmptyTopicByDomainId(domainId);
//                // 对中英文做个判断，与课程不一致的就排除
//                List<Topic> emptyTopics = rawEmptyTopics.stream()
//                        .filter((Topic t) -> checkIsChinese(t.getTopicName()) == domainChinese)
//                        .collect(Collectors.toList());

                Log.log("===============" + Thread.currentThread().getName() + ": 课程:" + currDomain.getDomainName()
                        + "下的空主题数量为" + rawEmptyTopics.size() + "，开始爬取===============");

                try {
                    new MultiCrawlerController().startCrawler(rawEmptyTopics, domainChinese, domainId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.log("===============" + Thread.currentThread().getName() + ": The progress of the domain crawler is: "
                        + (i+1) + "/" + handleList.size());

            }
            Log.log("===============" + Thread.currentThread().getName() + "爬取完毕，任务已结束");
        }
    }

    /**
     * 直接使用爬虫爬取填充空主题，不通过算法
     */
    public Result crawlEmptyTopicsByDomainWithoutAI(int min, int max) {

        // 首先，找到所有具有空主题的课程（空主题数必须小于250，250以上的算异常数据）
        // 然后，开启多个线程分别进行爬取

        List<BigInteger> rawDomainIds = topicRepository.findDomainIdWhichHasEmptyTopic(min, max);
        List<Long> domainIdWhichHasEmptyTopic = rawDomainIds.stream().map(s -> Long.parseLong(s.toString())).collect(Collectors.toList());
        new Thread(new CrawlEmptyTopicRunnable(domainIdWhichHasEmptyTopic), "[" + min + ", " + max + ") Thread").start();

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "开始爬取");
    }

    /**
     * 该方法调用lhx师兄接口，通过分面抽取算法得到空主题
     * @throws Exception
     */
    public void crawlEmptyTopicsByDomain() throws Exception {

        // 首先，找到所有具有空主题的课程（空主题数必须小于200，200以上的算异常数据）
        List<BigInteger> rawDomainIds = topicRepository.findDomainIdWhichHasEmptyTopic1();
        List<Long> domainIdWhichHasEmptyTopic = rawDomainIds.stream().map(s -> Long.parseLong(s.toString())).collect(Collectors.toList());

        Log.log("===============" + Thread.currentThread().getName() + ": 具有空主题的课程数量为" + domainIdWhichHasEmptyTopic.size() +
                "，开始分面抽取===============");

        for (Long domainId : domainIdWhichHasEmptyTopic) {

            // 异常课程，暂时跳过，其中有：
            // 787-中国人民解放军历史 （主题中英文混杂、有一些不相干主题、有wikipedia上无目录的主题）
            // 797-Genealogy (主题中英文混杂、有一些不相干主题)
            // 630-Calculus (未知原因)
            if (domainId == 787 || domainId == 797 || domainId == 630) continue;

            Domain currDomain = domainRepository.findByDomainId(domainId);
            // 获取对应课程下的空主题列表
            List<Topic> emptyTopics = topicRepository.findEmptyTopicByDomainId(domainId);

            // 建立一个空主题id与名字映射的图
            Map<String, Long> emptyTopicMapping = new HashMap<>();
            for (Topic emptyTopic : emptyTopics) {
                emptyTopicMapping.put(emptyTopic.getTopicName(), emptyTopic.getTopicId());

                // 同时，若topic_url是空的，帮忙爬取一下url
            }

            Log.log("===============" + Thread.currentThread().getName() + ": 课程:" + currDomain.getDomainName()
                    + "下空主题数量为" + emptyTopics.size() + "，开始分面抽取===============");

            // 调用lhx师兄接口
            Result result = facetExtraction(currDomain, emptyTopics,
                    checkIsChinese(currDomain.getDomainName().trim()), false, 0);

            if (result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
                Log.log("===============" + Thread.currentThread().getName() + ": 课程:" + currDomain.getDomainName()
                        + "下空主题构建完毕，开始碎片爬取===============");

                for (Object o : ((LinkedHashMap) result.getData()).entrySet()) {
                    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) o;
                    Long currTopicId = emptyTopicMapping.get(entry.getKey());
                    List<String> facetNames = entry.getValue();
                    for (String facetName : facetNames) {
                        System.out.println(domainId + " " + currTopicId + " " + facetName);
                        // !!!这里暂时只考虑了一级分面的情况
//                        if (facetRepository.findByTopicIdAndFacetName(currTopicId, facetName) == null) {
//                            facetRepository.save(new Facet(facetName, 1, currTopicId, null));
//                            // 然后爬取对应的空分面
//                            crawlEmptyFacet(facetRepository.findByTopicIdAndFacetName(currTopicId, facetName).getFacetId());
//                        }

                    }
                }
            } else {
                Log.log("===============" + Thread.currentThread().getName() + ": " + result.getData() + "===============");
            }

        }

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

    public Result deleteEmptyFacets() {
        // 删除时要排除人工构建的课程下的分面
        int[] artificial = new int[]{1,411,412,27,413,414,417,418,419,420,421,422,424,425,426,427,428,430,431,432,433,434,436,437,438,439,440,441,444,446,448,452,453,454,455,456,458,459,461,462,463,464,466,467,468,469,503,471,480,483,488,490,500,501,880};
        Set<Long> exclude = new HashSet<>();
        for (int i : artificial) {
            exclude.add(Long.valueOf(i));
        }

        List<BigInteger> rawEmptyFacetIds = facetRepository.findEmptyFacet();
        if (rawEmptyFacetIds == null) return ResultUtil.error(ResultEnum.FACET_DELETE_ERROR.getCode(), ResultEnum.FACET_DELETE_ERROR.getMsg(), "无空分面");
        List<Long> emptyFacetIds = rawEmptyFacetIds.stream().map(s -> Long.parseLong(s.toString())).collect(Collectors.toList());


        for (Long emptyFacetId : emptyFacetIds) {
            Facet facet = facetRepository.findByFacetId(emptyFacetId);
            if (facet == null) continue; // 可能该分面已在删除其父分面时删除
            Topic topic = topicRepository.findByTopicId(facet.getTopicId());

            if (topic != null && exclude.contains(topic.getDomainId()))
                continue;
            facetService.deleteFacetAndChildrenFacetCompleteByFacetId(emptyFacetId);
            logger.info("已删除id为" + emptyFacetId + "的分面");
        }

        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "删除成功");
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
//                        try {
//                            crawlEmptyTopic(specificId, true); // !!!先默认都为中文
//                            Log.log("===============" + Thread.currentThread().getName() + "已完成工作，主题 " + specificId + " 下的分面爬取完毕");
//                        } catch (URISyntaxException e) {
//                            // 若出错，将记录写回数据库
//                            missingRecordRepository.save(new MissingRecord(type, specificId));
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            // 若出错，将记录写回数据库
//                            missingRecordRepository.save(new MissingRecord(type, specificId));
//                            e.printStackTrace();
//                        }
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

                // 若还有空的就接着爬
                if (missingRecordRepository.countByType(2) != 0) {
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
                        e.printStackTrace();

                    }
                }
            }
        }
    }

}
