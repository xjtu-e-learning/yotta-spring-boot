package com.xjtu.spider_Assemble.controller;


import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.spider_Assemble.service.SpiderAssembleService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * spider assemble
 * 用于自动构建api的自动碎片爬取
 * @author mkx
 */

@RestController
@RequestMapping("/spiderAssemble")
public class SpiderAssembleController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SpiderAssembleService spiderService;

    @ApiOperation(value = "webmagic自动爬取课程碎片api", notes = "自动构建碎片，webmagic自动爬取课程碎片api")
    @PostMapping("/crawlAssemblesByDomainName")
    public ResponseEntity crawlAssembles(@RequestParam(name = "domainName") String domainName) {
        Result result = spiderService.startCrawlAssembles(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "统计课程碎片", notes = "统计课程碎片")
    @GetMapping("/countAssemblesByDomainName")
    public ResponseEntity countAssembles(@RequestParam(name = "domainName") String domainName){
        Result result = spiderService.countAssembles(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
