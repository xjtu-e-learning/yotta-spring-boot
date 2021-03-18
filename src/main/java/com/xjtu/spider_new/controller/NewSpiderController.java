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


}
