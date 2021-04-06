package com.xjtu.spider_new.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.spider_new.service.NewSpiderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * 2021使用的爬虫
 *
 * @author 洪振杰
 * @date 2021年3月
 */
@Api(description = "2021版爬虫使用接口")
@RestController
@RequestMapping("/newSpiderFor2021")
public class NewSpiderController {


    @Autowired
    private NewSpiderService SpiderService;

    @CrossOrigin
    @PostMapping("/spiderTopicAndFragmentByDomainName")
    @ApiOperation(value = "构建指定学科，课程下的所有主题分面树并爬取所有分面下碎片且实时返回数据", notes = "爬虫程序爬取指定学科，课程的主题、分面信息并存入数据库，再根据所有分面爬取碎片")
    public ResponseEntity spiderTopicFacetTreeByDomainName(@RequestParam(name = "subjectName") String subjectName,
                                                           @RequestParam(name = "domainName") String domainName,
                                                           @RequestParam(name = "isChineseOrNot") Boolean isChineseOrNot) throws Exception {
        Result result = SpiderService.TopicFacetTreeSpider(subjectName, domainName, isChineseOrNot);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @CrossOrigin
    @PostMapping("/buildFacetsByTopicList")
    @ApiOperation(value = "构建指定课程下的所有主题列表中的所有分面",
            notes = "构建指定课程下的所有主题列表中的所有分面")
    public ResponseEntity buildFacetsByTopicList(@RequestParam(name = "domainName") String domainName,
                                                 @RequestParam(name = "isChinese") Boolean isChinese,
                                                 @RequestParam(name = "topicNames") List<String> topicNames) throws URISyntaxException {
        Result result = NewSpiderService.facetExtraction(domainName, topicNames, isChinese, false);

        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @CrossOrigin
    @PostMapping("/fillEmptyTopic")
    @ApiOperation(value = "填充单个空主题",
            notes = "填充单个空主题")
    public ResponseEntity fillEmptyTopic(@RequestParam(name = "domainName") String domainName,
                                                 @RequestParam(name = "isChinese") Boolean isChinese,
                                                 @RequestParam(name = "topicId") Long topicId) throws Exception {
        Result result = SpiderService.crawlEmptyTopic(topicId, isChinese);

        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @CrossOrigin
    @PostMapping("/fillEmptyFacet")
    @ApiOperation(value = "填充单个空分面",
            notes = "填充单个空分面")
    public ResponseEntity fillEmptyFacet(@RequestParam(name = "facetId") Long facetId) throws Exception {

        Result result = SpiderService.crawlEmptyFacet(facetId);

        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @CrossOrigin
    @PostMapping("/fillEmptyDomain")
    @ApiOperation(value = "填充单个空课程",
            notes = "填充单个空课程")
    public ResponseEntity fillEmptyDomain(@RequestParam(name = "domainId") Long domainId,
                                          @RequestParam(name = "isChinese") Boolean isChinese) throws Exception {

        Result result = SpiderService.crawlEmptyDomain(domainId, isChinese);

        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @CrossOrigin
    @PostMapping("/fillEmpty")
    @ApiOperation(value = "找一个空的地方开始填充",
            notes = "找一个空的地方开始填充")
    public ResponseEntity fillEmpty() throws Exception {

        Result result = SpiderService.crawlEmptyData();

        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @CrossOrigin
    @PostMapping("/emptyCrawlerStatus")
    @ApiOperation(value = "查看爬取空数据线程的状态",
            notes = "查看爬取空数据线程的状态")
    public ResponseEntity emptyCrawlerStatus() throws Exception {

        Result result = SpiderService.getEmptyCrawlerInfo();

        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
