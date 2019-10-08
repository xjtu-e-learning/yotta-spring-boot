package com.xjtu.spider_topic.controller;


import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.spider_topic.service.TSpiderService;
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
public class TSpiderController {

    @Autowired
    private TSpiderService tSpiderService;

    @PostMapping("/spiderTopicFacetByDomainName")
    @ApiOperation(value = "爬虫程序爬取指定课程的主题、分面信息并存入数据库", notes = "爬虫程序爬取指定学科的主题、分面信息并存入数据库")
    public ResponseEntity spiderTopicFacetByDomainName(@RequestParam(name = "subjectName") String subjectName,
                                                       @RequestParam(name = "domainName") String domainName,
                                                       @RequestParam(name = "isChineseOrNot") Boolean isChineseOrNot) throws Exception {
        Result result = tSpiderService.TSpider(subjectName, domainName, isChineseOrNot);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}