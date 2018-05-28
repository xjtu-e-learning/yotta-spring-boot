package com.xjtu.spider.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.spider.service.SpiderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 爬虫：碎片化知识采集
 * @author yangkuan
 * @date 2018/05/15 23:17
 */
@RestController
@RequestMapping("/spider")
public class SpiderController {

    @Autowired
    SpiderService spiderService;

    @ApiOperation(value = "webmagic爬取课程碎片", notes = "webmagic爬取课程碎片")
    @GetMapping("/crawlAssembles")
    public ResponseEntity crawlAssembles(){
        Result result = spiderService.crawlAssembles();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
