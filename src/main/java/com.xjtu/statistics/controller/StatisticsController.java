package com.xjtu.statistics.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.statistics.service.StatisticsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;

/**
 * API数据统计
 * @author yangkuan
 * @date 2018/03/21 19:09
 * */
@RequestMapping("/statistics")
@RestController
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    /**
     * API
     * 根据课程名，查询该课程下的统计数据
     * */
    @GetMapping("/getStatisticalInformationByDomainName")
    @ApiOperation(value = "根据课程名，查询该课程下的统计数据", notes = "根据课程名，查询该课程下的统计数据")
    public ResponseEntity getStatisticalInformationByDomainName(@RequestParam(name = "domainName") String domainName){
        Result result = statisticsService.findStatisticalInformationByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程名、主题名列表（以“，”分割的字符串），查询该课程下的碎片统计数据
     * */
    @PostMapping("/getAssembleDistributionByDomainNameAndTopicNames")
    @ApiOperation(value = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程下的碎片统计数据"
            , notes = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程下的碎片统计数据")
    public ResponseEntity getAssembleDistributionByDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            ,@RequestParam(name = "topicNamesSegmentedByComma") String topicNamesSegmentedByComma){
        Result result = statisticsService.findAssembleDistributionByDomainNameAndTopicNamesSplitedByComma(domainName,topicNamesSegmentedByComma);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程名、主题名列表（以“，”分割的字符串），查询该课程及主题下的碎片数据
     * */
    @PostMapping("/getAssemblesByDomainNameAndTopicNames")
    @ApiOperation(value = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程及主题下的碎片数据"
            , notes = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程及主题下的碎片数据")
    public ResponseEntity getAssemblesByDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            ,@RequestParam(name = "topicNamesSegmentedByComma") String topicNamesSegmentedByComma){
        Result result = statisticsService.findAssemblesByDomainNameAndTopicNamesSplitedByComma(domainName,topicNamesSegmentedByComma);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     * API
     * 根据课程名、主题名列表（以“，”分割的字符串）以及数据源、是否包含数据源，对碎片数据进行分词，统计词频信息
     * */
    @PostMapping("/getWordFrequencyBySourceNameAndDomainNameAndTopicNames")
    @ApiOperation(value = "根据课程名、主题名列表（以“，”分割的字符串）以及数据源、是否包含数据源，对碎片数据进行分词，统计词频信息"
            , notes = "根据课程名、主题名列表（以“，”分割的字符串）以及数据源、是否包含数据源，对碎片数据进行分词，统计词频信息")
    public ResponseEntity getWordFrequencyBySourceNameAndDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNamesSegmentedByComma") String topicNamesSegmentedByComma
            , @RequestParam(name = "sourceName") String sourceName
            , @RequestParam(name = "hasSourceName") boolean hasSourceName){
        Result result = statisticsService.findWordFrequencyBySourceNameAndDomainNameAndTopicNames(domainName
                , topicNamesSegmentedByComma, sourceName, hasSourceName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 统计所有课程的主题、分面、碎片数量分布信息
     */
    @ApiOperation(value = "统计所有课程的主题、分面、碎片数量分布信息"
            ,notes = "统计所有课程的主题、分面、碎片数量分布信息")
    @PostMapping("/getDomainDistribution")
    public ResponseEntity getDomainDistribution(){
        Result result = statisticsService.findDomainDistribution();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     * API
     * 根据关键字，查询相关相似的课程、主题和分面
     */
    @ApiOperation(value = "根据关键字，查询相关相似的课程、主题和分面"
            , notes = "根据关键字，查询相关相似的课程、主题和分面")
    @GetMapping("/queryKeyword")
    public ResponseEntity queryKeyword(@RequestParam(name = "keyword") String keyword){
        Result result = statisticsService.queryKeyword(keyword);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
