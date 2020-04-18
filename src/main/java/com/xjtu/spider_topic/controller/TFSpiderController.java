package com.xjtu.spider_topic.controller;


import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.spider_topic.service.TFSpiderService;
import com.xjtu.utils.Log;
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

    @PostMapping("/spiderTopicFacetByDomainName")
    @ApiOperation(value = "爬取指定课程的主题、分面信息", notes = "爬虫程序爬取指定学科，课程的主题、分面信息并存入数据库")
    public ResponseEntity spiderTopicFacetByDomainName(@RequestParam(name = "subjectName") String subjectName,
                                                       @RequestParam(name = "domainName") String domainName,
                                                       @RequestParam(name = "isChineseOrNot") Boolean isChineseOrNot) throws Exception {
        Result result = TFSpiderService.TFSpider(subjectName, domainName, isChineseOrNot);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            Log.log("\n\n\n==========该课程的知识主题分面树已经构建完成==========");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @PostMapping("/spiderFacetByDomainName")
    @ApiOperation(value = "按课程构建分面与分面关系", notes = "用于课程下主题构建结束后才构建分面的情况")
    public ResponseEntity spiderFacetByDomainName(@RequestParam(name = "domainName") String domainName) throws Exception{
        Result result = TFSpiderService.FSpider(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            Log.log("\n\n\n==========该课程的分面与分面关系已经构建完成==========");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("/spiderTopicFacetByTopicName")
    @ApiOperation(value = "按主题构建主题分面树", notes = "针对人工添加的新主题爬取对应的主题分面树并存入数据库")
    public ResponseEntity spiderTopicFacetByTopicName(@RequestParam(name = "domainName") String domainName,
                                                      @RequestParam(name = "topicName") String topicName) throws Exception {
        Result result = TFSpiderService.TSpider(domainName,topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            Log.log("\n\n\n==========该主题的分面与分面关系已经构建完成==========");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}