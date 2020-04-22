package com.xjtu.spider_topic.controller;


import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.spider_topic.service.TFSpiderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 知识主题爬虫；知识主题抽取
 *
 * @author 张铎
 * @date 2019年7月
 */
@RestController
@RequestMapping("/spiderTopicFacet")
public class TFSpiderController {

    @Autowired
    private TFSpiderService TFSpiderService;

    @PostMapping("/spiderTopicFacetTreeByDomainName")
    @ApiOperation(value = "构建指定学科，课程下的所有主题分面树", notes = "爬虫程序爬取指定学科，课程的主题、分面信息并存入数据库")
    public ResponseEntity spiderTopicFacetTreeByDomainName(@RequestParam(name = "subjectName") String subjectName,
                                                           @RequestParam(name = "domainName") String domainName,
                                                           @RequestParam(name = "isChineseOrNot") Boolean isChineseOrNot) throws Exception {
        Result result = TFSpiderService.TopicFacetTreeSpider(subjectName, domainName, isChineseOrNot);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @GetMapping("/spiderFacetTreeByDomainName")
    @ApiOperation(value = "已完成知识主题抽取后，构建指定课程在的分面树", notes = "用于课程下主题构建结束后构建分面的情况")
    public ResponseEntity spiderFacetByDomainName(@RequestParam(name = "domainName") String domainName) throws Exception{
        Result result = TFSpiderService.FacetTreeSpider(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @GetMapping("/spiderSingleTopicFacetTreeByTopicName")
    @ApiOperation(value = "构建指定课程，主题下的一棵主题分面树", notes = "针对人工添加的新主题爬取对应的主题分面树并存入数据库")
    public ResponseEntity spiderSingleTopicFacetByTopicName(@RequestParam(name = "domainName") String domainName,
                                                      @RequestParam(name = "topicName") String topicName) throws Exception {
        Result result = TFSpiderService.SingleTopicFacetTreeSpider(domainName,topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}