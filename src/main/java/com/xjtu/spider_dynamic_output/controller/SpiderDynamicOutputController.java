package com.xjtu.spider_dynamic_output.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.spider_dynamic_output.service.SpiderDynamicOutputService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 动态输出爬虫爬到的内容
 * @author ljj
 * @date 2020年11月
 * */

@RestController
@RequestMapping("/spiderDynamicOutput")
public class SpiderDynamicOutputController {

    @Autowired
    private SpiderDynamicOutputService spiderdOutputService;


    @ApiOperation(value = "输入课程名学科名、课程名，爬取并返回该课程下的主题列表", notes = "输入课程名学科名、课程名，爬取并返回该课程下的主题列表")
    @PostMapping("/spiderTopicBySubjectAndDomainName")
    public ResponseEntity spiderTopicBySubjectAndDomainName(@RequestParam(name="subjectName") String subjectName,
                                                            @RequestParam(name="domainName") String domainName
    ) throws Exception{
        Result result=spiderdOutputService.TopicSpider(subjectName,domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "输入课程名、主题名，启动爬虫", notes = "输入课程名、主题名，开启爬取该主题下的分面树和分面下的碎片爬虫")
    @PostMapping("/startSpiderFacetAssembleTreeByDomianAndTopicName")
    public ResponseEntity startSpiderFacetAssembleTreeByDomianAndTopicName(@RequestParam(name="domainName") String domainName,
                                                                      @RequestParam(name="topicName") String topicName
                                                                     ) throws Exception{
        Result result=spiderdOutputService.startFacetAssembleSpider(domainName,topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "输入课程名、主题名，启动增量爬虫", notes = "输入课程名、主题名，开启爬取该主题下的分面树和分面下的碎片爬虫")
    @PostMapping("/startIncrementalSpiderFacetAssembleTreeByDomianAndTopicName")
    public ResponseEntity startIncrementalSpiderFacetAssembleTreeByDomianAndTopicName(@RequestParam(name="domainName") String domainName,
                                                                      @RequestParam(name="topicName") String topicName
    ) throws Exception{
        Result result=spiderdOutputService.startIncrementFacetAssembleSpider(domainName,topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @ApiOperation(value = "输入课程名、主题名，查询爬虫的数据和爬虫状态", notes = "输入课程名、主题名，查询爬虫的数据和爬虫状态")
    @PostMapping("/spiderFacetAssembleTreeByDomianAndTopicName")
    public ResponseEntity spiderFacetAssembleTreeByDomianAndTopicName(@RequestParam(name="domainName") String domainName,
                                                                      @RequestParam(name="topicName") String topicName
    ) throws Exception{
        Result result=spiderdOutputService.getFacetAssemble(domainName,topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "输入课程名、主题名，查询增量爬虫的数据和爬虫状态", notes = "输入课程名、主题名，查询增量爬虫的数据和爬虫状态")
    @PostMapping("/incrementalSpiderFacetAssembleTreeByDomianAndTopicName")
    public ResponseEntity incrementalSpiderFacetAssembleTreeByDomianAndTopicName(@RequestParam(name="domainName") String domainName,
                                                                                 @RequestParam(name="topicName") String topicName
    ) throws Exception{
        Result result=spiderdOutputService.getIncrementFacetAssemble(domainName,topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "输入课程Id, 在CSDN上爬取对应所有分面的碎片", notes = "无")
    @PostMapping("/crawlAssemblesByDomainId")
    public ResponseEntity crawlAssemblesByDomainId(@RequestParam(name="domainId") Long domainId
    ) throws Exception{
        Result result= spiderdOutputService.crawlAssemblesByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
