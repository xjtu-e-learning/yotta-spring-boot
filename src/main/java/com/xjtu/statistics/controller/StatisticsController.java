package com.xjtu.statistics.controller;

import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.statistics.service.StatisticsService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API数据统计
 *
 * @author yangkuan
 * @date 2018/03/21 19:09
 */
@RequestMapping("/statistics")
@RestController
public class StatisticsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatisticsService statisticsService;

    @GetMapping("/getStatisticalInformation")
    @ApiOperation(value = "查询所有课程下的统计数据", notes = "查询所有课程下的统计数据")
    public ResponseEntity getStatisticalInformation() {
        Result result = statisticsService.findStatisticalInformation();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/getStatisticalInformationBySubjectName")
    @ApiOperation(value = "根据学科名，查询所有课程下的统计数据", notes = "根据学科名，查询所有课程下的统计数据")
    public ResponseEntity getStatisticalInformationBySubjectName(@RequestParam(name = "subjectName") String subjectName) {
        Result result = statisticsService.findStatisticalInformationBySubjectName(subjectName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程名，查询该课程下的统计数据
     */
    @GetMapping("/getStatisticalInformationByDomainName")
    @ApiOperation(value = "根据课程名，查询该课程下的统计数据", notes = "根据课程名，查询该课程下的统计数据")
    public ResponseEntity getStatisticalInformationByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = statisticsService.findStatisticalInformationByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程名、主题名，查询该主题下的统计数据
     */
    @GetMapping("/getStatisticalInformationByDomainNameAndTopicName")
    @ApiOperation(value = "根据课程名、主题名，查询该主题下的统计数据", notes = "根据课程名、主题名，查询该主题下的统计数据")
    public ResponseEntity getStatisticalInformationByDomainNameAndTopicName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicName") String topicName) {
        Result result = statisticsService.findStatisticalInformationByDomainNameAndTopicName(domainName, topicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/countSubjectAndDomain")
    @ApiOperation(value = "统计学科、课程数量", notes = "统计学科、课程数量")
    public ResponseEntity countSubjectAndDomain() {
        Result result = statisticsService.countSubjectAndDomain();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/updateStatistics")
    @ApiOperation(value = "数据统计并保存", notes = "数据统计并保存")
    public ResponseEntity updateStatistics() {
        Result result = statisticsService.updateStatistics();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程名、主题名列表（以“，”分割的字符串），查询该课程下的碎片统计数据
     */
    @PostMapping("/getAssembleDistributionByDomainNameAndTopicNames")
    @ApiOperation(value = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程下的碎片统计数据"
            , notes = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程下的碎片统计数据")
    public ResponseEntity getAssembleDistributionByDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNamesSegmentedByComma") String topicNamesSegmentedByComma) {
        Result result = statisticsService.findAssembleDistributionByDomainNameAndTopicNamesSplitedByComma(domainName, topicNamesSegmentedByComma);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程名、主题名列表（以“，”分割的字符串），查询该课程及主题下的碎片数据
     */
    @PostMapping("/getAssemblesByDomainNameAndTopicNames")
    @ApiOperation(value = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程及主题下的碎片数据"
            , notes = "根据课程名、主题名列表（以“，”分割的字符串），查询该课程及主题下的碎片数据")
    public ResponseEntity getAssemblesByDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNamesSegmentedByComma") String topicNamesSegmentedByComma) {
        Result result = statisticsService.findAssemblesByDomainNameAndTopicNamesSplitedByComma(domainName, topicNamesSegmentedByComma);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程名、主题名列表（以“，”分割的字符串）以及数据源、是否包含数据源，对碎片数据进行分词，统计词频信息
     */
    @PostMapping("/getWordFrequencyBySourceNameAndDomainNameAndTopicNames")
    @ApiOperation(value = "根据课程名、主题名列表（以“，”分割的字符串）以及数据源、是否包含数据源，对碎片数据进行分词，统计词频信息"
            , notes = "根据课程名、主题名列表（以“，”分割的字符串）以及数据源、是否包含数据源，对碎片数据进行分词，统计词频信息")
    public ResponseEntity getWordFrequencyBySourceNameAndDomainNameAndTopicNames(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "topicNamesSegmentedByComma") String topicNamesSegmentedByComma
            , @RequestParam(name = "sourceName") String sourceName
            , @RequestParam(name = "hasSourceName") boolean hasSourceName) {
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
            , notes = "统计所有课程的主题、分面、碎片数量分布信息")
    @PostMapping("/getDomainDistribution")
    public ResponseEntity getDomainDistribution() {
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
    public ResponseEntity queryKeyword(@RequestParam(name = "keyword") String keyword) {
        Result result = statisticsService.queryKeyword(keyword);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 统计碎片数量
     */
    @ApiOperation(value = "统计碎片数量"
            , notes = "统计碎片数量")
    @GetMapping("/countAssemble")
    public ResponseEntity countAssemble() {
        Result result = statisticsService.countAssemble();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 统计主题数量
     */
    @ApiOperation(value = "统计主题数量"
            , notes = "统计主题数量")
    @GetMapping("/countTopic")
    public ResponseEntity countTopic() {
        Result result = statisticsService.countTopic();
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    /**
     * API
     * 根据课程id，统计碎片数量
     */
    @ApiOperation(value = "根据课程id，统计碎片数量"
            , notes = "根据课程id，统计碎片数量")
    @GetMapping("/countAssembleByDomainId")
    public ResponseEntity countAssembleByDomainId(@RequestParam(name = "domainId") Long domainId) {
        Result result = statisticsService.countAssembleByDomainId(domainId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程id集合，统计碎片数量
     */
    @ApiOperation(value = "根据课程id集合，统计碎片数量"
            , notes = "根据课程id集合，统计碎片数量")
    @GetMapping("/countAssembleGroupByDomainIds")
    public ResponseEntity countAssembleGroupByDomainIds(@RequestParam(name = "domainIds") List<Long> domainIds) {
        Result result = statisticsService.countAssembleGroupByDomainIds(domainIds);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据主题id，统计碎片数量
     */
    @ApiOperation(value = "根据主题id，统计碎片数量"
            , notes = "根据主题id，统计碎片数量")
    @GetMapping("/countAssembleByTopicId")
    public ResponseEntity countAssembleByTopicId(@RequestParam(name = "topicId") Long topicId) {
        Result result = statisticsService.countAssembleByTopicId(topicId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据主题id集合，统计碎片数量
     */
    @ApiOperation(value = "根据主题id集合，统计碎片数量"
            , notes = "根据主题id集合，统计碎片数量")
    @GetMapping("/countAssembleGroupByTopicIds")
    public ResponseEntity countAssembleGroupByTopicIds(@RequestParam(name = "topicIds") List<Long> topicIds) {
        Result result = statisticsService.countAssembleGroupByTopicIds(topicIds);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据主题id集合，统计一级分面数量
     */
    @ApiOperation(value = "根据主题id集合，统计一级分面数量"
            , notes = "根据主题id集合，统计一级分面数量")
    @GetMapping("/countFirstLayerFacetGroupByTopicIds")
    public ResponseEntity countFirstLayerFacetGroupByTopicIds(@RequestParam(name = "topicIds") List<Long> topicIds) {
        Result result = statisticsService.countFirstLayerFacetGroupByTopicIds(topicIds);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 根据课程id集合，统计主题数量
     */
    @ApiOperation(value = "根据课程id集合，统计主题数量"
            , notes = "根据课程id集合，统计主题数量")
    @GetMapping("/countTopicGroupByDomainIds")
    public ResponseEntity countTopicGroupByDomainIds(@RequestParam(name = "domainIds") List<Long> domainIds) {
        Result result = statisticsService.countTopicGroupByDomainIds(domainIds);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
